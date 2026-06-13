# Kantin Kampus - Catalog & Order Service

Sistem backend kantin kampus yang terdiri dari 2 microservice independen dengan database terpisah:

| Service | Port | Database | Tanggung Jawab |
|---|---|---|---|
| `catalog_service` | 8081 | `catalog_db` | Mengelola data produk (CRUD, stok, status) |
| `order_service` | 8082 | `order_db` | Mengelola order pelanggan, berkomunikasi ke `catalog_service` via HTTP |

## Arsitektur

- Setiap service punya database PostgreSQL sendiri — **tidak ada akses lintas database secara langsung**.
- `order_service` memvalidasi produk dan menyesuaikan stok di `catalog_service` melalui **HTTP (Feign Client)**, bukan query database langsung.
- Setiap endpoint diamankan dengan **HTTP Basic Auth** (Spring Security + `InMemoryUserDetailsManager`).
- Error response konsisten dalam format JSON (`ApiError`) tanpa stack trace, ditangani oleh `GlobalExceptionHandler`.

## Cara Menjalankan

### 1. Siapkan Database

Buat 2 database PostgreSQL (contoh menggunakan PostgreSQL lokal di port 5432):

```sql
CREATE DATABASE catalog_db;
CREATE DATABASE order_db;
```

Sesuaikan kredensial di masing-masing `src/main/resources/application.properties` jika berbeda dari `postgres` / `postgres`.

### 2. Jalankan catalog_service

```bash
cd catalog_service
./mvnw spring-boot:run
```

Service berjalan di `http://localhost:8081`.

### 3. Jalankan order_service

```bash
cd order_service
./mvnw spring-boot:run
```

Service berjalan di `http://localhost:8082`.

> **Penting**: jalankan `catalog_service` terlebih dahulu sebelum `order_service`, karena pembuatan order memerlukan komunikasi HTTP ke `catalog_service`.

## Kredensial (Basic Auth)

| Role | Username | Password | Catatan |
|---|---|---|---|
| Admin | `admin` | `admin123` | Akses penuh ke semua endpoint & semua order |
| Customer A | `user@example.com` | `password` | Hanya bisa akses order miliknya sendiri |
| Customer B | `user2@example.com` | `password` | Digunakan untuk testing otorisasi lintas user |

## Business Rules Singkat

### Catalog Service

- `sku` unik (tidak boleh duplikat).
- `name` wajib diisi, `price` harus > 0, `stock` harus >= 0.
- `status` default `ACTIVE`. Produk `INACTIVE` tidak dapat dipesan.

### Order Service

- `customerName` wajib, `customerEmail` wajib dan harus format email valid.
- Minimal 1 item per order, `quantity` minimal 1.
- Saat membuat order:
  1. Validasi produk & stok ke `catalog_service` (HTTP GET).
  2. Kurangi stok produk di `catalog_service` (HTTP PATCH).
  3. Simpan order dengan snapshot nama & harga produk saat itu.
- Status order: `PENDING` → `PAID` atau `CANCELLED`. Hanya order `PENDING` yang bisa di-pay/cancel.
- `cancel` mengembalikan stok produk ke `catalog_service`.
- User biasa hanya bisa melihat/mengakses order miliknya sendiri (dicocokkan via email). Admin bisa akses semua order.

## Testing dengan Postman

Import file [`postman_collection.json`](postman_collection.json) ke Postman. Koleksi ini berisi 3 folder:

1. **Catalog Service** — CRUD produk (Create, List, Detail, Update Stock, Update Status).
2. **Order Service** — Create Order, List/Get Order, Pay, Cancel.
3. **Authorization Test (403)** — skenario membuktikan user tidak bisa mengakses order milik user lain:
   1. Customer A membuat order → `orderId` otomatis tersimpan.
   2. Customer B mencoba `GET /api/orders/{orderId}` → harus mendapat `403 Forbidden`.
   3. Admin mencoba `GET /api/orders/{orderId}` yang sama → harus berhasil `200 OK`.

### Urutan testing yang disarankan

1. Jalankan **Create Product** (folder Catalog Service) untuk membuat produk — `productId` otomatis tersimpan ke collection variable.
2. Jalankan **Create Order** (folder Order Service) menggunakan `productId` tersebut.
3. Coba **Pay Order** atau **Cancel Order** dan cek perubahan stok di **Get Product Detail**.
4. Jalankan folder **Authorization Test (403)** untuk membuktikan isolasi data antar user.

## Unit Test

Jalankan unit test (JUnit + Mockito) di masing-masing service:

```bash
cd catalog_service
./mvnw test
```

Test yang tersedia (`ProductServiceTest`):
- `create_shouldThrowConflict_whenSkuAlreadyExists` — memastikan SKU duplikat ditolak dengan `409 CONFLICT`.
- `adjustStock_shouldThrowBadRequest_whenResultingStockIsNegative` — memastikan pengurangan stok yang membuat stok negatif ditolak dengan `400 BAD_REQUEST` dan stok tidak berubah.

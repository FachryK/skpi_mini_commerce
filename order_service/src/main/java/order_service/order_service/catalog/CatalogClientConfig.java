package order_service.order_service.catalog;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CatalogClientConfig {
    @Bean
    public RequestInterceptor catalogAuthInterceptor(
            @Value("${catalog.service.username}") String username,
            @Value("${catalog.service.password}") String password
    ) {
        String token = Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        return template -> template.header("Authorization", "Basic " + token);
    }
}

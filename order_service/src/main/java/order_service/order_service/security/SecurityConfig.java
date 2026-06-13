package order_service.order_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${app.security.users.admin.username}") String adminUsername,
            @Value("${app.security.users.admin.password}") String adminPassword,
            @Value("${app.security.users.customer.username}") String customerUsername,
            @Value("${app.security.users.customer.password}") String customerPassword,
            @Value("${app.security.users.customer2.username}") String customer2Username,
            @Value("${app.security.users.customer2.password}") String customer2Password
    ) {
        return new InMemoryUserDetailsManager(
                User.withUsername(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .roles("ADMIN")
                        .build(),
                User.withUsername(customerUsername)
                        .password(passwordEncoder.encode(customerPassword))
                        .roles("USER")
                        .build(),
                User.withUsername(customer2Username)
                        .password(passwordEncoder.encode(customer2Password))
                        .roles("USER")
                        .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

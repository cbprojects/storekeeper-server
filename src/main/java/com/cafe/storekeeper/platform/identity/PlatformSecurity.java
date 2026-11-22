package com.cafe.storekeeper.platform.identity;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class PlatformSecurity {
    @Bean PasswordEncoder passwords() { return new BCryptPasswordEncoder(12); }
    @Bean org.springframework.security.core.userdetails.UserDetailsService noGeneratedUsers() {
        return username -> { throw new org.springframework.security.core.userdetails.UsernameNotFoundException("Use platform identity"); };
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, AccountRepository accounts,
            ObjectProvider<ClientRegistrationRepository> clients, GoogleIdentity google,
            @Value("${storekeeper.allowed-origins:http://localhost:4200}") String origins,
            @Value("${storekeeper.public-url:http://localhost:4200}") String publicUrl,
            @Value("${storekeeper.mail.enabled:true}") boolean mailEnabled) throws Exception {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::strip).toList());
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Content-Type", "X-CSRF-TOKEN", "X-Business-Id", "Idempotency-Key"));
        cors.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        http.cors(c -> c.configurationSource(source))
            .csrf(c -> c.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/actuator/health", "/actuator/health/**", "/api/v1/catalog", "/error").permitAll()
                .requestMatchers("/api/v1/auth/options", "/api/v1/auth/session", "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/verify", "/api/v1/auth/resend", "/api/v1/auth/forgot", "/api/v1/auth/reset", "/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().denyAll())
            .requestCache(c -> c.disable())
            .exceptionHandling(e -> e.authenticationEntryPoint((req,res,ex) -> res.sendError(401))
                .accessDeniedHandler((req,res,ex) -> res.sendError(403)))
            .logout(l -> l.logoutUrl("/api/v1/auth/logout").invalidateHttpSession(true).clearAuthentication(true)
                .deleteCookies("JSESSIONID").logoutSuccessHandler((req,res,auth) -> res.setStatus(204)))
            .addFilterBefore(new SessionVersionFilter(accounts, mailEnabled), AnonymousAuthenticationFilter.class);
        if (clients.getIfAvailable() != null) {
            http.oauth2Login(o -> o.userInfoEndpoint(u -> u.oidcUserService(google))
                .successHandler(new GoogleLoginHandler(google,publicUrl))
                .failureHandler(new org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler(publicUrl + "/#/account/login?error=google")));

        }
        return http.build();
    }
}

package cz.unicorn.fitnesssystem.config;

import cz.unicorn.fitnesssystem.auth.SignatureAuthFilter;
import cz.unicorn.fitnesssystem.auth.SignatureVerifier;
import cz.unicorn.fitnesssystem.auth.TrainerSessionCookie;
import cz.unicorn.fitnesssystem.auth.TrainerSessionFilter;
import cz.unicorn.fitnesssystem.constant.ApiPaths;
import cz.unicorn.fitnesssystem.service.MemberAuthService;
import cz.unicorn.fitnesssystem.service.TrainerSessionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig {

    private final HandlerExceptionResolver exceptionResolver;

    public SecurityConfig(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MemberAuthService memberAuthService,
                                                   SignatureVerifier signatureVerifier, AppProperties properties,
                                                   TrainerSessionService trainerSessionService,
                                                   TrainerSessionCookie trainerSessionCookie)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex))
                        .accessDeniedHandler((req, res, ex) -> exceptionResolver.resolveException(req, res, null, ex)))
                .addFilterBefore(new SignatureAuthFilter(memberAuthService, signatureVerifier, exceptionResolver, properties),
                        AnonymousAuthenticationFilter.class)
                .addFilterBefore(new TrainerSessionFilter(trainerSessionService, trainerSessionCookie, exceptionResolver),
                        AnonymousAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ApiPaths.PING).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiPaths.MEMBERS).permitAll()
                        .requestMatchers(ApiPaths.MEMBER_API, ApiPaths.MEMBER_API + "/**", ApiPaths.INVITES + "/**")
                        .hasRole("MEMBER")
                        .requestMatchers(HttpMethod.POST, ApiPaths.TRAINER_PUBLIC.toArray(String[]::new)).permitAll()
                        .requestMatchers(ApiPaths.TRAINER_API + "/**").hasRole("TRAINER")
                        .anyRequest().denyAll());
        return http.build();
    }
}
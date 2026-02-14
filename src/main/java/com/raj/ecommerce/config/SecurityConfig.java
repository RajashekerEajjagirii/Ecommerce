package com.raj.ecommerce.config;

import com.raj.ecommerce.constants.Constants;
import com.raj.ecommerce.security.JwtFilter;
import com.raj.ecommerce.security.JwtTokenProvider;
import com.raj.ecommerce.security.UserInfoDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserInfoDetailsService userService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserInfoDetailsService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(Constants.PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(Constants.SWAGGER_WHITELIST).permitAll()
                        // UserInfoDetailsService currently creates authorities like "ADMIN"/"USER"
                        // (not "ROLE_ADMIN"/"ROLE_USER"), so use hasAuthority(...) here.
                        .requestMatchers(Constants.USER_ACCESS_ENDPOINTS).hasAuthority("USER")
//                        .requestMatchers(Constants.ADMIN_ACCESS_ENDPOINTS).hasAuthority("ADMIN")
                        .requestMatchers(Constants.ADMIN_ACCESS_ENDPOINTS).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtTokenProvider, userService), UsernamePasswordAuthenticationFilter.class);

        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder());
        http.authenticationManager(authBuilder.build());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

package com.payment.test.config;

import com.payment.test.payload.response.CustomAccessDenied;
import com.payment.test.security.AuthEntryPointJwt;
import com.payment.test.security.AuthTokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfiguration {
    @Autowired
    AuthEntryPointJwt authEntryPointJwt;
    @Autowired
    AuthTokenFilter authTokenFilter;
    @Autowired
    CustomAccessDenied customAccessDenied;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("[SecurityFilterChain::start]");
        httpSecurity.cors().and().csrf().disable()
                .exceptionHandling().accessDeniedHandler(customAccessDenied).authenticationEntryPoint(authEntryPointJwt).and()
                .authorizeRequests().antMatchers("/auth/**").permitAll();
        httpSecurity.headers().frameOptions().sameOrigin();

        httpSecurity.cors().and().csrf().disable()
                        .authorizeRequests().anyRequest()
                        .authenticated().and().exceptionHandling().authenticationEntryPoint(authEntryPointJwt)
                        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        log.info("[SecurityFilterChain::end]");

        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDenied();
    }
}

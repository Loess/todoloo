package com.loess.todoloo.config;

import com.loess.todoloo.model.auth.filters.CustomAuthenticationFilter;
import com.loess.todoloo.model.auth.filters.CustomAuthorizationFilter;
import com.loess.todoloo.model.enums.Role;
import com.loess.todoloo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final String[] SWAGGER_ENDPOINT = {
            "/**swagger**/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private static final String[] WHITELIST = {
            "/h2-console/**/**",
            "/docs/**",
            "/csrf/**",
            "/webjars/**"
    };

    @Order(1)
    @Configuration
    public class SwaggerConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("itmo")
                    .password(passwordEncoder().encode("itmo"))
                    .authorities("ROLE_USER");
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers().antMatchers(SWAGGER_ENDPOINT)
                    .and()
                    .authorizeRequests().anyRequest().hasAuthority("ROLE_USER")
                    .and()
                    .httpBasic();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Order(2)
    @Configuration
    @RequiredArgsConstructor
    public static class RestConfiguration extends WebSecurityConfigurerAdapter {

        private final UserDetailsService userDetailsService;
        private final UserService userService;
        private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManager() throws Exception {
            return super.authenticationManager();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            CustomAuthenticationFilter customAuthenticationFilter =
                    new CustomAuthenticationFilter(authenticationManager(),userService);
            customAuthenticationFilter.setFilterProcessesUrl("/api/login");
            customAuthenticationFilter.setFilterProcessesUrl("/api/register");
            customAuthenticationFilter.setFilterProcessesUrl("/api/token/refresh");

            http
                    .cors()
                    .and()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    //.antMatchers(HttpMethod.GET, "/**").permitAll()
                    .antMatchers(PUT, "/users/new").permitAll()
                    .antMatchers("/api/login/**", "/api/token/refresh/**", "/api/register/**").permitAll()
                    .antMatchers(SWAGGER_ENDPOINT).permitAll()
                    .antMatchers(GET, "/**").hasAnyAuthority("KID", "PARENT")
                    .antMatchers(PUT, "/**").hasAnyAuthority("KID", "PARENT")
                    .antMatchers(PATCH, "/**").hasAnyAuthority("KID", "PARENT")
                    .antMatchers(POST, "/**").hasAnyAuthority("KID", "PARENT")
                    .antMatchers(DELETE, "/**").hasAnyAuthority("KID", "PARENT")
                    //.antMatchers(DELETE, "/**").hasAnyRole(Role.KID.name(), Role.PARENT.name())
                    .anyRequest().authenticated().and()
                    .addFilterAfter(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        }

        @Override
        public void configure(WebSecurity web) throws Exception { //WHITELIST
            web
                    .ignoring()
                    .antMatchers(WHITELIST)
                    .antMatchers();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
            return source;
        }
    }

}

package com.longlz.taskboard.config;

import com.longlz.taskboard.security.jwt.AuthEntryPointJwt;
import com.longlz.taskboard.security.jwt.AuthTokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // 启用方法级权限注解
@AllArgsConstructor
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;

    // 密码加密器
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt算法加密
    }

    // 安全过滤器链（核心配置）
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 跨域与CSRF配置
                .cors(Customizer.withDefaults()) // 启用默认CORS配置
                .csrf(csrf -> csrf.disable()) // API服务禁用CSRF

                // 异常处理（JWT专用入口）
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(unauthorizedHandler)
                )

                // 会话管理（无状态）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT无状态必备
                )

                // 授权规则（Lambda DSL风格）
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**", "/api/test/**").permitAll() // 开放认证与测试接口
                        .anyRequest().authenticated() // 其他请求需认证
                )

                // 添加JWT过滤器
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class); // 优先级高于默认认证

        return http.build();
    }

    // 认证管理器（支持JWT与表单登录）
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager(); // 自动关联UserDetailsService
    }
}
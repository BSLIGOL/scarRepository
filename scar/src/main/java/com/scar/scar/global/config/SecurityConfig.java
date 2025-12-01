package com.scar.scar.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scar.scar.global.security.CustomUserDetails;
import com.scar.scar.user.service.CustomUserDetailService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService userDetailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 보안 설정 비활성화 (REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. CORS 설정 (WebMvcConfig에서 설정)
                .cors(cors -> cors.configure(http))
                // 3. HTTP 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/studies/create").authenticated()
                        .requestMatchers("/applications/*/apply").authenticated()
                        .anyRequest().permitAll())
                // 4. 로그인 설정 (JSON 방식)
                .formLogin(form -> form
                        .loginProcessingUrl("/login") // 로그인 URL (POST /login)
                        .usernameParameter("email") // 로그인 ID 파라미터 (email)
                        .passwordParameter("password") // 로그인 비밀번호 파라미터 (password)
                        .successHandler((request, response, authentication) -> {
                            // 로그인 성공 시 JSON 응답 반환
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                            Map<String, Object> data = new HashMap<>();
                            data.put("id", userDetails.getUser().getId());
                            data.put("email", userDetails.getUsername());
                            data.put("nickName", userDetails.getNickName());

                            new ObjectMapper().writeValue(response.getWriter(), data);
                        })
                        .failureHandler((request, response, exception) -> {
                            // 로그인 실패 시 JSON 응답 반환
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            Map<String, String> data = new HashMap<>();
                            data.put("error", "로그인 실패: " + exception.getMessage());

                            new ObjectMapper().writeValue(response.getWriter(), data);
                        })
                        .permitAll())
                // 5. 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                // 6. 세션 관리 설정
                .sessionManagement(session -> session
                        // 세션 고정 공격 보호 (로그인 시 세션 ID 변경)
                        .sessionFixation().changeSessionId()
                        // 동시 세션 제어
                        .maximumSessions(1) // 최대 허용 세션 수 1개
                        .maxSessionsPreventsLogin(false) // 동시 로그인 차단 여부 (false: 기존 세션 만료)
                        .expiredSessionStrategy(event -> {
                            var response = event.getResponse();
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, String> data = new HashMap<>();
                            data.put("error", "세션이 만료되었습니다. 다시 로그인해주세요.");
                            new ObjectMapper().writeValue(response.getWriter(), data);
                        }));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

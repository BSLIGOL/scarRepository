package com.scar.scar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scar.scar.security.CustomUserDetails;
import com.scar.scar.service.CustomUserDetailService;
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
                // 1. CSRF 비활성화 (REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. CORS 활성화 (WebMvcConfig와 연동)
                .cors(cors -> cors.configure(http))
                // 3. 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/studies/create").authenticated()
                        .requestMatchers("/applications/*/apply").authenticated()
                        .anyRequest().permitAll())
                // 4. 로그인 설정 (JSON 응답)
                .formLogin(form -> form
                        .loginProcessingUrl("/login") // 프론트에서 POST /login으로 요청
                        .usernameParameter("email") // 프론트에서 보내는 필드명 (email)
                        .passwordParameter("password") // 프론트에서 보내는 필드명 (password)
                        .successHandler((request, response, authentication) -> {
                            // 로그인 성공 시 JSON 반환
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
                            // 로그인 실패 시 JSON 반환
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
                // 6. 세션 관리 정책
                .sessionManagement(session -> session
                        // 세션 고정 공격 방지 (로그인 시 세션 ID 변경)
                        .sessionFixation().changeSessionId()
                        // 동시 세션 제어
                        .maximumSessions(1) // 사용자당 최대 1개 세션만 허용
                        .maxSessionsPreventsLogin(false) // 신규 로그인 시 기존 세션 만료
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
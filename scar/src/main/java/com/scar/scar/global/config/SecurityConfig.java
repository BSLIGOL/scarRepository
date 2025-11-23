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
                // 1. CSRF ?????????濡?씀?濾???(REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. CORS ???????(WebMvcConfig?? ??????熬곣몿???
                .cors(cors -> cors.configure(http))
                // 3. ???????????關?쒎첎?嫄???
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/studies/create").authenticated()
                        .requestMatchers("/applications/*/apply").authenticated()
                        .anyRequest().permitAll())
                // 4. ????????????關?쒎첎?嫄???(JSON ???????
                .formLogin(form -> form
                        .loginProcessingUrl("/login") // ??????꾩룆梨띰쭕??縕????耀붾굝????????POST /login????????????濾???
                        .usernameParameter("email") // ??????꾩룆梨띰쭕??縕????耀붾굝?????????????怨뺤른???????????꾩룆梨띰쭕???????(email)
                        .passwordParameter("password") // ??????꾩룆梨띰쭕??縕????耀붾굝?????????????怨뺤른???????????꾩룆梨띰쭕???????(password)
                        .successHandler((request, response, authentication) -> {
                            // ?????????????濡?씀?????⑤슢?⑶큺????JSON ?????諛몃마嶺뚮??????
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
                            // ????????????????쇰뮝????JSON ?????諛몃마嶺뚮??????
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            Map<String, String> data = new HashMap<>();
                            data.put("error", "????????????????쇰뮝?? " + exception.getMessage());

                            new ObjectMapper().writeValue(response.getWriter(), data);
                        })
                        .permitAll())
                // 5. ????????????꾩룆梨띰쭕??????關?쒎첎?嫄???
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                // 6. ??耀붾굝?????????????⑤㈇??????耀붾굝????????
                .sessionManagement(session -> session
                        // ??耀붾굝??????????????????????????諛몃마??潁뺛깺苡? (????????????耀붾굝???????ID ?????怨뺤른????
                        .sessionFixation().changeSessionId()
                        // ???????????耀붾굝?????????????
                        .maximumSessions(1) // ??????????遺얘턁??????傭? 1????耀붾굝????????????????關?쒎첎?嫄??怨룸쵂??
                        .maxSessionsPreventsLogin(false) // ?????壤굿?????????????????????????耀붾굝?????????遺얘턁????????
                        .expiredSessionStrategy(event -> {
                            var response = event.getResponse();
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, String> data = new HashMap<>();
                            data.put("error", "??耀붾굝??????????遺얘턁????????????????? ????????꾨굴??????????耀붾굝????癲ル슢??㎖?밤뀋??轅붽틓??????????");
                            new ObjectMapper().writeValue(response.getWriter(), data);
                        }));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

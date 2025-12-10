package com.scar.scar.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection; // Added
import java.util.Date;
import java.util.stream.Collectors;

import com.scar.scar.global.security.CustomUserDetails;
import com.scar.scar.user.domain.User;
import com.scar.scar.user.domain.GlobalRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String COOKIE_NAME = "accessToken";
    private final Key key;
    private final long tokenValidityInMilliseconds;

    private final boolean cookieSecure;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            @Value("${jwt.cookie-secure}") boolean cookieSecure) {
        if ("defaultSecretKeyShouldBeLongEnoughToPreventErrors1234567890".equals(secret)) {
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }

        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;

        this.cookieSecure = cookieSecure;
    }

    // 토큰 생성
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        CustomUserDetails userDetails = (CustomUserDetails) authentication
                .getPrincipal(); // Casting principal to CustomUserDetails

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder().setSubject(authentication.getName()) // email
                .claim(AUTHORITIES_KEY, authorities).claim("userId", userDetails.getUser().getId()) // user id
                .claim("nickName", userDetails.getUser().getNickName()) // nickname
                .signWith(key, SignatureAlgorithm.HS256).setExpiration(validity).compact();
    }

    // 쿠키 생성 (HttpOnly)
    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(tokenValidityInMilliseconds / 1000)
                .sameSite("Lax")
                .build();
    }

    // 쿠키 삭제 (로그아웃 용)
    public ResponseCookie createLogoutCookie() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    // Request Header(Cookie)에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            log.info("JwtTokenProvider: No cookies found in request");
            return null;
        }
        for (Cookie c : request.getCookies()) {
            if (COOKIE_NAME.equals(c.getName())) {
            }
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    // 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // DB 조회 없이 Claims에서 정보 추출하여 User 객체 생성
        String email = claims.getSubject();
        String auth = (String) claims.get(AUTHORITIES_KEY);
        Long userId = claims.get("userId", Long.class);
        String nickName = claims.get("nickName", String.class);

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(auth.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 중요한 점: 여기서는 인증만을 위한 "가짜" User 객체를 만듭니다.
        // 비밀번호는 인증이 끝났으므로 비워둡니다.
        User principal = User.builder()
                .id(userId)
                .email(email)
                .nickName(nickName)
                .password("") // 비밀번호는 필요 없음
                .globalRole(GlobalRole.valueOf(auth)) // Role 복구
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(
                principal);

        return new UsernamePasswordAuthenticationToken(customUserDetails, token, authorities);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}

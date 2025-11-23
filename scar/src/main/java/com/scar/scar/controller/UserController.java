package com.scar.scar.controller;

import com.scar.scar.dto.UserRequestDto;
import com.scar.scar.security.CustomUserDetails;
import com.scar.scar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody UserRequestDto dto) {
        userService.join(dto);
        return ResponseEntity.ok("Success");
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     * 프론트엔드에서 페이지 로드 시 현재 로그인 상태 확인용
     */
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", currentUser.getUser().getId());
        data.put("email", currentUser.getUsername());
        data.put("nickName", currentUser.getNickName());

        return ResponseEntity.ok(data);
    }

    /**
     * 회원 정보 수정 (닉네임)
     */
    @PutMapping("/users/me")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        String newNickName = request.get("nickName");
        if (newNickName == null || newNickName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "닉네임을 입력해주세요."));
        }

        try {
            userService.updateProfile(currentUser.getUser().getId(), newNickName);
            Map<String, Object> response = new HashMap<>();
            response.put("id", currentUser.getUser().getId());
            response.put("email", currentUser.getUsername());
            response.put("nickName", newNickName);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/users/password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "현재 비밀번호와 새 비밀번호를 입력해주세요."));
        }

        try {
            userService.changePassword(currentUser.getUser().getId(), currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/users/me")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            userService.deleteAccount(currentUser.getUser().getId());
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

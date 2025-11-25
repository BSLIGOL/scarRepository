package com.scar.scar.user.controller;

import com.scar.scar.user.dto.UserRequestDto;
import com.scar.scar.global.security.CustomUserDetails;
import com.scar.scar.user.service.UserService;
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
     * 현재 로그인한 유저 정보를 조회합니다.
     */
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인된 사용자가 없습니다."));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", currentUser.getUser().getId());
        data.put("email", currentUser.getUsername());
        data.put("nickName", currentUser.getNickName());

        return ResponseEntity.ok(data);
    }

    /**
     * 프로필 정보를 수정합니다. (닉네임 변경)
     */
    @PutMapping("/users/me")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인된 사용자가 없습니다."));
        }

        String newNickName = request.get("nickName");
        if (newNickName == null || newNickName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "닉네임은 필수 입력값입니다."));
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
     * 비밀번호를 변경합니다.
     */
    @PutMapping("/users/password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인된 사용자가 없습니다."));
        }

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "현재 비밀번호와 새 비밀번호를 모두 입력해야 합니다."));
        }

        try {
            userService.changePassword(currentUser.getUser().getId(), currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 계정을 삭제합니다.
     */
    @DeleteMapping("/users/me")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인된 사용자가 없습니다."));
        }

        try {
            userService.deleteAccount(currentUser.getUser().getId());
            return ResponseEntity.ok(Map.of("message", "계정이 성공적으로 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

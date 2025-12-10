package com.scar.scar.user.controller;

import com.scar.scar.user.dto.UserRequestDto;
import com.scar.scar.user.dto.UserResponseDto;
import com.scar.scar.user.dto.UpdateProfileRequestDto;
import com.scar.scar.user.dto.ChangePasswordRequestDto;
import com.scar.scar.global.security.CustomUserDetails;
import com.scar.scar.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class UserController {
    private final UserService userService;

    /**
     * 회원가입을 처리합니다.
     *
     * @param dto 회원가입 요청 정보 (이메일, 비밀번호, 닉네임)
     * @return 성공 시 "Success" 문자열 반환
     */
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody UserRequestDto dto) {
        userService.join(dto);
        return ResponseEntity.ok("Success");
    }

    /**
     * 현재 로그인한 유저 정보 조회
     */
    @GetMapping("/current-user")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserResponseDto response = UserResponseDto.builder()
                .id(currentUser.getUser().getId())
                .email(currentUser.getUsername())
                .nickName(currentUser.getNickName())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 프로필 정보 수정 (닉네임 변경)
     */
    @PutMapping("/users/me")
    public ResponseEntity<UserResponseDto> updateProfile(
            @RequestBody UpdateProfileRequestDto request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newNickName = request.getNickName();

        if (newNickName == null || newNickName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            userService.updateProfile(currentUser.getUser().getId(), newNickName);

            UserResponseDto response = UserResponseDto.builder()
                    .id(currentUser.getUser().getId())
                    .email(currentUser.getUsername())
                    .nickName(newNickName)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/users/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequestDto request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            userService.changePassword(currentUser.getUser().getId(), request.getCurrentPassword(),
                    request.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 계정 삭제
     */
    @DeleteMapping("/users/me")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            userService.deleteAccount(currentUser.getUser().getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

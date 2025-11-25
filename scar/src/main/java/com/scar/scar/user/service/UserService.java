package com.scar.scar.user.service;

import com.scar.scar.user.domain.User;
import com.scar.scar.user.domain.GlobalRole;
import com.scar.scar.user.dto.UserRequestDto;
import com.scar.scar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User join(UserRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (userRepository.existsByNickName(dto.getNickName())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickName(dto.getNickName())
                .globalRole(GlobalRole.USER)
                .build();

        return userRepository.save(user);
    }

    /**
     * 프로필 정보를 수정합니다. (닉네임 변경)
     */
    @Transactional
    public User updateProfile(Long userId, String newNickName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 닉네임이 변경되었고, 이미 존재하는 닉네임인 경우
        if (!user.getNickName().equals(newNickName) && userRepository.existsByNickName(newNickName)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        user.updateNickName(newNickName);
        return userRepository.save(user);
    }

    /**
     * 비밀번호를 변경합니다.
     */
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 변경
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 계정을 삭제합니다.
     */
    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 계정 삭제
        userRepository.delete(user);
    }
}

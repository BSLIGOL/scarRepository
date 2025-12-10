package com.scar.scar.user.service;

import com.scar.scar.schedule.domain.Schedule;
import com.scar.scar.schedule.domain.ScheduleAttendance;
import com.scar.scar.schedule.repository.ScheduleAttendanceRepository;
import com.scar.scar.schedule.repository.ScheduleRepository;
import com.scar.scar.study.domain.StudyApplication;
import com.scar.scar.study.domain.StudyMember;
import com.scar.scar.study.domain.StudyRole;
import com.scar.scar.study.repository.StudyApplicationRepository;
import com.scar.scar.study.repository.StudyMemberRepository;
import com.scar.scar.study.repository.StudyRepository;
import com.scar.scar.user.domain.User;
import com.scar.scar.user.domain.GlobalRole;
import com.scar.scar.user.dto.UserRequestDto;
import com.scar.scar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyApplicationRepository studyApplicationRepository;
    private final ScheduleAttendanceRepository scheduleAttendanceRepository;
    private final ScheduleRepository scheduleRepository;
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
     * 프로필 정보 수정 (닉네임 변경)
     */
    @Transactional
    public User updateProfile(Long userId, String newNickName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (!user.getNickName().equals(newNickName) && userRepository.existsByNickName(newNickName)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        user.updateNickName(newNickName);
        return userRepository.save(user);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 계정 삭제
     */
    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (studyMemberRepository.existsByUserAndStudyRole(user, StudyRole.LEADER)) {
            throw new IllegalArgumentException("운영 중인 스터디가 있어 탈퇴할 수 없습니다. 스터디를 삭제하거나 위임해주세요.");
        }

        List<StudyMember> memberships = studyMemberRepository.findAllByUserId(userId);
        for (StudyMember member : memberships) {
            if (member.getLeftAt() == null) {
                member.setLeftAt(java.time.LocalDateTime.now());
            }
        }

        List<StudyApplication> applications = studyApplicationRepository.findAllByUser(user);
        studyApplicationRepository.deleteAll(applications);

        userRepository.delete(user);
    }
}

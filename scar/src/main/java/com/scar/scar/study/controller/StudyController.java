package com.scar.scar.study.controller;

import com.scar.scar.study.domain.StudyMember;
import com.scar.scar.user.domain.User;
import com.scar.scar.study.domain.StudyRole;
import com.scar.scar.study.dto.DashboardStudyDto;

import com.scar.scar.study.dto.StudyDetailDto;
import com.scar.scar.study.dto.StudyRequestDto;
import com.scar.scar.study.dto.StudyResponseDto;
import com.scar.scar.user.repository.UserRepository;
import com.scar.scar.global.security.CustomUserDetails;
import com.scar.scar.schedule.service.ScheduleService;
import com.scar.scar.study.service.StudyMemberService;
import com.scar.scar.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studies")
public class StudyController {
    private final StudyService studyService;
    private final StudyMemberService studyMemberService;
    private final ScheduleService scheduleService;

    /**
     * 전체 스터디 목록 조회
     */
    @GetMapping("")
    public ResponseEntity<List<StudyResponseDto>> listStudies() {
        List<StudyResponseDto> studies = studyService.getAllStudies();
        return ResponseEntity.ok(studies);
    }

    /**
     * 스터디 생성
     */
    @PostMapping("/create")
    public ResponseEntity<String> createStudy(
            @RequestBody StudyRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자가 없습니다.");
        }
        studyService.createStudy(dto, currentUser.getUser());
        return ResponseEntity.ok("Success");
    }

    /**
     * 스터디 상세 정보 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudyDetailDto> viewStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = (currentUser != null) ? currentUser.getUser().getId() : null;
        StudyDetailDto study = studyService.getStudyDetail(id, userId);
        return ResponseEntity.ok(study);
    }

    /**
     * 내가 가입한 스터디 목록 조회
     */
    @GetMapping("/my")
    public ResponseEntity<List<DashboardStudyDto>> getMyStudies(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<DashboardStudyDto> myStudies = studyService.getMyStudies(currentUser.getUser().getId());
        return ResponseEntity.ok(myStudies);
    }

    /**
     * 스터디 정보 수정 (스터디 리더만 가능)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudy(
            @PathVariable Long id,
            @RequestBody StudyRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자가 없습니다.");
        }

        try {
            studyService.updateStudy(id, dto, currentUser.getUser().getId());
            return ResponseEntity.ok("스터디 정보가 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 스터디 삭제 (스터디 리더만 가능)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자가 없습니다.");
        }

        try {
            studyService.deleteStudy(id, currentUser.getUser().getId());
            return ResponseEntity.ok("스터디가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 스터디 리더 권한 위임 (스터디 리더만 가능)
     */
    @PostMapping("/{id}/delegate")
    public ResponseEntity<?> delegateLeader(
            @PathVariable Long id,
            @RequestBody Long newLeaderId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자가 없습니다.");
        }

        try {
            studyService.delegateLeader(id, newLeaderId, currentUser.getUser().getId());
            return ResponseEntity.ok("스터디 리더 권한이 위임되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

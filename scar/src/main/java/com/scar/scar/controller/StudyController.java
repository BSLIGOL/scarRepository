package com.scar.scar.controller;

import com.scar.scar.domain.StudyMember;
import com.scar.scar.domain.User;
import com.scar.scar.domain.enums.StudyRole;
import com.scar.scar.dto.DashboardStudyDto;
import com.scar.scar.dto.ScheduleDto;
import com.scar.scar.dto.StudyDetailDto;
import com.scar.scar.dto.StudyRequestDto;
import com.scar.scar.dto.StudyResponseDto;
import com.scar.scar.repository.UserRepository;
import com.scar.scar.security.CustomUserDetails;
import com.scar.scar.service.ScheduleService;
import com.scar.scar.service.StudyMemberService;
import com.scar.scar.service.StudyService;
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

    @GetMapping("")
    public ResponseEntity<List<StudyResponseDto>> listStudies() {
        List<StudyResponseDto> studies = studyService.getAllStudies();
        return ResponseEntity.ok(studies);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createStudy(
            @RequestBody StudyRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }
        studyService.createStudy(dto, currentUser.getUser());
        return ResponseEntity.ok("Success");
    }

    /** 스터디 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<StudyDetailDto> viewStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = (currentUser != null) ? currentUser.getUser().getId() : null;
        StudyDetailDto study = studyService.getStudyDetail(id, userId);
        return ResponseEntity.ok(study);
    }

    /** 내가 가입한 스터디 목록 조회 */
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
     * 스터디 수정 (리더만 가능)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudy(
            @PathVariable Long id,
            @RequestBody StudyRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        try {
            studyService.updateStudy(id, dto, currentUser.getUser().getId());
            return ResponseEntity.ok("스터디가 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 스터디 삭제 (리더만 가능)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        try {
            studyService.deleteStudy(id, currentUser.getUser().getId());
            return ResponseEntity.ok("스터디가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

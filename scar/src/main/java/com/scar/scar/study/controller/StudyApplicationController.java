package com.scar.scar.study.controller;

import com.scar.scar.study.domain.StudyApplication;
import com.scar.scar.study.dto.StudyApplicationResponseDto;
import com.scar.scar.global.security.CustomUserDetails;
import com.scar.scar.study.service.StudyApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class StudyApplicationController {
    private final StudyApplicationService studyApplicationService;

    /** 스터디 신청 목록을 조회합니다. (스터디 리더만 가능, only PENDING) */
    @GetMapping("/{id}")
    public ResponseEntity<List<StudyApplicationResponseDto>> getStudyApplications(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<StudyApplication> applications = studyApplicationService.getStudyApplications(id,
                currentUser.getUser().getId());

        List<StudyApplicationResponseDto> dtos = applications.stream()
                .map(StudyApplicationResponseDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /** 스터디 신청 */
    @PostMapping("/{id}/apply")
    public ResponseEntity<String> applyToStudy(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        String message = body.get("message");
        studyApplicationService.applyToStudy(id, currentUser.getUser().getId(), message);
        return ResponseEntity.ok("Success");
    }

    /** 스터디 신청 승인 */
    @PostMapping("/approve/{applicationId}")
    public ResponseEntity<String> approve(
            @PathVariable long applicationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        studyApplicationService.approveApplication(applicationId, currentUser.getUser().getId());
        return ResponseEntity.ok("Approved");
    }

    /** 스터디 신청 거절 */
    @PostMapping("/reject/{applicationId}")
    public ResponseEntity<String> reject(
            @PathVariable long applicationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        studyApplicationService.rejectApplication(applicationId, currentUser.getUser().getId());
        return ResponseEntity.ok("Rejected");
    }
}

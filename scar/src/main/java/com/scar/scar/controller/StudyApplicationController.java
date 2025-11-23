package com.scar.scar.controller;

import com.scar.scar.domain.StudyApplication;
import com.scar.scar.dto.StudyApplicationResponseDto;
import com.scar.scar.security.CustomUserDetails;
import com.scar.scar.service.StudyApplicationService;
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

    /** 스터디 신청 목록 조회 */
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

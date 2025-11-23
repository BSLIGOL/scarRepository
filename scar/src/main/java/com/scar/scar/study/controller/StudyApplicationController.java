package com.scar.scar.user.controller;

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

    /** ???????諛몃마??????????癒꺜????遺얘턁?????????遺얜??熬곣뫖釉멧벧猿뗪섭鴉????????Β??????*/
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

    /** ???????諛몃마??????????癒꺜??*/
    @PostMapping("/{id}/apply")
    public ResponseEntity<String> applyToStudy(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        String message = body.get("message");
        studyApplicationService.applyToStudy(id, currentUser.getUser().getId(), message);
        return ResponseEntity.ok("Success");
    }

    /** ???????諛몃마??????????癒꺜????????*/
    @PostMapping("/approve/{applicationId}")
    public ResponseEntity<String> approve(
            @PathVariable long applicationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        studyApplicationService.approveApplication(applicationId, currentUser.getUser().getId());
        return ResponseEntity.ok("Approved");
    }

    /** ???????諛몃마??????????癒꺜????遺얘턁筌?（??????*/
    @PostMapping("/reject/{applicationId}")
    public ResponseEntity<String> reject(
            @PathVariable long applicationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        studyApplicationService.rejectApplication(applicationId, currentUser.getUser().getId());
        return ResponseEntity.ok("Rejected");
    }
}


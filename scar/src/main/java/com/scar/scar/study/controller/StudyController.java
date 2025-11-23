package com.scar.scar.study.controller;

import com.scar.scar.study.domain.StudyMember;
import com.scar.scar.user.domain.User;
import com.scar.scar.study.domain.StudyRole;
import com.scar.scar.study.dto.DashboardStudyDto;
import com.scar.scar.schedule.dto.ScheduleDto;
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("?汝??吏??癲ル슢???뼘?????썹땟???嶺뚮ㅎ????");
        }
        studyService.createStudy(dto, currentUser.getUser());
        return ResponseEntity.ok("Success");
    }

    /** ???熬곣뫖???????노듋????됰슦????*/
    @GetMapping("/{id}")
    public ResponseEntity<StudyDetailDto> viewStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long userId = (currentUser != null) ? currentUser.getUser().getId() : null;
        StudyDetailDto study = studyService.getStudyDetail(id, userId);
        return ResponseEntity.ok(study);
    }

    /** ?????熬곣뫖????꿔꺂??袁ㅻ븶筌믠뫀萸???됰슦????*/
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
     * ???熬곣뫖???????볥궚??(??잙갭큔筌??????醫딆쓧???
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudy(
            @PathVariable Long id,
            @RequestBody StudyRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("?汝??吏??癲ル슢???뼘?????썹땟???嶺뚮ㅎ????");
        }

        try {
            studyService.updateStudy(id, dto, currentUser.getUser().getId());
            return ResponseEntity.ok("???熬곣뫖???? ????볥궚???嶺???????");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * ???熬곣뫖???????(??잙갭큔筌??????醫딆쓧???
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudy(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("?汝??吏??癲ル슢???뼘?????썹땟???嶺뚮ㅎ????");
        }

        try {
            studyService.deleteStudy(id, currentUser.getUser().getId());
            return ResponseEntity.ok("???熬곣뫖???? ?????嶺???????");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

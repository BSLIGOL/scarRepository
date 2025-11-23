package com.scar.scar.schedule.controller;

import com.scar.scar.schedule.dto.DashboardScheduleDto;
import com.scar.scar.schedule.dto.ScheduleDetailDto;
import com.scar.scar.schedule.dto.ScheduleRequestDto;
import com.scar.scar.global.security.CustomUserDetails;
import com.scar.scar.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    /** ???嚥?????노듋????됰슦????*/
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailDto> getScheduleDetail(@PathVariable Long scheduleId) {
        ScheduleDetailDto scheduleDetailDto = scheduleService.getScheduleDetail(scheduleId);
        return ResponseEntity.ok(scheduleDetailDto);
    }

    /** ???嚥????꾩룆???*/
    @PostMapping("/new")
    public ResponseEntity<String> newSchedule(
            @RequestBody ScheduleRequestDto schedule,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        scheduleService.createSchedule(schedule, currentUser.getUser());
        return ResponseEntity.ok("Success");
    }

    /** ???????紐꾪닓 ??濚밸Ŧ?????됰슦????(7??????? */
    @GetMapping("/upcoming")
    public ResponseEntity<List<DashboardScheduleDto>> getUpcomingSchedules(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<DashboardScheduleDto> schedules = scheduleService.getUpcomingSchedules(currentUser.getUser().getId());
        return ResponseEntity.ok(schedules);
    }

    /** ????紐꾪닚????濚밸Ŧ?????됰슦????*/
    @GetMapping("/today")
    public ResponseEntity<List<DashboardScheduleDto>> getTodaySchedules(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<DashboardScheduleDto> schedules = scheduleService.getTodaySchedules(currentUser.getUser().getId());
        return ResponseEntity.ok(schedules);
    }

    /** ?????嚥???됰슦????(????????꾣뤃?饔낃퀣????醫딆쓧??? */
    @GetMapping("/my")
    public ResponseEntity<List<DashboardScheduleDto>> getMySchedules(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<DashboardScheduleDto> schedules = scheduleService.getMySchedules(
                currentUser.getUser().getId(), year, month);
        return ResponseEntity.ok(schedules);
    }

    /**
     * ???嚥?????볥궚??(???熬곣뫖?????잙갭큔筌??????醫딆쓧???
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("?汝??吏??癲ル슢???뼘?????썹땟???嶺뚮ㅎ????");
        }

        try {
            scheduleService.updateSchedule(id, dto, currentUser.getUser().getId());
            return ResponseEntity.ok("???嚥싳쉶瑗ч뇡癒?낟??????볥궚???嶺???????");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * ???嚥?????(???熬곣뫖?????잙갭큔筌??????醫딆쓧???
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("?汝??吏??癲ル슢???뼘?????썹땟???嶺뚮ㅎ????");
        }

        try {
            scheduleService.deleteSchedule(id, currentUser.getUser().getId());
            return ResponseEntity.ok("???嚥싳쉶瑗ч뇡癒?낟???????嶺???????");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

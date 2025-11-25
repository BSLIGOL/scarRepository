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

    /** 일정 상세 정보를 조회합니다. */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailDto> getScheduleDetail(@PathVariable Long scheduleId) {
        ScheduleDetailDto scheduleDetailDto = scheduleService.getScheduleDetail(scheduleId);
        return ResponseEntity.ok(scheduleDetailDto);
    }

    /** 일정을 생성합니다. */
    @PostMapping("/new")
    public ResponseEntity<String> newSchedule(
            @RequestBody ScheduleRequestDto schedule,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        scheduleService.createSchedule(schedule, currentUser.getUser());
        return ResponseEntity.ok("Success");
    }

    /** 다가오는 일정을 조회합니다. (7일 이내) */
    @GetMapping("/upcoming")
    public ResponseEntity<List<DashboardScheduleDto>> getUpcomingSchedules(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<DashboardScheduleDto> schedules = scheduleService.getUpcomingSchedules(currentUser.getUser().getId());
        return ResponseEntity.ok(schedules);
    }

    /** 오늘의 일정을 조회합니다. */
    @GetMapping("/today")
    public ResponseEntity<List<DashboardScheduleDto>> getTodaySchedules(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<DashboardScheduleDto> schedules = scheduleService.getTodaySchedules(currentUser.getUser().getId());
        return ResponseEntity.ok(schedules);
    }

    /** 월별 일정을 조회합니다. (year, month가 null이면 전체 일정 조회) */
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
     * 일정을 수정합니다. (스터디 리더만 가능)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자가 없습니다.");
        }

        try {
            scheduleService.updateSchedule(id, dto, currentUser.getUser().getId());
            return ResponseEntity.ok("일정이 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 일정을 삭제합니다. (스터디 리더만 가능)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자가 없습니다.");
        }

        try {
            scheduleService.deleteSchedule(id, currentUser.getUser().getId());
            return ResponseEntity.ok("일정이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

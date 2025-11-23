package com.scar.scar.controller;

import com.scar.scar.dto.DashboardScheduleDto;
import com.scar.scar.dto.ScheduleDetailDto;
import com.scar.scar.dto.ScheduleRequestDto;
import com.scar.scar.security.CustomUserDetails;
import com.scar.scar.service.ScheduleService;
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

    /** 스케줄 상세 조회 */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailDto> getScheduleDetail(@PathVariable Long scheduleId) {
        ScheduleDetailDto scheduleDetailDto = scheduleService.getScheduleDetail(scheduleId);
        return ResponseEntity.ok(scheduleDetailDto);
    }

    /** 스케줄 생성 */
    @PostMapping("/new")
    public ResponseEntity<String> newSchedule(
            @RequestBody ScheduleRequestDto schedule,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        scheduleService.createSchedule(schedule, currentUser.getUser());
        return ResponseEntity.ok("Success");
    }

    /** 다가오는 일정 조회 (7일 이내) */
    @GetMapping("/upcoming")
    public ResponseEntity<List<DashboardScheduleDto>> getUpcomingSchedules(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<DashboardScheduleDto> schedules = scheduleService.getUpcomingSchedules(currentUser.getUser().getId());
        return ResponseEntity.ok(schedules);
    }

    /** 오늘의 일정 조회 */
    @GetMapping("/today")
    public ResponseEntity<List<DashboardScheduleDto>> getTodaySchedules(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<DashboardScheduleDto> schedules = scheduleService.getTodaySchedules(currentUser.getUser().getId());
        return ResponseEntity.ok(schedules);
    }

    /** 내 스케줄 전체 조회 (또는 특정 년/월 필터링) */
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
     * 스케줄 수정 (스터디 리더만 가능)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        try {
            scheduleService.updateSchedule(id, dto, currentUser.getUser().getId());
            return ResponseEntity.ok("스케줄이 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 스케줄 삭제 (스터디 리더만 가능)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        try {
            scheduleService.deleteSchedule(id, currentUser.getUser().getId());
            return ResponseEntity.ok("스케줄이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

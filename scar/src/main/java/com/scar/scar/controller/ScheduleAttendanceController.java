package com.scar.scar.controller;

import com.scar.scar.domain.enums.AttendanceStatus;
import com.scar.scar.service.ScheduleAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class ScheduleAttendanceController {
    private final ScheduleAttendanceService scheduleAttendanceService;

    @PostMapping("/{scheduleId}")
    public ResponseEntity<String> attendSchedule(@PathVariable Long scheduleId,
            @RequestParam Long userId,
            @RequestParam AttendanceStatus status) {
        scheduleAttendanceService.attendSchedule(scheduleId, userId, status);
        return ResponseEntity.ok("Success"); // JSON 응답 반환
    }
}

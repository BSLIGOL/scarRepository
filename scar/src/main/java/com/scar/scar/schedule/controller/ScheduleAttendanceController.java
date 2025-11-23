package com.scar.scar.user.controller;

import com.scar.scar.schedule.domain.AttendanceStatus;
import com.scar.scar.schedule.service.ScheduleAttendanceService;
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
        return ResponseEntity.ok("Success"); // JSON ????????????諛몃마嶺뚮??????
    }
}


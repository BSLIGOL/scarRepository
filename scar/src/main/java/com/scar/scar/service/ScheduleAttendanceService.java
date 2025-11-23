package com.scar.scar.service;

import com.scar.scar.domain.Schedule;
import com.scar.scar.domain.ScheduleAttendance;
import com.scar.scar.domain.StudyMember;
import com.scar.scar.domain.enums.AttendanceStatus;
import com.scar.scar.repository.ScheduleAttendanceRepository;
import com.scar.scar.repository.ScheduleRepository;
import com.scar.scar.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleAttendanceService {

        private final ScheduleRepository scheduleRepository;
        private final ScheduleAttendanceRepository scheduleAttendanceRepository;
        private final StudyMemberRepository studyMemberRepository;

        @Transactional
        public void attendSchedule(Long scheduleId, Long userId, AttendanceStatus status) {
                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));
                StudyMember studyMember = studyMemberRepository
                                .findByStudyIdAndUserId(schedule.getStudy().getId(), userId)
                                .orElseThrow(() -> new IllegalArgumentException("스터디 멤버가 아닙니다."));

                ScheduleAttendance attendance = scheduleAttendanceRepository
                                .findByScheduleAndStudyMember(schedule, studyMember)
                                .orElseGet(() -> {
                                        ScheduleAttendance newAttendance = ScheduleAttendance.builder()
                                                        .schedule(schedule)
                                                        .studyMember(studyMember)
                                                        .status(status)
                                                        .build();
                                        return scheduleAttendanceRepository.save(newAttendance);
                                });

                attendance.setStatus(status);
        }
}

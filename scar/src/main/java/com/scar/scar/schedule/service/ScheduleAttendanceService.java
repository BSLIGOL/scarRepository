package com.scar.scar.schedule.service;

import com.scar.scar.schedule.domain.Schedule;
import com.scar.scar.schedule.domain.ScheduleAttendance;
import com.scar.scar.study.domain.StudyMember;
import com.scar.scar.schedule.domain.AttendanceStatus;
import com.scar.scar.schedule.repository.ScheduleAttendanceRepository;
import com.scar.scar.schedule.repository.ScheduleRepository;
import com.scar.scar.study.repository.StudyMemberRepository;
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
                                .orElseThrow(() -> new IllegalArgumentException("해당 일정을 찾을 수 없습니다."));
                StudyMember studyMember = studyMemberRepository
                                .findByStudyIdAndUserId(schedule.getStudy().getId(), userId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 스터디에 가입된 회원이 아닙니다."));

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

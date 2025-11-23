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
                                .orElseThrow(() -> new IllegalArgumentException("????????熬곣벀嫄?????瑗??????遺얘턁????????????????繹먮굞??????????怨몄）."));
                StudyMember studyMember = studyMemberRepository
                                .findByStudyIdAndUserId(schedule.getStudy().getId(), userId)
                                .orElseThrow(() -> new IllegalArgumentException("???????諛몃마?????遺얘턁????????⑤슢堉???? ??????꾩룆梨띰쭕??????????怨몄）."));

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


package com.scar.scar.schedule.repository;

import com.scar.scar.schedule.domain.Schedule;
import com.scar.scar.schedule.domain.ScheduleAttendance;
import com.scar.scar.study.domain.StudyMember;
import com.scar.scar.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleAttendanceRepository extends JpaRepository<ScheduleAttendance, Long> {
    List<ScheduleAttendance> findAllByScheduleId(Long scheduleId);

    Optional<ScheduleAttendance> findByScheduleAndStudyMember(Schedule schedule, StudyMember studyMember);
}


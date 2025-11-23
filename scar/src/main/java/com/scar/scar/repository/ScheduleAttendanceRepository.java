package com.scar.scar.repository;

import com.scar.scar.domain.Schedule;
import com.scar.scar.domain.ScheduleAttendance;
import com.scar.scar.domain.StudyMember;
import com.scar.scar.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleAttendanceRepository extends JpaRepository<ScheduleAttendance, Long> {
    List<ScheduleAttendance> findAllByScheduleId(Long scheduleId);

    Optional<ScheduleAttendance> findByScheduleAndStudyMember(Schedule schedule, StudyMember studyMember);
}

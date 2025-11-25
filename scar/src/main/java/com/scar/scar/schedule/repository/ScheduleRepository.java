package com.scar.scar.schedule.repository;

import com.scar.scar.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByStudyId(Long studyId);

    List<Schedule> findByStudyIdInAndStartTimeBetweenOrderByStartTimeAsc(
            List<Long> studyIds, LocalDateTime start, LocalDateTime end);

    // 전체 일정 조회 (시간순 정렬)
    List<Schedule> findByStudyIdInOrderByStartTimeAsc(List<Long> studyIds);
}

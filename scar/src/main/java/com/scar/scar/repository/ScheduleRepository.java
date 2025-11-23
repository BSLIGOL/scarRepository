package com.scar.scar.repository;

import com.scar.scar.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByStudyId(Long studyId);

    List<Schedule> findByStudyIdInAndStartTimeBetweenOrderByStartTimeAsc(
            List<Long> studyIds, LocalDateTime start, LocalDateTime end);

    // 전체 스케줄 조회 (날짜 제한 없음)
    List<Schedule> findByStudyIdInOrderByStartTimeAsc(List<Long> studyIds);
}

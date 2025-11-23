package com.scar.scar.service;

import com.scar.scar.domain.*;
import com.scar.scar.dto.AttendanceMemberDto;
import com.scar.scar.dto.DashboardScheduleDto;
import com.scar.scar.dto.ScheduleDetailDto;
import com.scar.scar.dto.ScheduleDto;
import com.scar.scar.dto.ScheduleRequestDto;
import com.scar.scar.repository.ScheduleAttendanceRepository;
import com.scar.scar.repository.ScheduleRepository;
import com.scar.scar.repository.StudyMemberRepository;
import com.scar.scar.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleService {

        private final StudyRepository studyRepository;
        private final ScheduleRepository scheduleRepository;
        private final ScheduleAttendanceRepository scheduleAttendanceRepository;
        private final StudyMemberRepository studyMemberRepository;

        /** 스케줄 목록 조회 */
        @Transactional(readOnly = true)
        public List<ScheduleDto> getSchedules(Long studyId) {

                return scheduleRepository.findAllByStudyId(studyId).stream()
                                .map(ScheduleDto::from)
                                .toList();
        }

        /** 스케줄 상세 조회 */
        @Transactional(readOnly = true)
        public ScheduleDetailDto getScheduleDetail(Long scheduleId) {

                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));

                List<StudyMember> studyMembers = studyMemberRepository.findAllByStudyId(schedule.getStudy().getId());

                List<ScheduleAttendance> attendanceList = scheduleAttendanceRepository.findAllByScheduleId(scheduleId);

                Map<Long, ScheduleAttendance> attendanceMap = new HashMap<>();
                for (ScheduleAttendance a : attendanceList) {
                        attendanceMap.put(a.getStudyMember().getUser().getId(), a);
                }

                List<AttendanceMemberDto> members = studyMembers.stream()
                                .map(sm -> {
                                        ScheduleAttendance attend = attendanceMap.get(sm.getUser().getId());
                                        return AttendanceMemberDto.builder()
                                                        .userId(sm.getUser().getId())
                                                        .nickName(sm.getUser().getNickName())
                                                        .status(attend != null ? attend.getStatus() : null)
                                                        .build();
                                })
                                .toList();

                return ScheduleDetailDto.of(schedule, members);
        }

        /** 스케줄 생성 */
        @Transactional
        public void createSchedule(ScheduleRequestDto dto, User creator) {

                Study study = studyRepository.findById(dto.getStudyId())
                                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

                Schedule schedule = Schedule.builder()
                                .study(study)
                                .title(dto.getTitle())
                                .content(dto.getContent())
                                .startTime(dto.getStartTime())
                                .endTime(dto.getEndTime())
                                .location(dto.getLocation())
                                .createdBy(creator)
                                .build();

                scheduleRepository.save(schedule);

        }

        /** 다가오는 일정 조회 (7일 이내) */
        @Transactional(readOnly = true)
        public List<DashboardScheduleDto> getUpcomingSchedules(Long userId) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime futureLimit = now.plusDays(7);

                List<StudyMember> myStudies = studyMemberRepository.findAllByUserId(userId);
                List<Long> studyIds = myStudies.stream()
                                .map(member -> member.getStudy().getId())
                                .toList();

                if (studyIds.isEmpty()) {
                        return Collections.emptyList();
                }

                List<Schedule> schedules = scheduleRepository
                                .findByStudyIdInAndStartTimeBetweenOrderByStartTimeAsc(studyIds, now, futureLimit);

                return schedules.stream()
                                .map(DashboardScheduleDto::from)
                                .toList();
        }

        /** 오늘의 일정 조회 */
        @Transactional(readOnly = true)
        public List<DashboardScheduleDto> getTodaySchedules(Long userId) {
                LocalDate today = LocalDate.now();
                LocalDateTime startOfDay = today.atStartOfDay();
                LocalDateTime endOfDay = today.atTime(23, 59, 59);

                List<StudyMember> myStudies = studyMemberRepository.findAllByUserId(userId);
                List<Long> studyIds = myStudies.stream()
                                .map(member -> member.getStudy().getId())
                                .toList();

                if (studyIds.isEmpty()) {
                        return Collections.emptyList();
                }

                List<Schedule> schedules = scheduleRepository
                                .findByStudyIdInAndStartTimeBetweenOrderByStartTimeAsc(studyIds, startOfDay, endOfDay);

                return schedules.stream()
                                .map(DashboardScheduleDto::from)
                                .toList();
        }

        /**
         * 내 스케줄 전체 조회 (또는 특정 년/월 필터링)
         * year, month가 null이면 전체 조회
         */
        @Transactional(readOnly = true)
        public List<DashboardScheduleDto> getMySchedules(Long userId, Integer year, Integer month) {
                // 1. 내가 속한 스터디 ID 목록 가져오기
                List<StudyMember> myStudies = studyMemberRepository.findAllByUserId(userId);
                List<Long> studyIds = myStudies.stream()
                                .map(member -> member.getStudy().getId())
                                .toList();

                if (studyIds.isEmpty()) {
                        return Collections.emptyList();
                }

                // 2. 스케줄 조회 (날짜 필터링 여부에 따라)
                List<Schedule> schedules;

                if (year != null && month != null) {
                        // 특정 년/월의 스케줄만 조회
                        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
                        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
                        schedules = scheduleRepository
                                        .findByStudyIdInAndStartTimeBetweenOrderByStartTimeAsc(studyIds, startOfMonth,
                                                        endOfMonth);
                } else {
                        // 전체 스케줄 조회 (날짜 제한 없음)
                        schedules = scheduleRepository.findByStudyIdInOrderByStartTimeAsc(studyIds);
                }

                // 3. DTO로 변환하여 반환
                return schedules.stream()
                                .map(DashboardScheduleDto::from)
                                .toList();
        }

        /**
         * 스케줄 수정 (스터디 리더만 가능)
         */
        @Transactional
        public void updateSchedule(Long scheduleId, ScheduleRequestDto dto, Long userId) {
                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));

                // 스터디 리더인지 확인
                boolean isLeader = studyMemberRepository.findAllByStudyId(schedule.getStudy().getId()).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == com.scar.scar.domain.enums.StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException("스터디 리더만 스케줄을 수정할 수 있습니다.");
                }

                // 스케줄 정보 수정
                schedule.updateInfo(dto.getTitle(), dto.getContent(), dto.getStartTime(),
                                dto.getEndTime(), dto.getLocation());
                scheduleRepository.save(schedule);
        }

        /**
         * 스케줄 삭제 (스터디 리더만 가능)
         */
        @Transactional
        public void deleteSchedule(Long scheduleId, Long userId) {
                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다."));

                // 스터디 리더인지 확인
                boolean isLeader = studyMemberRepository.findAllByStudyId(schedule.getStudy().getId()).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == com.scar.scar.domain.enums.StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException("스터디 리더만 스케줄을 삭제할 수 있습니다.");
                }

                // 스케줄 삭제
                scheduleRepository.delete(schedule);
        }
}

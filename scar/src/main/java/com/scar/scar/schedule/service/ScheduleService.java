package com.scar.scar.schedule.service;

import com.scar.scar.study.domain.*;
import com.scar.scar.schedule.domain.*;
import com.scar.scar.user.domain.*;
import com.scar.scar.schedule.dto.AttendanceMemberDto;
import com.scar.scar.schedule.dto.DashboardScheduleDto;
import com.scar.scar.schedule.dto.ScheduleDetailDto;
import com.scar.scar.schedule.dto.ScheduleDto;
import com.scar.scar.schedule.dto.ScheduleRequestDto;
import com.scar.scar.schedule.repository.ScheduleAttendanceRepository;
import com.scar.scar.schedule.repository.ScheduleRepository;
import com.scar.scar.study.repository.StudyMemberRepository;
import com.scar.scar.study.repository.StudyRepository;
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

        /** 특정 스터디 전체 일정 조회 */
        @Transactional(readOnly = true)
        public List<ScheduleDto> getSchedules(Long studyId) {

                return scheduleRepository.findAllByStudyId(studyId).stream()
                                .map(ScheduleDto::from)
                                .toList();
        }

        /** 일정 상세 정보 조회 */
        @Transactional(readOnly = true)
        public ScheduleDetailDto getScheduleDetail(Long scheduleId, Long currentUserId) {

                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "해당 일정을 찾을 수 없습니다."));

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

                boolean isLeader = false;
                if (currentUserId != null) {
                        isLeader = studyMembers.stream()
                                        .anyMatch(member -> member.getUser().getId().equals(currentUserId)
                                                        && member.getStudyRole() == StudyRole.LEADER
                                                        && member.getLeftAt() == null);
                }

                return ScheduleDetailDto.of(schedule, members, isLeader);
        }

        /** 일정 생성 */
        @Transactional
        public void createSchedule(ScheduleRequestDto dto, User creator) {

                Study study = studyRepository.findById(dto.getStudyId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "해당 스터디를 찾을 수 없습니다."));

                Schedule schedule = Schedule.builder()
                                .study(study)
                                .title(dto.getTitle())
                                .content(dto.getContent())
                                .startTime(dto.getStartTime().toLocalDateTime())
                                .endTime(dto.getEndTime().toLocalDateTime())
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
         * 월별 일정 조회 (year, month가 null이면 전체 일정 조회)
         */
        @Transactional(readOnly = true)
        public List<DashboardScheduleDto> getMySchedules(Long userId, Integer year, Integer month) {
                List<StudyMember> myStudies = studyMemberRepository.findAllByUserId(userId);
                List<Long> studyIds = myStudies.stream()
                                .map(member -> member.getStudy().getId())
                                .toList();

                if (studyIds.isEmpty()) {
                        return Collections.emptyList();
                }

                // 2. 일정 조회 (년/월 조건이 있으면 해당 월, 없으면 전체)
                List<Schedule> schedules;

                if (year != null && month != null) {
                        // 해당 월의 시작일과 종료일 계산
                        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
                        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
                        schedules = scheduleRepository
                                        .findByStudyIdInAndStartTimeBetweenOrderByStartTimeAsc(studyIds, startOfMonth,
                                                        endOfMonth);
                } else {
                        schedules = scheduleRepository.findByStudyIdInOrderByStartTimeAsc(studyIds);
                }

                return schedules.stream()
                                .map(DashboardScheduleDto::from)
                                .toList();
        }

        /**
         * 일정 수정 (스터디 리더만 가능)
         */
        @Transactional
        public void updateSchedule(Long scheduleId, ScheduleRequestDto dto, Long userId) {
                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "해당 일정을 찾을 수 없습니다."));

                // 스터디 리더인지 확인
                boolean isLeader = studyMemberRepository.findAllByStudyId(schedule.getStudy().getId()).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException(
                                        "스터디 리더만 일정을 수정할 수 있습니다.");
                }

                // 일정 정보 업데이트
                schedule.updateInfo(dto.getTitle(), dto.getContent(), dto.getStartTime().toLocalDateTime(),
                                dto.getEndTime().toLocalDateTime(), dto.getLocation());
                scheduleRepository.save(schedule);
        }

        /**
         * 일정 삭제 (스터디 리더만 가능)
         */
        @Transactional
        public void deleteSchedule(Long scheduleId, Long userId) {
                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "해당 일정을 찾을 수 없습니다."));

                boolean isLeader = studyMemberRepository.findAllByStudyId(schedule.getStudy().getId()).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == com.scar.scar.study.domain.StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException(
                                        "스터디 리더만 일정을 삭제할 수 있습니다.");
                }

                scheduleRepository.delete(schedule);
        }
}

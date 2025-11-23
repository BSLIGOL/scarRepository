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

        /** ???????遺얘턁?????????遺얜??熬곣뫖釉멧벧猿뗪섭鴉????????Β??????*/
        @Transactional(readOnly = true)
        public List<ScheduleDto> getSchedules(Long studyId) {

                return scheduleRepository.findAllByStudyId(studyId).stream()
                                .map(ScheduleDto::from)
                                .toList();
        }

        /** ??????????嶺뚮ㅎ?볠꽴???????Β??????*/
        @Transactional(readOnly = true)
        public ScheduleDetailDto getScheduleDetail(Long scheduleId) {

                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException("????????熬곣벀嫄?????瑗??????遺얘턁????????????????繹먮굞??????????怨몄）."));

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

        /** ???????????썹땟戮녹????*/
        @Transactional
        public void createSchedule(ScheduleRequestDto dto, User creator) {

                Study study = studyRepository.findById(dto.getStudyId())
                                .orElseThrow(() -> new IllegalArgumentException("???????諛몃마???? ??遺얘턁????????????????繹먮굞??????????怨몄）."));

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

        /** ????????꿔꺂???????????롮쾸?椰????????Β??????(7??????? */
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

        /** ?????꿔꺂????????????롮쾸?椰????????Β??????*/
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
         * ?????????????꾩룆梨띰쭕???????Β??????(????????????????????????▲뀋????釉먮폁????
         * year, month??????ル뭽?? null???????????꾩룆梨띰쭕???????Β??????
         */
        @Transactional(readOnly = true)
        public List<DashboardScheduleDto> getMySchedules(Long userId, Integer year, Integer month) {
                // 1. ??? ??????繹먮굞??????????諛몃마???ID ??遺얘턁?????????遺얜??熬곣뫖釉멧벧猿뗪섭鴉?????????ル뭽????耀붾굝??????癲ル슢???븐쭍?黎앸럽??????
                List<StudyMember> myStudies = studyMemberRepository.findAllByUserId(userId);
                List<Long> studyIds = myStudies.stream()
                                .map(member -> member.getStudy().getId())
                                .toList();

                if (studyIds.isEmpty()) {
                        return Collections.emptyList();
                }

                // 2. ??????????Β??????(???????癒꺜? ?????????▲뀋????釉먮폁????????????????ㅻ깹???
                List<Schedule> schedules;

                if (year != null && month != null) {
                        // ????????????濾????????????熬곣벀嫄?????瑗???????????Β??????
                        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
                        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
                        schedules = scheduleRepository
                                        .findByStudyIdInAndStartTimeBetweenOrderByStartTimeAsc(studyIds, startOfMonth,
                                                        endOfMonth);
                } else {
                        // ??????꾩룆梨띰쭕????????????Β??????(???????癒꺜? ????????????????살몖??
                        schedules = scheduleRepository.findByStudyIdInOrderByStartTimeAsc(studyIds);
                }

                // 3. DTO???????怨뺤른???????????????諛몃마嶺뚮??????
                return schedules.stream()
                                .map(DashboardScheduleDto::from)
                                .toList();
        }

        /**
         * ??????????怨뚮뼺?됰뗀???(???????諛몃마?????????좊틣??釉랁닕???????????ル뭽????
         */
        @Transactional
        public void updateSchedule(Long scheduleId, ScheduleRequestDto dto, Long userId) {
                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException("????????熬곣벀嫄?????瑗??????遺얘턁????????????????繹먮굞??????????怨몄）."));

                // ???????諛몃마?????????좊틣??釉랁닕????? ??耀붾굝??????????
                boolean isLeader = studyMemberRepository.findAllByStudyId(schedule.getStudy().getId()).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == com.scar.scar.study.domain.StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException("???????諛몃마?????????좊틣??釉랁닕?????????????熬곣벀嫄?????瑗?????????怨뚮뼺?됰뗀???????????????????怨몄）.");
                }

                // ???????耀붾굝??????????????怨뚮뼺?됰뗀???
                schedule.updateInfo(dto.getTitle(), dto.getContent(), dto.getStartTime(),
                                dto.getEndTime(), dto.getLocation());
                scheduleRepository.save(schedule);
        }

        /**
         * ?????????(???????諛몃마?????????좊틣??釉랁닕???????????ル뭽????
         */
        @Transactional
        public void deleteSchedule(Long scheduleId, Long userId) {
                Schedule schedule = scheduleRepository.findById(scheduleId)
                                .orElseThrow(() -> new IllegalArgumentException("????????熬곣벀嫄?????瑗??????遺얘턁????????????????繹먮굞??????????怨몄）."));

                // ???????諛몃마?????????좊틣??釉랁닕????? ??耀붾굝??????????
                boolean isLeader = studyMemberRepository.findAllByStudyId(schedule.getStudy().getId()).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == com.scar.scar.study.domain.StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException("???????諛몃마?????????좊틣??釉랁닕?????????????熬곣벀嫄?????瑗????????????????????????怨몄）.");
                }

                // ?????????
                scheduleRepository.delete(schedule);
        }
}


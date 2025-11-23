package com.scar.scar.study.service;

import com.scar.scar.study.domain.Study;
import com.scar.scar.study.domain.StudyMember;
import com.scar.scar.user.domain.User;
import com.scar.scar.study.domain.ApplyStatus;
import com.scar.scar.study.domain.StudyRole;
import com.scar.scar.schedule.dto.DashboardScheduleDto;
import com.scar.scar.study.dto.DashboardStudyDto;
import com.scar.scar.study.dto.StudyDetailDto;
import com.scar.scar.study.dto.StudyRequestDto;
import com.scar.scar.study.dto.StudyResponseDto;
import com.scar.scar.schedule.repository.ScheduleRepository;
import com.scar.scar.study.repository.StudyApplicationRepository;
import com.scar.scar.study.repository.StudyMemberRepository;
import com.scar.scar.study.repository.StudyRepository;
import com.scar.scar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {

        private final StudyRepository studyRepository;
        private final StudyMemberRepository studyMemberRepository;
        private final StudyApplicationRepository studyApplicationRepository;
        private final UserRepository userRepository;

        private final ScheduleRepository scheduleRepository;

        @Transactional
        public StudyResponseDto createStudy(StudyRequestDto dto, User creator) {
                Study study = dto.toEntity(creator);
                Study savedStudy = studyRepository.save(study);

                StudyMember leaderMember = StudyMember.builder()
                                .study(savedStudy)
                                .user(creator)
                                .studyRole(StudyRole.LEADER)
                                .joinedAt(LocalDateTime.now())
                                .build();

                studyMemberRepository.save(leaderMember);
                long memberCount = studyMemberRepository.countByStudy(savedStudy);

                return StudyResponseDto.of(savedStudy, memberCount);
        }

        @Transactional(readOnly = true)
        public List<StudyResponseDto> getAllStudies() {
                return studyRepository.findAllStudiesWithMemberCount();
        }

        @Transactional(readOnly = true)
        public StudyDetailDto getStudyDetail(Long studyId, Long currentUserId) {
                Study study = studyRepository.findByIdWithCreatorAndMembers(studyId)
                                .orElseThrow(() -> new RuntimeException("???????諛몃마???? ?????Β???????? ?????????????怨몄）."));

                boolean joined = false;
                boolean applied = false;
                boolean isLeader = false;

                // ????????遺얘턁????????⑤슢堉?????????????▲뀋????釉먮폁????
                List<StudyMember> activeMembers = study.getMembers().stream()
                                .filter(member -> member.getLeftAt() == null)
                                .toList();

                if (currentUserId != null) {
                        User user = userRepository.findById(currentUserId)
                                        .orElseThrow(() -> new RuntimeException("?????? ?????Β???????? ?????????????怨몄）."));

                        joined = activeMembers.stream()
                                        .anyMatch(member -> member.getUser().getId().equals(currentUserId));

                        applied = studyApplicationRepository.existsByUserAndStudyAndStatusIn(
                                        user, study, List.of(ApplyStatus.PENDING));

                        // ??????좊틣??釉랁닕?????? ??耀붾굝??????????
                        isLeader = activeMembers.stream()
                                        .anyMatch(member -> member.getUser().getId().equals(currentUserId)
                                                        && member.getStudyRole() == StudyRole.LEADER);
                }

                // ??????롮쾸?椰?????遺얘턁?????????遺얜??熬곣뫖釉멧벧猿뗪섭鴉????????Β??????
                List<DashboardScheduleDto> schedules = List.of();
                if (joined) {
                        schedules = scheduleRepository.findAllByStudyId(studyId).stream()
                                        .map(DashboardScheduleDto::from)
                                        .collect(Collectors.toList());
                }

                return StudyDetailDto.of(study, activeMembers.size(),
                                activeMembers.stream().map(member -> member.getUser().getNickName()).toList(),
                                joined, applied, isLeader, schedules);
        }

        @Transactional(readOnly = true)
        public List<DashboardStudyDto> getMyStudies(Long userId) {
                List<StudyMember> myMemberships = studyMemberRepository.findAllByUserId(userId);

                return myMemberships.stream()
                                .map(member -> {
                                        Study study = member.getStudy();
                                        long memberCount = studyMemberRepository.countByStudy(study);
                                        return DashboardStudyDto.from(study, memberCount);
                                })
                                .collect(Collectors.toList());

        }

        /**
         * ???????諛몃마????????怨뚮뼺?됰뗀???(??????좊틣??釉랁닕???????????ル뭽????
         */
        @Transactional
        public void updateStudy(Long studyId, StudyRequestDto dto, Long userId) {
                Study study = studyRepository.findById(studyId)
                                .orElseThrow(() -> new IllegalArgumentException("???????諛몃마???? ??遺얘턁????????????????繹먮굞??????????怨몄）."));

                // ??????좊틣??釉랁닕????? ??耀붾굝??????????
                boolean isLeader = studyMemberRepository.findAllByStudyId(studyId).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException("???????諛몃마?????????좊틣??釉랁닕??????????怨뚮뼺?됰뗀???????????????????怨몄）.");
                }

                // ???????諛몃마?????耀붾굝??????????????怨뚮뼺?됰뗀???
                study.updateInfo(dto.getTitle(), dto.getContent(), dto.getMaxMember());
                studyRepository.save(study);
        }

        /**
         * ???????諛몃마???????(??????좊틣??釉랁닕???????????ル뭽????
         */
        @Transactional
        public void deleteStudy(Long studyId, Long userId) {
                Study study = studyRepository.findById(studyId)
                                .orElseThrow(() -> new IllegalArgumentException("???????諛몃마???? ??遺얘턁????????????????繹먮굞??????????怨몄）."));

                // ??????좊틣??釉랁닕????? ??耀붾굝??????????
                boolean isLeader = studyMemberRepository.findAllByStudyId(studyId).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException("???????諛몃마?????????좊틣??釉랁닕?????????????????????????怨몄）.");
                }

                // ??????꾩룆梨띰쭕??????????袁④뎬??????饔낅떽????????????????대첐??
                studyRepository.delete(study);
        }
}


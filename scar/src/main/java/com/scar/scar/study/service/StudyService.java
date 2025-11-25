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
                                .orElseThrow(() -> new RuntimeException("스터디를 찾을 수 없습니다."));

                boolean joined = false;
                boolean applied = false;
                boolean isLeader = false;

                // 탈퇴하지 않은 멤버만 필터링
                List<StudyMember> activeMembers = study.getMembers().stream()
                                .filter(member -> member.getLeftAt() == null)
                                .toList();

                if (currentUserId != null) {
                        User user = userRepository.findById(currentUserId)
                                        .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

                        joined = activeMembers.stream()
                                        .anyMatch(member -> member.getUser().getId().equals(currentUserId));

                        applied = studyApplicationRepository.existsByUserAndStudyAndStatusIn(
                                        user, study, List.of(ApplyStatus.PENDING));

                        // 리더인지 확인
                        isLeader = activeMembers.stream()
                                        .anyMatch(member -> member.getUser().getId().equals(currentUserId)
                                                        && member.getStudyRole() == StudyRole.LEADER);
                }

                // 스케줄 목록 (멤버인 경우에만 조회)
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
         * 스터디 정보 수정 (리더만 가능)
         */
        @Transactional
        public void updateStudy(Long studyId, StudyRequestDto dto, Long userId) {
                Study study = studyRepository.findById(studyId)
                                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

                // 리더인지 확인
                boolean isLeader = studyMemberRepository.findAllByStudyId(studyId).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException("스터디 리더만 수정할 수 있습니다.");
                }

                // 스터디 정보 수정
                study.updateInfo(dto.getTitle(), dto.getContent(), dto.getMaxMember());
                studyRepository.save(study);
        }

        /**
         * 스터디 삭제 (리더만 가능)
         */
        @Transactional
        public void deleteStudy(Long studyId, Long userId) {
                Study study = studyRepository.findById(studyId)
                                .orElseThrow(() -> new IllegalArgumentException("스터디를 찾을 수 없습니다."));

                // 리더인지 확인
                boolean isLeader = studyMemberRepository.findAllByStudyId(studyId).stream()
                                .anyMatch(member -> member.getUser().getId().equals(userId)
                                                && member.getStudyRole() == StudyRole.LEADER
                                                && member.getLeftAt() == null);

                if (!isLeader) {
                        throw new IllegalArgumentException("스터디 리더만 삭제할 수 있습니다.");
                }

                // 연관된 데이터 삭제 (소프트 삭제)
                // 1. 스케줄 삭제
                List<com.scar.scar.schedule.domain.Schedule> schedules = scheduleRepository.findAllByStudyId(studyId);
                scheduleRepository.deleteAll(schedules);

                // 2. 신청 내역 삭제
                List<com.scar.scar.study.domain.StudyApplication> applications = studyApplicationRepository
                                .findByStudy(study);
                studyApplicationRepository.deleteAll(applications);

                // 3. 멤버 삭제 (리더 포함)
                List<StudyMember> members = studyMemberRepository.findAllByStudyId(studyId);
                studyMemberRepository.deleteAll(members);

                // 4. 스터디 삭제
                studyRepository.delete(study);
        }
}

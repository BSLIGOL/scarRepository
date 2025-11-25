package com.scar.scar.study.service;

import com.scar.scar.study.domain.Study;
import com.scar.scar.study.domain.StudyApplication;
import com.scar.scar.study.domain.StudyMember;
import com.scar.scar.user.domain.User;
import com.scar.scar.study.domain.ApplyStatus;
import com.scar.scar.study.domain.StudyRole;
import com.scar.scar.study.repository.StudyApplicationRepository;
import com.scar.scar.study.repository.StudyMemberRepository;
import com.scar.scar.study.repository.StudyRepository;
import com.scar.scar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyApplicationService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final StudyApplicationRepository studyApplicationRepository;
    private final StudyMemberRepository studyMemberRepository;

    /** 스터디 신청 목록 조회 (only PENDING) */
    public List<StudyApplication> getStudyApplications(Long studyId, Long loginId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디를 찾을 수 없습니다."));

        StudyMember leader = studyMemberRepository.findByStudyAndStudyRole(study, StudyRole.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디의 리더를 찾을 수 없습니다."));

        if (!leader.getUser().getId().equals(loginId)) {
            throw new IllegalStateException("스터디 리더만 신청 목록을 조회할 수 있습니다.");
        }

        return studyApplicationRepository.findByStudyAndStatus(study, ApplyStatus.PENDING);
    }

    /** 스터디 신청 */
    @Transactional
    public Long applyToStudy(Long studyId, Long userId, String message) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디를 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (studyMemberRepository.existsByUserAndStudyAndLeftAtIsNull(user, study)) {
            throw new IllegalStateException("이미 해당 스터디에 가입되어 있습니다.");
        }

        if (studyApplicationRepository.existsByUserAndStudyAndStatus(user, study, ApplyStatus.PENDING)) {
            throw new IllegalStateException("이미 스터디 신청이 대기 중입니다.");
        }

        int currentMembers = study.getMembers().size();

        if (currentMembers >= study.getMaxMember()) {
            throw new IllegalStateException("스터디 정원이 초과되었습니다.");
        }

        StudyApplication studyApplication = StudyApplication.builder()
                .study(study)
                .user(user)
                .message(message)
                .status(ApplyStatus.PENDING)
                .build();

        studyApplicationRepository.save(studyApplication);

        return studyApplication.getId();
    }

    /** 스터디 신청 승인 */
    @Transactional
    public StudyApplication approveApplication(Long applicationId, Long leaderId) {
        StudyApplication application = studyApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디 신청을 찾을 수 없습니다."));

        Study study = application.getStudy();

        StudyMember leader = studyMemberRepository.findByStudyAndStudyRole(study, StudyRole.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디의 리더를 찾을 수 없습니다."));

        if (!leader.getUser().getId().equals(leaderId)) {
            throw new IllegalStateException("스터디 리더만 신청을 승인할 수 있습니다.");
        }

        if (application.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        application.setStatus(ApplyStatus.APPROVED);

        StudyMember newMember = StudyMember.builder()
                .study(study)
                .user(application.getUser())
                .studyRole(StudyRole.MEMBER)
                .joinedAt(java.time.LocalDateTime.now())
                .build();

        studyMemberRepository.save(newMember);

        return application;
    }

    /** 스터디 신청 거절 */
    @Transactional
    public StudyApplication rejectApplication(Long applicationId, Long leaderId) {
        StudyApplication application = studyApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디 신청을 찾을 수 없습니다."));

        Study study = application.getStudy();

        StudyMember leader = studyMemberRepository.findByStudyAndStudyRole(study, StudyRole.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디의 리더를 찾을 수 없습니다."));

        if (!leader.getUser().getId().equals(leaderId)) {
            throw new IllegalStateException("스터디 리더만 신청을 거절할 수 있습니다.");
        }

        if (application.getStatus() != ApplyStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }
        application.setStatus(ApplyStatus.REJECTED);

        return application;
    }
}

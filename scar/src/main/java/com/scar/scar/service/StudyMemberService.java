package com.scar.scar.service;

import com.scar.scar.domain.Study;
import com.scar.scar.domain.StudyMember;
import com.scar.scar.domain.User;
import com.scar.scar.repository.StudyMemberRepository;
import com.scar.scar.repository.StudyRepository;
import com.scar.scar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;
    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    public void leaveStudy(Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        StudyMember member = studyMemberRepository.findByUserAndStudy(user, study)
                .orElseThrow(() -> new IllegalStateException("스터디 멤버가 아닙니다."));

        member.setLeftAt(LocalDateTime.now());
        studyMemberRepository.save(member);
    }

    public List<StudyMember> getMembers(Long studyId) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다."));

        return studyMemberRepository.findByStudy(study);
    }

    /** 스터디 멤버 정보 */
    public StudyMember getStudyMember(Long studyId, Long userId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디가 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        return studyMemberRepository.findByUserAndStudy(user, study).orElse(null);
    }
}

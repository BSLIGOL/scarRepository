package com.scar.scar.study.service;

import com.scar.scar.study.domain.Study;
import com.scar.scar.study.domain.StudyMember;
import com.scar.scar.user.domain.User;
import com.scar.scar.study.repository.StudyMemberRepository;
import com.scar.scar.study.repository.StudyRepository;
import com.scar.scar.user.repository.UserRepository;
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
                                .orElseThrow(() -> new IllegalArgumentException("해당 스터디를 찾을 수 없습니다."));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

                StudyMember member = studyMemberRepository.findByUserAndStudy(user, study)
                                .orElseThrow(() -> new IllegalStateException("해당 스터디에 가입된 회원이 아닙니다."));

                member.setLeftAt(LocalDateTime.now());
                studyMemberRepository.save(member);
        }

        public List<StudyMember> getMembers(Long studyId) {

                Study study = studyRepository.findById(studyId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 스터디를 찾을 수 없습니다."));

                return studyMemberRepository.findByStudy(study);
        }

        /** 스터디 멤버 정보 조회 */
        public StudyMember getStudyMember(Long studyId, Long userId) {
                Study study = studyRepository.findById(studyId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 스터디를 찾을 수 없습니다."));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

                return studyMemberRepository.findByUserAndStudy(user, study).orElse(null);
        }
}

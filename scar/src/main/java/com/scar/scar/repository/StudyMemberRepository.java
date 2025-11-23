package com.scar.scar.repository;

import com.scar.scar.domain.Study;
import com.scar.scar.domain.StudyMember;
import com.scar.scar.domain.User;
import com.scar.scar.domain.enums.StudyRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    List<StudyMember> findByStudy(Study study);

    List<StudyMember> findAllByStudyId(Long studyId);

    boolean existsByUserAndStudy(User user, Study study);

    Optional<StudyMember> findByUserAndStudy(User user, Study study);

    Optional<StudyMember> findByStudyAndStudyRole(Study study, StudyRole studyRole);

    long countByStudy(Study study);

    List<StudyMember> findAllByUserId(Long userId);

    Optional<StudyMember> findByStudyIdAndUserId(Long studyId, Long userId);

    boolean existsByUserAndStudyAndLeftAtIsNull(User user, Study study);
}

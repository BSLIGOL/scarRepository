package com.scar.scar.study.repository;

import com.scar.scar.study.domain.Study;
import com.scar.scar.study.domain.StudyApplication;
import com.scar.scar.user.domain.User;
import com.scar.scar.study.domain.ApplyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyApplicationRepository extends JpaRepository<StudyApplication, Long> {

    List<StudyApplication> findByStudy(Study study);

    List<StudyApplication> findByStudyAndStatus(Study study, ApplyStatus status);

    Optional<StudyApplication> findByUserAndStudy(User user, Study study);

    long countByStudy(Study study);

    boolean existsByUserAndStudy(User userId, Study studyId);

    boolean existsByUserAndStudyAndStatusIn(User user, Study study, List<ApplyStatus> statuses);

    boolean existsByUserAndStudyAndStatus(User user, Study study, ApplyStatus status);
}


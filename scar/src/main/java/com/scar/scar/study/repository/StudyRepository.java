package com.scar.scar.study.repository;

import com.scar.scar.study.domain.Study;
import com.scar.scar.user.domain.User;
import com.scar.scar.study.dto.StudyResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

        List<Study> findByCreator(User creator);

        List<Study> findByTitleContaining(String keyword);

        @Query("SELECT new com.scar.scar.study.dto.StudyResponseDto(" +
                        "s.id, s.title, s.content, s.maxMember, " +
                        "s.creator.nickName, COUNT(sm.id)) " +
                        "FROM Study s " +
                        "LEFT JOIN s.creator " +
                        "LEFT JOIN StudyMember sm ON sm.study.id = s.id " +
                        "GROUP BY s.id, s.title, s.content, s.maxMember, s.creator.nickName")
        List<StudyResponseDto> findAllStudiesWithMemberCount();

        @Query("SELECT s FROM Study s " +
                        "JOIN FETCH s.creator " +
                        "LEFT JOIN FETCH s.members m " +
                        "WHERE s.id = :id")
        Optional<Study> findByIdWithCreatorAndMembers(@Param("id") Long id);
}

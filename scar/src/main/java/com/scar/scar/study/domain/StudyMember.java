package com.scar.scar.study.domain;

import com.scar.scar.study.domain.Study;

import com.scar.scar.user.domain.User;

import com.scar.scar.global.entity.BaseEntity;

import com.scar.scar.study.domain.StudyRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "study_member")
@SQLDelete(sql = "UPDATE study_member SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class StudyMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_role", nullable = false)
    private StudyRole studyRole;
}


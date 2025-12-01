package com.scar.scar.study.domain;

import com.scar.scar.study.domain.Study;

import com.scar.scar.user.domain.User;

import com.scar.scar.global.entity.BaseEntity;

import com.scar.scar.study.domain.ApplyStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "study_application")
@SQLDelete(sql = "UPDATE study_application SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class StudyApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplyStatus status = ApplyStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String message;
}


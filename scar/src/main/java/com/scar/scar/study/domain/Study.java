package com.scar.scar.study.domain;

import com.scar.scar.user.domain.User;

import com.scar.scar.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "studies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE studies SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int maxMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "study", fetch = FetchType.LAZY)
    @Builder.Default
    private List<StudyMember> members = new ArrayList<>();

    /**
     * 스터디 정보를 수정합니다.
     */
    public void updateInfo(String title, String content, int maxMember) {
        this.title = title;
        this.content = content;
        this.maxMember = maxMember;
    }
}

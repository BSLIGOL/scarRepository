package com.scar.scar.schedule.domain;

import com.scar.scar.study.domain.StudyMember;

import com.scar.scar.study.domain.Study;

import com.scar.scar.global.entity.BaseEntity;

import com.scar.scar.schedule.domain.AttendanceStatus;
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
@Table(name = "schedule_attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "schedule_id", "study_member_id" })
})
@SQLDelete(sql = "UPDATE schedule_attendance SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ScheduleAttendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_member_id", nullable = false)
    private StudyMember studyMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;
}


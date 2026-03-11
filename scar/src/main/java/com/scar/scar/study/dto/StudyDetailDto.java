package com.scar.scar.study.dto;

import com.scar.scar.schedule.dto.DashboardScheduleDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudyDetailDto {
    private Long id;
    private String title;
    private String content;
    private int maxMember;
    private long currentMemberCount;
    private List<StudyMemberDto> members;
    private boolean joinedByCurrentUser;
    private boolean appliedByCurrentUser;
    @JsonProperty("isLeader")
    private boolean isLeader;
    private String leaderNickname;
    private List<DashboardScheduleDto> schedules;

    public static StudyDetailDto of(com.scar.scar.study.domain.Study study, int currentMemberCount,
            List<StudyMemberDto> members, boolean joined, boolean applied, boolean isLeader,
            List<DashboardScheduleDto> schedules, String leaderNickname) {
        return StudyDetailDto.builder()
                .id(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .maxMember(study.getMaxMember())
                .currentMemberCount(currentMemberCount)
                .members(members)
                .joinedByCurrentUser(joined)
                .appliedByCurrentUser(applied)
                .isLeader(isLeader)
                .leaderNickname(leaderNickname)
                .schedules(schedules)
                .build();
    }
}

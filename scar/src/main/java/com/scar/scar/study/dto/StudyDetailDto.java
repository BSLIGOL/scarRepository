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
    private String creatorNickName;
    private long currentMemberCount;
    private List<String> memberNickNames;
    private boolean joinedByCurrentUser;
    private boolean appliedByCurrentUser;
    @JsonProperty("isLeader")
    private boolean isLeader;
    private List<DashboardScheduleDto> schedules;

    public static StudyDetailDto of(com.scar.scar.study.domain.Study study, int currentMemberCount,
            List<String> memberNickNames, boolean joined, boolean applied, boolean isLeader,
            List<DashboardScheduleDto> schedules) {
        return StudyDetailDto.builder()
                .id(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .maxMember(study.getMaxMember())
                .currentMemberCount(currentMemberCount)
                .creatorNickName(study.getCreator().getNickName())
                .memberNickNames(memberNickNames)
                .joinedByCurrentUser(joined)
                .appliedByCurrentUser(applied)
                .isLeader(isLeader)
                .schedules(schedules)
                .build();
    }
}


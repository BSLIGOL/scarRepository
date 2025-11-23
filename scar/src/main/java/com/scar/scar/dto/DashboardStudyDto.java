package com.scar.scar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardStudyDto {
    private Long id;
    private String name;
    private int memberCount;
    private int maxMembers;

    public static DashboardStudyDto from(com.scar.scar.domain.Study study, long memberCount) {
        return DashboardStudyDto.builder()
                .id(study.getId())
                .name(study.getTitle())
                .memberCount((int) memberCount)
                .maxMembers(study.getMaxMember())
                .build();
    }
}

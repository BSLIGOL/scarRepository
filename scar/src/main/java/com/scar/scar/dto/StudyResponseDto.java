package com.scar.scar.dto;

import com.scar.scar.domain.Study;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyResponseDto {
    private Long id;
    private String title;
    private String content;
    private int maxMember;
    private String creatorNickName;
    private long currentMemberCount;

    public static StudyResponseDto of(Study study, long memberCount) {
        return StudyResponseDto.builder()
                .id(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .maxMember(study.getMaxMember())
                .creatorNickName(study.getCreator().getNickName())
                .currentMemberCount(memberCount)
                .build();
    }
}

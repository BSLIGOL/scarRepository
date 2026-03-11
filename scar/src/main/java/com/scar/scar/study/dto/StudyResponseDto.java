package com.scar.scar.study.dto;

import com.scar.scar.study.domain.Study;
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
    private String leaderNickname;
    private int maxMember;
    private long currentMemberCount;

    public StudyResponseDto(Long id, String title, String content, int maxMember, long currentMemberCount,
            String leaderNickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.maxMember = maxMember;
        this.currentMemberCount = currentMemberCount;
        this.leaderNickname = leaderNickname;
    }

    public static StudyResponseDto of(Study study, long memberCount) {
        return StudyResponseDto.builder()
                .id(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .maxMember(study.getMaxMember())
                .currentMemberCount(memberCount)
                .build();
    }

    public static StudyResponseDto of(Study study, long memberCount, String leaderNickname) {
        return StudyResponseDto.builder()
                .id(study.getId())
                .title(study.getTitle())
                .content(study.getContent())
                .leaderNickname(leaderNickname)
                .maxMember(study.getMaxMember())
                .currentMemberCount(memberCount)
                .build();
    }
}

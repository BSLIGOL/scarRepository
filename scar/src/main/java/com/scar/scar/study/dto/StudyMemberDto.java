package com.scar.scar.study.dto;

import com.scar.scar.study.domain.StudyMember;
import com.scar.scar.study.domain.StudyRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyMemberDto {
    private Long userId;
    private String nickName;
    private StudyRole role;

    public static StudyMemberDto from(StudyMember member) {
        return StudyMemberDto.builder()
                .userId(member.getUser().getId())
                .nickName(member.getUser().getNickName())
                .role(member.getStudyRole())
                .build();
    }
}

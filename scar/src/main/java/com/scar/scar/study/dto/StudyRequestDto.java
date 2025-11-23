package com.scar.scar.study.dto;

import com.scar.scar.study.domain.Study;
import com.scar.scar.user.domain.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRequestDto {
    private String title;
    private String content;
    private int maxMember;

    public Study toEntity(User creator) {
        return Study.builder()
                .title(this.title)
                .content(this.content)
                .maxMember(this.maxMember)
                .creator(creator)
                .build();
    }
}


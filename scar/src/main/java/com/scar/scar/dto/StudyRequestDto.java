package com.scar.scar.dto;

import com.scar.scar.domain.Study;
import com.scar.scar.domain.User;
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

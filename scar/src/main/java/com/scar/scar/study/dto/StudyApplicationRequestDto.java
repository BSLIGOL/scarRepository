package com.scar.scar.study.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyApplicationRequestDto {
    private Long applicationId;
    private Long studyId;
    private Long userId;
    private String message;
}


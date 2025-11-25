package com.scar.scar.schedule.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestDto {
    private Long studyId;
    private String title;
    private String content;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String location;
}


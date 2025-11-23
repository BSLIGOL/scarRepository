package com.scar.scar.schedule.dto;


import com.scar.scar.schedule.domain.Schedule;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;

    public static ScheduleDto from(Schedule s) {
        return ScheduleDto.builder()
                .id(s.getId())
                .title(s.getTitle())
                .content(s.getContent())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .location(s.getLocation())
                .build();
    }
}


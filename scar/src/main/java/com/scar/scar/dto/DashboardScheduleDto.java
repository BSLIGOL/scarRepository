package com.scar.scar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardScheduleDto {
    private Long id;
    private String studyName;
    private String name;
    private String date; // YYYY-MM-DD 형식
    private String time; // HH:MM 형식 (오늘의 일정용)
    private String location;

    public static DashboardScheduleDto from(com.scar.scar.domain.Schedule schedule) {
        return DashboardScheduleDto.builder()
                .id(schedule.getId())
                .studyName(schedule.getStudy().getTitle())
                .name(schedule.getTitle())
                .date(schedule.getStartTime().toLocalDate().toString())
                .time(schedule.getStartTime().toLocalTime().toString().substring(0, 5))
                .location(schedule.getLocation())
                .build();
    }
}

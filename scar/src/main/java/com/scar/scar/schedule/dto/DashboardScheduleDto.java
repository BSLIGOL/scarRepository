package com.scar.scar.schedule.dto;

import com.scar.scar.schedule.dto.DashboardScheduleDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardScheduleDto {
    private Long id;
    private String studyName;
    private String name;
    private String date; // YYYY-MM-DD ??耀붾굝??????????⑤８??
    private String time; // HH:MM ??耀붾굝??????????⑤８??(?????꿔꺂????????????롮쾸?椰????
    private String location;

    public static DashboardScheduleDto from(com.scar.scar.schedule.domain.Schedule schedule) {
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


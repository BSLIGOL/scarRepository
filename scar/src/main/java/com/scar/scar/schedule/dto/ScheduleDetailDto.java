package com.scar.scar.schedule.dto;

import com.scar.scar.schedule.domain.Schedule;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDetailDto {
    private Long id;
    private String title;
    private String content;
    private Long studyId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String creatorNickName;
    private List<AttendanceMemberDto> members;

    @JsonProperty("isLeader")
    private boolean isLeader;

    public static ScheduleDetailDto of(Schedule schedule, List<AttendanceMemberDto> members, boolean isLeader) {
        return ScheduleDetailDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .content(schedule.getContent())
                .studyId(schedule.getStudy().getId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .location(schedule.getLocation())
                .creatorNickName(schedule.getCreatedBy().getNickName())
                .members(members)
                .isLeader(isLeader)
                .build();
    }

}

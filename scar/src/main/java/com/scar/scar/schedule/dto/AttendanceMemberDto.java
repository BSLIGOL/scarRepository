    package com.scar.scar.schedule.dto;

    import com.scar.scar.schedule.domain.AttendanceStatus;
    import lombok.*;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class AttendanceMemberDto {
        private Long userId;
        private String nickName;
        private AttendanceStatus status;
    }


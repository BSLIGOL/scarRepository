    package com.scar.scar.dto;

    import com.scar.scar.domain.enums.AttendanceStatus;
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

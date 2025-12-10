package com.scar.scar.study.dto;

import com.scar.scar.study.domain.StudyApplication;
import com.scar.scar.study.domain.ApplyStatus;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class StudyApplicationResponseDto {
    private Long id; // 스터디 신청 ID
    private Long userId; // 신청자 ID
    private String nickName; // 신청자 닉네임 (신청 당시)
    private String message; // 신청 메시지
    private ApplyStatus status;// 신청 상태 (PENDING, APPROVED 등)

    public static StudyApplicationResponseDto from(StudyApplication app) {
        return StudyApplicationResponseDto.builder()
                .id(app.getId())
                .userId(app.getUser().getId())
                .nickName(app.getUser().getNickName())
                .message(app.getMessage())
                .status(app.getStatus())
                .build();
    }
}

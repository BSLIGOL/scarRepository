package com.scar.scar.dto;
import com.scar.scar.domain.StudyApplication;
import com.scar.scar.domain.enums.ApplyStatus;
import lombok.*;
@Getter
@Builder
@AllArgsConstructor
public class StudyApplicationResponseDto {
    private Long id;           // 신청 ID
    private Long userId;       // 신청자 ID
    private String nickName;   // 신청자 닉네임 (중요!)
    private String message;    // 메시지
    private ApplyStatus status;// 상태 (PENDING, APPROVED 등)
    // 엔티티 -> DTO 변환 메서드 (편의상 추가)
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
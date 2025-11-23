-- Users Table
DROP TABLE IF EXISTS `schedule`; -- Remove old table causing conflicts
CREATE TABLE `users` (
	`id`	BIGINT	NOT NULL	AUTO_INCREMENT COMMENT 'PK',
	`email`	VARCHAR(100)	NOT NULL	UNIQUE COMMENT '사용자 이메일, 유니크',
	`password`	VARCHAR(255)	NOT NULL	COMMENT '로그인 비밀번호',
	`nick_name`	VARCHAR(50)	NOT NULL	UNIQUE COMMENT '유니크 닉네임',
	`global_role`	ENUM('USER','ADMIN')	NOT NULL	COMMENT '회원 권한 (ADMIN, USER)',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '생성 시각',
	`updated_at`	DATETIME	NULL	COMMENT '수정 시각',
	`deleted_at`	DATETIME	NULL	COMMENT '삭제 시각',
	PRIMARY KEY (`id`)
);

-- Studies Table
CREATE TABLE `studies` (
	`id`	BIGINT	NOT NULL	AUTO_INCREMENT COMMENT 'PK',
	`title`	VARCHAR(100)	NOT NULL	COMMENT '스터디 제목',
	`content`	TEXT	NOT NULL	COMMENT '스터디 내용',
	`max_member`	INT	NOT NULL	COMMENT '최대 참여자 수',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '생성 시각',
	`updated_at`	DATETIME	NULL	COMMENT '수정 시각',
	`deleted_at`	DATETIME	NULL	COMMENT '삭제 시각',
	`creator_id`	BIGINT	NOT NULL	COMMENT '생성자 FK',
	PRIMARY KEY (`id`)
);

-- Study Application Table
CREATE TABLE `study_application` (
	`id`	BIGINT	NOT NULL	AUTO_INCREMENT COMMENT 'PK',
	`status`	ENUM('PENDING','APPROVED','REJECTED')	NOT NULL	DEFAULT 'PENDING'	COMMENT '신청 상태',
	`message`	TEXT	NULL	COMMENT '신청 메시지',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
	`updated_at`	DATETIME	NULL	COMMENT '수정 시각',
	`deleted_at`	DATETIME	NULL	COMMENT '삭제 시각',
	`study_id`	BIGINT	NOT NULL	COMMENT '스터디 FK',
	`user_id`	BIGINT	NOT NULL	COMMENT '신청자 FK',
	PRIMARY KEY (`id`)
);

-- Study Member Table
CREATE TABLE `study_member` (
	`id`	BIGINT	NOT NULL	AUTO_INCREMENT COMMENT 'PK',
	`joined_at`	DATETIME	NOT NULL	COMMENT '참여 시작일',
	`left_at`	DATETIME	NULL	COMMENT '탈퇴일',
	`study_role`	ENUM('LEADER','MEMBER')	NOT NULL	COMMENT '스터디 내 역할',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '생성 시각',
	`updated_at`	DATETIME	NULL	COMMENT '수정 시각',
	`deleted_at`	DATETIME	NULL	COMMENT '삭제 시각',
	`study_id`	BIGINT	NOT NULL	COMMENT '스터디 FK',
	`user_id`	BIGINT	NOT NULL	COMMENT '유저 FK',
	PRIMARY KEY (`id`)
);

-- Schedules Table
CREATE TABLE `schedules` (
	`id`	BIGINT	NOT NULL	AUTO_INCREMENT COMMENT 'PK',
	`title`	VARCHAR(255)	NOT NULL	COMMENT '일정 제목',
	`content`	TEXT	NOT NULL	COMMENT '일정 내용',
	`start_time`	DATETIME	NOT NULL	COMMENT '시작 시간',
	`end_time`	DATETIME	NOT NULL	COMMENT '종료 시간',
	`location`	VARCHAR(255)	NOT NULL	COMMENT '장소',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '생성 시각',
	`updated_at`	DATETIME	NULL	COMMENT '수정 시각',
	`deleted_at`	DATETIME	NULL	COMMENT '삭제 시각',
	`study_id`	BIGINT	NOT NULL	COMMENT '스터디 FK',
	`user_id`	BIGINT	NOT NULL	COMMENT '생성자 FK',
	PRIMARY KEY (`id`)
);

-- Schedule Attendance Table
CREATE TABLE `schedule_attendance` (
	`id`	BIGINT	NOT NULL	AUTO_INCREMENT COMMENT 'PK',
	`status`	ENUM('ATTENDED','ABSENT')	NOT NULL	COMMENT '출석 상태',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '생성 시각',
	`updated_at`	DATETIME	NULL	COMMENT '수정 시각',
	`deleted_at`	DATETIME	NULL	COMMENT '삭제 시각',
	`schedule_id`	BIGINT	NOT NULL	COMMENT '일정 FK',
	`study_member_id`	BIGINT	NOT NULL	COMMENT '참여자(스터디멤버) FK',
	PRIMARY KEY (`id`),
	UNIQUE KEY `uk_schedule_member` (`schedule_id`, `study_member_id`)
);

-- Foreign Key Constraints

-- Studies -> Users (Creator)
ALTER TABLE `studies` ADD CONSTRAINT `FK_studies_creator` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`);

-- Study Application -> Studies, Users
ALTER TABLE `study_application` ADD CONSTRAINT `FK_study_application_study` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `study_application` ADD CONSTRAINT `FK_study_application_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

-- Study Member -> Studies, Users
ALTER TABLE `study_member` ADD CONSTRAINT `FK_study_member_study` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `study_member` ADD CONSTRAINT `FK_study_member_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

-- Schedules -> Studies, Users
ALTER TABLE `schedules` ADD CONSTRAINT `FK_schedules_study` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`);
ALTER TABLE `schedules` ADD CONSTRAINT `FK_schedules_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

-- Schedule Attendance -> Schedules, Study Member
ALTER TABLE `schedule_attendance` ADD CONSTRAINT `FK_schedule_attendance_schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedules` (`id`);
ALTER TABLE `schedule_attendance` ADD CONSTRAINT `FK_schedule_attendance_member` FOREIGN KEY (`study_member_id`) REFERENCES `study_member` (`id`);

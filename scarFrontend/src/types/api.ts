export const AttendanceStatus = {
    ATTENDED: 'ATTENDED',
    ABSENT: 'ABSENT',
} as const;
export type AttendanceStatus = (typeof AttendanceStatus)[keyof typeof AttendanceStatus];

export const StudyRole = {
    LEADER: 'LEADER',
    MEMBER: 'MEMBER',
} as const;
export type StudyRole = (typeof StudyRole)[keyof typeof StudyRole];

export const ApplyStatus = {
    PENDING: 'PENDING',
    APPROVED: 'APPROVED',
    REJECTED: 'REJECTED',
} as const;
export type ApplyStatus = (typeof ApplyStatus)[keyof typeof ApplyStatus];

export interface User {
    id: number;
    email: string;
    nickName: string;
}

export interface AttendanceMemberDto {
    userId: number;
    nickName: string;
    status: AttendanceStatus;
}

export interface ScheduleDetailDto {
    id: number;
    title: string;
    content: string;
    studyId: number;
    startTime: string; // ISO Date string
    endTime: string; // ISO Date string
    location: string;
    creatorNickName: string;
    members: AttendanceMemberDto[];
}

export interface ScheduleDto {
    id: number;
    title: string;
    content: string;
    startTime: string;
    endTime: string;
    location: string;
}

export interface ScheduleRequestDto {
    studyId: number;
    title: string;
    content: string;
    startTime: string; // ISO Date string
    endTime: string; // ISO Date string
    location: string;
}

export interface StudyDetailDto {
    id: number;
    title: string;
    content: string;
    maxMember: number;
    creatorNickName: string;
    currentMemberCount: number;
    memberNickNames: string[];
    joinedByCurrentUser: boolean;
    appliedByCurrentUser: boolean;
}

export interface StudyRequestDto {
    title: string;
    content: string;
    maxMember: number;
}

export interface StudyResponseDto {
    id: number;
    title: string;
    content: string;
    maxMember: number;
    creatorNickName: string;
    currentMemberCount: number;
}

export interface UserRequestDto {
    email: string;
    password?: string; // Optional for update scenarios if needed
    nickName: string;
}

export interface StudyApplicationResponseDto {
    id: number;
    userId: number;
    nickName: string;
    studyId: number;
    status: ApplyStatus;
    message: string;
}

export interface DashboardStudyDto {
    id: number;
    name: string;
    memberCount: number;
    maxMembers: number;
}

export interface DashboardScheduleDto {
    id: number;
    studyName: string;
    name: string;
    date?: string; // YYYY-MM-DD 형식
    time?: string; // HH:MM 형식
    location: string;
}


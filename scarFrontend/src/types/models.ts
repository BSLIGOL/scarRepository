import { ApplyStatus } from './api';
import type { DashboardScheduleDto } from './api';

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

export const ApplicationStatus = {
    PENDING: 'PENDING',
    APPROVED: 'APPROVED',
    REJECTED: 'REJECTED',
} as const;
export type ApplicationStatus = (typeof ApplicationStatus)[keyof typeof ApplicationStatus];

export interface User {
    id: number;
    email: string;
    nickName: string;
}

export interface AttendanceMember {
    userId: number;
    nickName: string;
    status: AttendanceStatus;
}

export interface StudyMember {
    userId: number;
    nickName: string;
    role: StudyRole;
}

export interface ScheduleDetail {
    id: number;
    title: string;
    content: string;
    studyId: number;
    startTime: string; // ISO Date string
    endTime: string; // ISO Date string
    location: string;
    creatorNickName: string;
    members: AttendanceMember[];
    isLeader: boolean;
}

export interface Schedule {
    id: number;
    title: string;
    content: string;
    startTime: string;
    endTime: string;
    location: string;
}

export interface StudyDetail {
    id: number;
    title: string;
    content: string;
    maxMember: number;
    creatorNickName: string;
    currentMemberCount: number;
    members: StudyMember[];
    joinedByCurrentUser: boolean;
    appliedByCurrentUser: boolean;
    isLeader: boolean;
    schedules: DashboardScheduleDto[];
}

export interface Study {
    id: number;
    title: string;
    content: string;
    maxMember: number;
    creatorNickName: string;
    currentMemberCount: number;
}

export interface StudyApplication {
    id: number;
    userId: number;
    nickName: string;
    studyId: number;
    status: ApplicationStatus;
    message: string;
}

// Request types for API calls
export interface UserRequest {
    email: string;
    password?: string;
    nickName: string;
}

export interface StudyRequest {
    title: string;
    content: string;
    maxMember: number;
}

export interface ScheduleRequest {
    studyId: number;
    title: string;
    content: string;
    startTime: string; // ISO Date string
    endTime: string; // ISO Date string
    location: string;
}

// Dashboard types
export interface DashboardStudy {
    id: number;
    name: string;
    memberCount: number;
    maxMembers: number;
}

export interface DashboardSchedule {
    id: number;
    studyName: string;
    name: string;
    date?: string; // YYYY-MM-DD 형식
    time?: string; // HH:MM 형식
    location: string;
}

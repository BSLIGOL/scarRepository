// Frontend-specific types mapped from backend DTOs
// This is a best practice to decouple frontend from backend structure

export interface UserProfile {
  id: number;
  email: string;
  nickname: string; // Use consistent camelCase naming in frontend
}

export interface MyStudy {
  id: number;
  name: string;
  memberCount: number;
  maxMembers: number;
}

export interface UpcomingSchedule {
  id: number;
  studyName: string;
  name: string;
  date?: string; // YYYY-MM-DD format
  time?: string; // HH:MM format
  location: string;
}

export interface TodaySchedule {
  id: number;
  studyName: string;
  name: string;
  time?: string; // HH:MM format
  location: string;
}

// Calendar-related types
export interface CalendarSchedule {
  id: number;
  studyId: number;
  studyName: string;
  title: string;
  startTime: string; // ISO Date string
  endTime: string; // ISO Date string
  location: string;
}

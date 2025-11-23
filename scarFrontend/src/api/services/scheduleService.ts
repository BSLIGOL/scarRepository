import apiClient from '../client';
import type { ScheduleDetail, ScheduleRequest, AttendanceStatus, DashboardSchedule } from '../../types/models';

export const scheduleService = {
    getScheduleDetail: async (scheduleId: number) => {
        const response = await apiClient.get<ScheduleDetail>(`/schedules/${scheduleId}`);
        return response.data;
    },

    createSchedule: async (data: ScheduleRequest) => {
        const response = await apiClient.post('/schedules/new', data);
        return response.data;
    },

    attendSchedule: async (scheduleId: number, userId: number, status: AttendanceStatus) => {
        const response = await apiClient.post(`/attendance/${scheduleId}`, null, {
            params: { userId, status },
        });
        return response.data;
    },

    getUpcomingSchedules: async () => {
        const response = await apiClient.get<DashboardSchedule[]>('/schedules/upcoming');
        return response.data;
    },

    getTodaySchedules: async () => {
        const response = await apiClient.get<DashboardSchedule[]>('/schedules/today');
        return response.data;
    },

    getMySchedules: async (year?: number, month?: number) => {
        const params: any = {};
        if (year !== undefined) params.year = year;
        if (month !== undefined) params.month = month;
        const response = await apiClient.get<DashboardSchedule[]>('/schedules/my', { params });
        return response.data;
    },

    updateSchedule: async (id: number, data: ScheduleRequest) => {
        const response = await apiClient.put<ScheduleDetail>(`/schedules/${id}`, data);
        return response.data;
    },

    deleteSchedule: async (id: number) => {
        const response = await apiClient.delete(`/schedules/${id}`);
        return response.data;
    },
};

import apiClient from '../client';
import type { UserRequest, User } from '../../types/models';

export const authService = {
    join: async (data: UserRequest) => {
        const response = await apiClient.post('/join', data);
        return response.data;
    },

    // Login using JWT (HttpOnly Cookie)
    login: async (data: Pick<UserRequest, 'email' | 'password'>) => {
        const response = await apiClient.post('/auth/login', data);
        return response.data;
    },

    getMe: async () => {
        // Cache busting to prevent stale user data
        const response = await apiClient.get<User>(`/current-user?t=${new Date().getTime()}`);
        return response.data;
    },

    logout: async () => {
        const response = await apiClient.post('/auth/logout');
        return response.data;
    },

    updateProfile: async (data: { nickName: string }) => {
        const response = await apiClient.put<User>('/users/me', data);
        return response.data;
    },

    changePassword: async (data: { currentPassword: string; newPassword: string }) => {
        const response = await apiClient.put('/users/password', data);
        return response.data;
    },

    deleteAccount: async () => {
        const response = await apiClient.delete('/users/me');
        return response.data;
    }
};

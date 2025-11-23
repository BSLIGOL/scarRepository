import apiClient from '../client';
import type { UserRequest, User } from '../../types/models';

export const authService = {
    join: async (data: UserRequest) => {
        const response = await apiClient.post('/join', data);
        return response.data;
    },

    // Login is typically handled by Spring Security form login or a specific endpoint
    // If you make a custom JSON login endpoint:
    login: async (data: Pick<UserRequest, 'email' | 'password'>) => {
        const response = await apiClient.post('/login', data, {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
        return response.data;
    },

    getMe: async () => {
        const response = await apiClient.get<User>('/current-user');
        return response.data;
    },

    logout: async () => {
        const response = await apiClient.post('/logout');
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

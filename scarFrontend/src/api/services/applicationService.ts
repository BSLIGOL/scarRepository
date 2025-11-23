import apiClient from '../client';
import type { StudyApplication } from '../../types/models';

export const applicationService = {
    getApplications: async (studyId: number) => {
        const response = await apiClient.get<StudyApplication[]>(`/applications/${studyId}`);
        return response.data;
    },

    applyToStudy: async (studyId: number, message: string) => {
        const response = await apiClient.post(`/applications/${studyId}/apply`, { message });
        return response.data;
    },

    approveApplication: async (applicationId: number) => {
        const response = await apiClient.post(`/applications/approve/${applicationId}`);
        return response.data;
    },

    rejectApplication: async (applicationId: number) => {
        const response = await apiClient.post(`/applications/reject/${applicationId}`);
        return response.data;
    },
};

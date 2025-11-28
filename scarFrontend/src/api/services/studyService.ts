import apiClient from '../client';
import type { Study, StudyRequest, StudyDetail, DashboardStudy } from '../../types/models';

export const studyService = {
    getAllStudies: async () => {
        const response = await apiClient.get<Study[]>('/studies');
        return response.data;
    },

    createStudy: async (data: StudyRequest) => {
        const response = await apiClient.post('/studies/create', data);
        return response.data;
    },

    getStudyDetail: async (id: number) => {
        const response = await apiClient.get<StudyDetail>(`/studies/${id}`);
        return response.data;
    },

    getMyStudies: async () => {
        const response = await apiClient.get<DashboardStudy[]>('/studies/my');
        return response.data;
    },

    updateStudy: async (id: number, data: StudyRequest) => {
        const response = await apiClient.put<StudyDetail>(`/studies/${id}`, data);
        return response.data;
    },

    deleteStudy: async (id: number) => {
        const response = await apiClient.delete(`/studies/${id}`);
        return response.data;
    },

    delegateLeader: async (studyId: number, newLeaderId: number) => {
        const response = await apiClient.post(`/studies/${studyId}/delegate`, newLeaderId, {
            headers: {
                'Content-Type': 'application/json',
            },
        });
        return response.data;
    },
};

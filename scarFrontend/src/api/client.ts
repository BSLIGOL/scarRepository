import axios from 'axios';

const apiClient = axios.create({
    baseURL: '/api', // Use proxy
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // For handling cookies/sessions
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
    (response) => {
        // [Critical Fix] CloudFront Custom Error Page Interception Prevention
        // If API returns HTML (e.g. index.html due to 403/404 interception), treat it as error.
        if (typeof response.data === 'string' && response.data.trim().startsWith('<!DOCTYPE')) {
            return Promise.reject(new Error('API returned HTML instead of JSON (CloudFront Error Page Interception)'));
        }
        return response;
    },
    (error) => {
        if (error.response && error.response.status === 401) {
            // Unauthorized (access denied or not logged in)
            // It's handled by the caller or global router guards
        }
        return Promise.reject(error);
    }
);

export default apiClient;

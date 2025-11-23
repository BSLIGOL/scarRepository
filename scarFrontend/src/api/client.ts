import axios from 'axios';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080', // Spring Boot default port
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // For handling cookies/sessions
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            // Handle unauthorized access (e.g., redirect to login)
            console.error('Unauthorized access - redirecting to login');
            // window.location.href = '/login'; // Uncomment if you want auto-redirect
        }
        return Promise.reject(error);
    }
);

export default apiClient;

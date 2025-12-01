using System.Collections.Generic;
using System.Threading.Tasks;
using scarDesktop.Models;

namespace scarDesktop.Services
{
    public class StudyService
    {
        private readonly ApiService _apiService;

        public StudyService()
        {
            _apiService = new ApiService();
        }

        public async Task<List<DashboardStudy>> GetMyStudiesAsync()
        {
            return await _apiService.GetAsync<List<DashboardStudy>>("/studies/my");
        }

        public async Task<List<Study>> GetAllStudiesAsync()
        {
            return await _apiService.GetAsync<List<Study>>("/studies");
        }

        public async Task<StudyDetail> GetStudyDetailAsync(int id)
        {
            return await _apiService.GetAsync<StudyDetail>($"/studies/{id}");
        }

        public async Task<string> CreateStudyAsync(StudyRequest request)
        {
            // The backend might return the created study or just a success message.
            // Based on frontend service: const response = await apiClient.post('/studies/create', data);
            // Let's assume it returns the created study or we just need to know it succeeded.
            // For now, let's return string (response body) to be safe, or we can try to parse it if we know the response.
            // Frontend says: return response.data;
            // Let's try to return the response as string for now.
            return await _apiService.PostAsync<string>("/studies/create", request);
        }
    }
}

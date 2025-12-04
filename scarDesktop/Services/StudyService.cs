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
            
            
            
            
            
            
            return await _apiService.PostAsync<string>("/studies/create", request);
        }
    }
}

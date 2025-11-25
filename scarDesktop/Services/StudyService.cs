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
    }
}

using System.Threading.Tasks;

namespace scarDesktop.Services
{
    public class ApplicationService
    {
        private readonly ApiService _apiService;

        public ApplicationService()
        {
            _apiService = new ApiService();
        }

        public async Task<bool> ApplyToStudyAsync(int studyId, string message)
        {
            var data = new
            {
                message = message
            };

            
            
            
            

            
            
            
            
            

            var response = await _apiService.PostAsync<string>($"/studies/{studyId}/apply", data);
            return response != null; 
        }
    }
}

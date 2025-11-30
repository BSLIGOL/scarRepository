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

            // Assuming the endpoint is /studies/{studyId}/apply
            // Need to verify with frontend code.
            // In StudyDetailPage.tsx: applicationService.applyToStudy(study.id, applicationMessage);
            // In applicationService.ts: post(`/studies/${studyId}/apply`, { message })

            // Note: PostAsync might return null/default on failure or void.
            // We need to check response status.
            // Our ApiService.PostAsync returns T.
            // Let's assume it returns something or we just check for success.
            // If we use PostAsync<string>, it returns the response body as string.

            var response = await _apiService.PostAsync<string>($"/studies/{studyId}/apply", data);
            return response != null; // Assuming non-null response means success
        }
    }
}

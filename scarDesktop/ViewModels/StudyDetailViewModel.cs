using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using scarDesktop.Models;
using scarDesktop.Services;
using System;
using System.Threading.Tasks;

namespace scarDesktop.ViewModels
{
    public partial class StudyDetailViewModel : ObservableObject
    {
        private readonly StudyService _studyService;
        private readonly AuthService _authService; 

        [ObservableProperty]
        private StudyDetail _study;

        [ObservableProperty]
        private bool _isLoading;

        [ObservableProperty]
        private string _errorMessage;

        [ObservableProperty]
        private bool _isLeader;

        [ObservableProperty]
        private bool _canJoin;

        public StudyDetailViewModel()
        {
            _studyService = new StudyService();
            _authService = new AuthService();
            _applicationService = new ApplicationService();
        }

        [RelayCommand]
        public async Task LoadStudyDetailAsync(int studyId)
        {
            IsLoading = true;
            ErrorMessage = string.Empty;

            try
            {
                Study = await _studyService.GetStudyDetailAsync(studyId);
                if (Study != null)
                {
                    IsLeader = Study.IsLeader;
                    CanJoin = !Study.JoinedByCurrentUser && !Study.AppliedByCurrentUser;
                }
                else
                {
                    ErrorMessage = "?§ÌÑ∞???ïÎ≥¥Î•?Î∂àÎü¨?????ÜÏäµ?àÎã§.";
                }
            }
            catch (Exception ex)
            {
                ErrorMessage = $"?§Î•ò Î∞úÏÉù: {ex.Message}";
            }
            finally
            {
                IsLoading = false;
            }
        }

        private readonly ApplicationService _applicationService;

        [RelayCommand]
        public async Task JoinAsync()
        {
            if (Study == null) return;

            
            string message = "?¥Ïã¨???òÍ≤†?µÎãà??"; 

            try
            {
                bool success = await _applicationService.ApplyToStudyAsync(Study.Id, message);
                if (success)
                {
                    
                    await LoadStudyDetailAsync(Study.Id);
                }
                else
                {
                    ErrorMessage = "?§ÌÑ∞???†Ï≤≠???§Ìå®?àÏäµ?àÎã§.";
                }
            }
            catch (Exception ex)
            {
                ErrorMessage = $"?†Ï≤≠ ?§Î•ò: {ex.Message}";
            }
        }
    }
}

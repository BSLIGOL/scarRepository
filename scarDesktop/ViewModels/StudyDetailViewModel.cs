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
        private readonly AuthService _authService; // For user info if needed

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
                    ErrorMessage = "스터디 정보를 불러올 수 없습니다.";
                }
            }
            catch (Exception ex)
            {
                ErrorMessage = $"오류 발생: {ex.Message}";
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

            // TODO: Show dialog to get message
            string message = "열심히 하겠습니다!"; // Default message for now

            try
            {
                bool success = await _applicationService.ApplyToStudyAsync(Study.Id, message);
                if (success)
                {
                    // Refresh
                    await LoadStudyDetailAsync(Study.Id);
                }
                else
                {
                    ErrorMessage = "스터디 신청에 실패했습니다.";
                }
            }
            catch (Exception ex)
            {
                ErrorMessage = $"신청 오류: {ex.Message}";
            }
        }
    }
}

using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using scarDesktop.Models;
using scarDesktop.Services;
using System;
using System.Threading.Tasks;

namespace scarDesktop.ViewModels
{
    public partial class CreateStudyViewModel : ObservableObject
    {
        private readonly StudyService _studyService;

        [ObservableProperty]
        private string title;

        [ObservableProperty]
        private string content;

        [ObservableProperty]
        private int maxMember = 4; // Default value

        [ObservableProperty]
        private bool isLoading;

        [ObservableProperty]
        private string errorMessage;

        public event EventHandler StudyCreated;

        public CreateStudyViewModel()
        {
            _studyService = new StudyService();
            CreateStudyCommand = new AsyncRelayCommand(CreateStudyAsync);
        }

        public IAsyncRelayCommand CreateStudyCommand { get; }

        private async Task CreateStudyAsync()
        {
            if (string.IsNullOrWhiteSpace(Title) || string.IsNullOrWhiteSpace(Content))
            {
                ErrorMessage = "제목과 내용을 모두 입력해주세요.";
                return;
            }

            if (MaxMember < 2)
            {
                ErrorMessage = "최대 인원은 2명 이상이어야 합니다.";
                return;
            }

            IsLoading = true;
            ErrorMessage = string.Empty;

            try
            {
                var request = new StudyRequest
                {
                    Title = Title,
                    Content = Content,
                    MaxMember = MaxMember
                };

                await _studyService.CreateStudyAsync(request);
                StudyCreated?.Invoke(this, EventArgs.Empty);
            }
            catch (Exception ex)
            {
                ErrorMessage = $"스터디 생성 실패: {ex.Message}";
            }
            finally
            {
                IsLoading = false;
            }
        }
    }
}

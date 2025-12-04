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
        private int maxMember = 4; 

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
                ErrorMessage = "?úÎ™©Í≥??¥Ïö©??Î™®Îëê ?ÖÎ†•?¥Ï£º?∏Ïöî.";
                return;
            }

            if (MaxMember < 2)
            {
                ErrorMessage = "ÏµúÎ? ?∏Ïõê?Ä 2Î™??¥ÏÉÅ?¥Ïñ¥???©Îãà??";
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
                ErrorMessage = $"?§ÌÑ∞???ùÏÑ± ?§Ìå®: {ex.Message}";
            }
            finally
            {
                IsLoading = false;
            }
        }
    }
}

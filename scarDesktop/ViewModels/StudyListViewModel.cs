using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using scarDesktop.Models;
using scarDesktop.Services;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace scarDesktop.ViewModels
{
    public partial class StudyListViewModel : ObservableObject
    {
        private readonly StudyService _studyService;

        [ObservableProperty]
        private ObservableCollection<Study> studies;

        [ObservableProperty]
        private bool isLoading;

        public StudyListViewModel()
        {
            _studyService = new StudyService();
            Studies = new ObservableCollection<Study>();
            LoadStudiesCommand = new AsyncRelayCommand(LoadStudiesAsync);
        }

        public IAsyncRelayCommand LoadStudiesCommand { get; }

        public async Task LoadStudiesAsync()
        {
            if (IsLoading) return;

            IsLoading = true;
            try
            {
                var studyList = await _studyService.GetAllStudiesAsync();
                Studies.Clear();
                if (studyList != null)
                {
                    foreach (var study in studyList)
                    {
                        Studies.Add(study);
                    }
                }
            }
            finally
            {
                IsLoading = false;
            }
        }
    }
}

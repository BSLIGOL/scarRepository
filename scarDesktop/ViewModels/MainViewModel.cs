using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using scarDesktop.Models;
using scarDesktop.Services;
using System.Collections.ObjectModel;
using System.Threading.Tasks;

namespace scarDesktop.ViewModels
{
    public partial class MainViewModel : ObservableObject
    {
        private readonly StudyService _studyService;

        [ObservableProperty]
        private ObservableCollection<DashboardStudy> myStudies;

        [ObservableProperty]
        private bool isLoading;

        public MainViewModel()
        {
            _studyService = new StudyService();
            MyStudies = new ObservableCollection<DashboardStudy>();
            LoadDataCommand = new AsyncRelayCommand(LoadDataAsync);
        }

        public IAsyncRelayCommand LoadDataCommand { get; }

        private async Task LoadDataAsync()
        {
            IsLoading = true;
            try
            {
                var studies = await _studyService.GetMyStudiesAsync();
                MyStudies.Clear();
                if (studies != null)
                {
                    foreach (var study in studies)
                    {
                        MyStudies.Add(study);
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

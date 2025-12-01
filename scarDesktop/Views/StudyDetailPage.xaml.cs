using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using Microsoft.UI.Xaml.Navigation;
using scarDesktop.ViewModels;

namespace scarDesktop.Views
{
    public sealed partial class StudyDetailPage : Page
    {
        public StudyDetailViewModel ViewModel => (StudyDetailViewModel)DataContext;

        public StudyDetailPage()
        {
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
            if (e.Parameter is int studyId)
            {
                ViewModel.LoadStudyDetailCommand.Execute(studyId);
            }
        }

        private void BackButton_Click(object sender, RoutedEventArgs e)
        {
            if (Frame.CanGoBack)
            {
                Frame.GoBack();
            }
            else
            {
                Frame.Navigate(typeof(StudyListPage));
            }
        }

        private void DashboardButton_Click(object sender, RoutedEventArgs e)
        {
            Frame.Navigate(typeof(MainPage));
        }
    }
}

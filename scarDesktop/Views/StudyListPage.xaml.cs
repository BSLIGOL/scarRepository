using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using Microsoft.UI.Xaml.Navigation;
using scarDesktop.Models;
using scarDesktop.ViewModels;

namespace scarDesktop.Views
{
    public sealed partial class StudyListPage : Page
    {
        public StudyListViewModel ViewModel => (StudyListViewModel)DataContext;

        public StudyListPage()
        {
            this.InitializeComponent();
        }

        protected override async void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
            await ViewModel.LoadStudiesAsync();
        }

        private void ListView_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (e.AddedItems.Count > 0)
            {
                var selectedStudy = (Study)e.AddedItems[0];
                Frame.Navigate(typeof(StudyDetailPage), selectedStudy.Id);

                // Reset selection
                ((ListView)sender).SelectedItem = null;
            }
        }

        private void CreateStudyButton_Click(object sender, RoutedEventArgs e)
        {
            Frame.Navigate(typeof(CreateStudyPage));
        }



        private void DashboardButton_Click(object sender, RoutedEventArgs e)
        {
            Frame.Navigate(typeof(MainPage));
        }
    }
}

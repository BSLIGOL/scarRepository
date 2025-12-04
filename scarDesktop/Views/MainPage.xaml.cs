using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using Microsoft.UI.Xaml.Navigation;
using scarDesktop.ViewModels;

namespace scarDesktop.Views
{
    public sealed partial class MainPage : Page
    {
        public MainViewModel ViewModel => (MainViewModel)DataContext;

        public MainPage()
        {
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            System.Console.WriteLine("[MainPage] OnNavigatedTo called");
            base.OnNavigatedTo(e);
            
            ViewModel.LoadDataCommand.Execute(null);
        }
        private void MainPage_Loaded(object sender, RoutedEventArgs e)
        {
            if (ViewModel.LoadDataCommand.CanExecute(null))
            {
                ViewModel.LoadDataCommand.Execute(null);
            }
        }
        private void FindStudyButton_Click(object sender, RoutedEventArgs e)
        {
            Frame.Navigate(typeof(StudyListPage));
        }

        private void LogoutButton_Click(object sender, RoutedEventArgs e)
        {
            var authService = new Services.AuthService();
            authService.Logout();
            Frame.Navigate(typeof(LoginPage));
        }

        private void GridView_ItemClick(object sender, ItemClickEventArgs e)
        {
            if (e.ClickedItem is Models.DashboardStudy study)
            {
                Frame.Navigate(typeof(StudyDetailPage), study.Id);
            }
        }
    }
}

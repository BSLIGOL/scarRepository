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
            // 페이지 진입 시 데이터 로드
            ViewModel.LoadDataCommand.Execute(null);
        }
    }
}

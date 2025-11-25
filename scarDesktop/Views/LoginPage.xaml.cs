using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using scarDesktop.ViewModels;

namespace scarDesktop.Views
{
    public sealed partial class LoginPage : Page
    {
        public LoginViewModel ViewModel => (LoginViewModel)DataContext;

        public LoginPage()
        {
            this.InitializeComponent();
            ViewModel.LoginSuccess += ViewModel_LoginSuccess;
        }

        private void ViewModel_LoginSuccess(object sender, System.EventArgs e)
        {
            System.Console.WriteLine("[LoginPage] ViewModel_LoginSuccess received. Navigating to MainPage.");
            Frame.Navigate(typeof(MainPage));
        }

        private void PasswordBox_PasswordChanged(object sender, RoutedEventArgs e)
        {
            if (this.DataContext is LoginViewModel viewModel)
            {
                viewModel.Password = ((PasswordBox)sender).Password;
            }
        }
    }
}

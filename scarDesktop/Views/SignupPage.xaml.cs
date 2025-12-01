using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using scarDesktop.ViewModels;

namespace scarDesktop.Views
{
    public sealed partial class SignupPage : Page
    {
        public SignupViewModel ViewModel => (SignupViewModel)DataContext;

        public SignupPage()
        {
            this.InitializeComponent();
            ViewModel.SignupSuccess += ViewModel_SignupSuccess;
        }

        private void ViewModel_SignupSuccess(object sender, System.EventArgs e)
        {
            // 회원가입 성공 시 로그인 페이지로 이동
            Frame.Navigate(typeof(LoginPage));
        }

        private void PasswordBox_PasswordChanged(object sender, RoutedEventArgs e)
        {
            if (this.DataContext is SignupViewModel viewModel)
            {
                viewModel.Password = ((PasswordBox)sender).Password;
            }
        }

        private void ConfirmPasswordBox_PasswordChanged(object sender, RoutedEventArgs e)
        {
            if (this.DataContext is SignupViewModel viewModel)
            {
                viewModel.ConfirmPassword = ((PasswordBox)sender).Password;
            }
        }

        private void LoginButton_Click(object sender, RoutedEventArgs e)
        {
            Frame.Navigate(typeof(LoginPage));
        }
    }
}

using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using scarDesktop.Services;
using System;
using System.Threading.Tasks;

namespace scarDesktop.ViewModels
{
    public partial class LoginViewModel : ObservableObject
    {
        private readonly AuthService _authService;

        [ObservableProperty]
        private string email;

        [ObservableProperty]
        private string password;

        [ObservableProperty]
        private string errorMessage;

        [ObservableProperty]
        private bool isLoading;

        public event EventHandler LoginSuccess;

        public LoginViewModel()
        {
            _authService = new AuthService();
        }

        [RelayCommand]
        public async Task Login()
        {
            Console.WriteLine("[LoginViewModel] Login command executed");
            if (IsLoading) return;

            if (string.IsNullOrWhiteSpace(Email) || string.IsNullOrWhiteSpace(Password))
            {
                ErrorMessage = "이메일과 비밀번호를 입력해주세요.";
                return;
            }

            IsLoading = true;
            ErrorMessage = string.Empty;

            try
            {
                Console.WriteLine("[LoginViewModel] Calling AuthService.LoginAsync");
                var result = await _authService.LoginAsync(Email, Password);
                Console.WriteLine($"[LoginViewModel] Login result - IsSuccess: {result.IsSuccess}, Error: {result.ErrorMessage}");

                if (result.IsSuccess)
                {
                    Console.WriteLine("[LoginViewModel] Invoking LoginSuccess event");
                    LoginSuccess?.Invoke(this, EventArgs.Empty);
                }
                else
                {
                    ErrorMessage = result.ErrorMessage;
                    Console.WriteLine($"[LoginViewModel] Login failed: {ErrorMessage}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[LoginViewModel] Exception during login: {ex.Message}");
                Console.WriteLine($"[LoginViewModel] Exception stack trace: {ex.StackTrace}");
                ErrorMessage = $"로그인 중 오류가 발생했습니다: {ex.Message}";
            }
            finally
            {
                IsLoading = false;
                Console.WriteLine("[LoginViewModel] IsLoading set to false");
            }
        }
    }
}

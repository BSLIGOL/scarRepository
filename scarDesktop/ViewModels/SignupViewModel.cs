using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using scarDesktop.Services;
using System;
using System.Threading.Tasks;

namespace scarDesktop.ViewModels
{
    public partial class SignupViewModel : ObservableObject
    {
        private readonly AuthService _authService;

        [ObservableProperty]
        private string email;

        [ObservableProperty]
        private string password;

        [ObservableProperty]
        private string confirmPassword;

        [ObservableProperty]
        private string nickName;

        [ObservableProperty]
        private string errorMessage;

        [ObservableProperty]
        private bool isLoading;

        public event EventHandler SignupSuccess;

        public SignupViewModel()
        {
            _authService = new AuthService();
        }

        [RelayCommand]
        public async Task Signup()
        {
            if (IsLoading) return;

            if (string.IsNullOrWhiteSpace(Email) || string.IsNullOrWhiteSpace(Password) || string.IsNullOrWhiteSpace(NickName))
            {
                ErrorMessage = "모든 필드를 입력해주세요.";
                return;
            }

            if (Password != ConfirmPassword)
            {
                ErrorMessage = "비밀번호가 일치하지 않습니다.";
                return;
            }

            if (Password.Length < 6)
            {
                ErrorMessage = "비밀번호는 최소 6자 이상이어야 합니다.";
                return;
            }

            IsLoading = true;
            ErrorMessage = string.Empty;

            try
            {
                var result = await _authService.SignupAsync(Email, Password, NickName);

                if (result.IsSuccess)
                {
                    SignupSuccess?.Invoke(this, EventArgs.Empty);
                }
                else
                {
                    ErrorMessage = result.ErrorMessage;
                }
            }
            catch (Exception ex)
            {
                ErrorMessage = $"회원가입 중 오류가 발생했습니다: {ex.Message}";
            }
            finally
            {
                IsLoading = false;
            }
        }
    }
}

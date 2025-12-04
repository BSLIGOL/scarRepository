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
                ErrorMessage = "ëª¨ë“  ?„ë“œë¥??…ë ¥?´ì£¼?¸ìš”.";
                return;
            }

            if (Password != ConfirmPassword)
            {
                ErrorMessage = "ë¹„ë?ë²ˆí˜¸ê°€ ?¼ì¹˜?˜ì? ?ŠìŠµ?ˆë‹¤.";
                return;
            }

            if (Password.Length < 6)
            {
                ErrorMessage = "ë¹„ë?ë²ˆí˜¸??ìµœì†Œ 6???´ìƒ?´ì–´???©ë‹ˆ??";
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
                ErrorMessage = $"?Œì›ê°€??ì¤??¤ë¥˜ê°€ ë°œìƒ?ˆìŠµ?ˆë‹¤: {ex.Message}";
            }
            finally
            {
                IsLoading = false;
            }
        }
    }
}

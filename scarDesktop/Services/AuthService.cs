using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using scarDesktop.Models;

namespace scarDesktop.Services
{
    public class AuthService
    {
        private readonly ApiService _apiService;
        public static string CurrentToken { get; private set; }

        public AuthService()
        {
            _apiService = new ApiService();
        }

        public async Task<(bool IsSuccess, string ErrorMessage)> LoginAsync(string email, string password)
        {
            Console.WriteLine($"[AuthService] LoginAsync called for {email}");
            try
            {
                var loginData = new Dictionary<string, string>
                {
                    { "email", email },
                    { "password", password }
                };

                var response = await _apiService.LoginWithFormAsync("/login", loginData);
                Console.WriteLine($"[AuthService] Response Status: {response.StatusCode}");
                Console.WriteLine($"[AuthService] Final URL: {response.RequestMessage?.RequestUri}");

                // 302 Found(리다이렉트)인 경우 처리
                if (response.StatusCode == System.Net.HttpStatusCode.Found)
                {
                    var location = response.Headers.Location?.ToString();
                    Console.WriteLine($"[AuthService] Redirect Location: {location}");

                    // Spring Security 로그인 실패 시 /login?error로 리다이렉트됨
                    if (location?.Contains("error") == true)
                    {
                        Console.WriteLine("[AuthService] Login failed - redirected to error page");
                        return (false, "로그인 실패: 이메일 또는 비밀번호가 올바르지 않습니다.");
                    }

                    // 다른 곳으로 리다이렉트되면 로그인 성공
                    Console.WriteLine("[AuthService] Login succeeded - redirected to success page");
                    return (true, string.Empty);
                }

                // 200 OK면 성공
                if (response.IsSuccessStatusCode)
                {
                    Console.WriteLine("[AuthService] Login succeeded - 200 OK");
                    return (true, string.Empty);
                }

                // 그 외의 경우는 실패
                Console.WriteLine($"[AuthService] Login failed with status: {response.StatusCode}");
                return (false, $"로그인 실패: 서버 응답 {response.StatusCode}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[AuthService] Login error: {ex.Message}");
                return (false, $"Exception: {ex.Message}");
            }
        }
    }
}

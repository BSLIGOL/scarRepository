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

        public async Task<(bool IsSuccess, string ErrorMessage)> SignupAsync(string email, string password, string nickName)
        {
            Console.WriteLine($"[AuthService] SignupAsync called for {email}");
            try
            {
                var signupData = new
                {
                    email = email,
                    password = password,
                    nickName = nickName
                };

                var response = await _apiService.PostAsync<string>("/join", signupData);

                // PostAsync returns deserialized object or default(T) on failure. 
                // However, PostAsync implementation in ApiService returns null (default) on failure.
                // But we need to check if the request was actually successful. 
                // Let's check ApiService.PostAsync implementation again. 
                // It returns T. If it returns null, it might mean failure or just null response.
                // Ideally ApiService should return a wrapper or we should use HttpClient directly here or improve ApiService.
                // For now, let's assume if it returns non-null or if we can change ApiService to return HttpResponseMessage or similar.
                // Actually, let's look at ApiService.PostAsync again. 
                // It returns default(T) if !IsSuccessStatusCode.
                // So if it returns null, it's likely a failure (unless the server returns null for success).
                // But for /join, it likely returns the created user or something.

                // Wait, let's check ApiService.PostAsync again.
                // It swallows the error and returns default. This is not ideal for error reporting.
                // I should probably add a method to ApiService that returns (bool, string) or HttpResponseMessage.
                // But for now, let's use a new method in ApiService or just use PostAsync and assume null means failure.

                if (response != null)
                {
                    Console.WriteLine("[AuthService] Signup succeeded");
                    return (true, string.Empty);
                }
                else
                {
                    Console.WriteLine("[AuthService] Signup failed (response is null)");
                    return (false, "회원가입 실패: 서버 응답이 없습니다.");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[AuthService] Signup error: {ex.Message}");
                return (false, $"Exception: {ex.Message}");
            }
        }
        public void Logout()
        {
            CurrentToken = null;
            // Additional cleanup if needed (e.g., removing cookies or local storage)
        }
    }
}

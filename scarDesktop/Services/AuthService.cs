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

                
                if (response.StatusCode == System.Net.HttpStatusCode.Found)
                {
                    var location = response.Headers.Location?.ToString();
                    Console.WriteLine($"[AuthService] Redirect Location: {location}");

                    
                    if (location?.Contains("error") == true)
                    {
                        Console.WriteLine("[AuthService] Login failed - redirected to error page");
                        return (false, "Î°úÍ∑∏???§Ìå®: ?¥Î©î???êÎäî ÎπÑÎ?Î≤àÌò∏Í∞Ä ?¨Î∞îÎ•¥Ï? ?äÏäµ?àÎã§.");
                    }

                    
                    Console.WriteLine("[AuthService] Login succeeded - redirected to success page");
                    return (true, string.Empty);
                }

                
                if (response.IsSuccessStatusCode)
                {
                    Console.WriteLine("[AuthService] Login succeeded - 200 OK");
                    return (true, string.Empty);
                }

                
                Console.WriteLine($"[AuthService] Login failed with status: {response.StatusCode}");
                return (false, $"Î°úÍ∑∏???§Ìå®: ?úÎ≤Ñ ?ëÎãµ {response.StatusCode}");
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

                
                
                
                
                
                
                
                
                
                
                

                
                
                
                

                if (response != null)
                {
                    Console.WriteLine("[AuthService] Signup succeeded");
                    return (true, string.Empty);
                }
                else
                {
                    Console.WriteLine("[AuthService] Signup failed (response is null)");
                    return (false, "?åÏõêÍ∞Ä???§Ìå®: ?úÎ≤Ñ ?ëÎãµ???ÜÏäµ?àÎã§.");
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
            
        }
    }
}

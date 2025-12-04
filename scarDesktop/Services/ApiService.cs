using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace scarDesktop.Services
{
    public class ApiService
    {
        private static readonly CookieContainer _cookieContainer = new CookieContainer();
        private static readonly HttpClient _httpClient;
        private const string BaseUrl = "http://localhost:8080";

        static ApiService()
        {
            var handler = new HttpClientHandler
            {
                CookieContainer = _cookieContainer,
                UseCookies = true,
                AllowAutoRedirect = false
            };
            _httpClient = new HttpClient(handler);
        }

        public ApiService()
        {
            
        }

        public async Task<T> GetAsync<T>(string endpoint)
        {
            var response = await _httpClient.GetAsync($"{BaseUrl}{endpoint}");

            if (!response.IsSuccessStatusCode)
            {
                return default;
            }

            var responseJson = await response.Content.ReadAsStringAsync();
            var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
            return JsonSerializer.Deserialize<T>(responseJson, options);
        }

        public async Task<T> PostAsync<T>(string endpoint, object data)
        {
            var json = JsonSerializer.Serialize(data);
            var content = new StringContent(json, Encoding.UTF8, "application/json");

            var response = await _httpClient.PostAsync($"{BaseUrl}{endpoint}", content);

            
            if (!response.IsSuccessStatusCode)
            {
                return default;
            }

            var responseString = await response.Content.ReadAsStringAsync();

            
            if (string.IsNullOrWhiteSpace(responseString))
            {
                return default;
            }

            try
            {
                var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
                return JsonSerializer.Deserialize<T>(responseString, options);
            }
            catch (JsonException)
            {
                
                
                if (typeof(T) == typeof(string))
                {
                    return (T)(object)responseString;
                }

                
                
                System.Console.WriteLine($"[ApiService] JSON parsing failed for response: {responseString}");
                return default;
            }
        }

        public async Task<HttpResponseMessage> LoginWithFormAsync(string endpoint, Dictionary<string, string> data)
        {
            var content = new FormUrlEncodedContent(data);
            return await _httpClient.PostAsync($"{BaseUrl}{endpoint}", content);
        }
    }
}

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
            // HttpClient는 static으로 공유하므로 생성자에서 할 일 없음
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

            // 에러 처리 (간단하게)
            if (!response.IsSuccessStatusCode)
            {
                return default;
            }

            var responseJson = await response.Content.ReadAsStringAsync();
            var options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
            return JsonSerializer.Deserialize<T>(responseJson, options);
        }

        public async Task<HttpResponseMessage> LoginWithFormAsync(string endpoint, Dictionary<string, string> data)
        {
            var content = new FormUrlEncodedContent(data);
            return await _httpClient.PostAsync($"{BaseUrl}{endpoint}", content);
        }
    }
}

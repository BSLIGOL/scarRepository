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

            var responseString = await response.Content.ReadAsStringAsync();

            // 응답이 비어있으면 default 반환
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
                // JSON 파싱 실패 시 (단순 문자열 반환 등)
                // T가 string이면 문자열 그대로 반환
                if (typeof(T) == typeof(string))
                {
                    return (T)(object)responseString;
                }

                // T가 object이고 응답이 단순 문자열인 경우, 문자열을 담은 익명 객체나 딕셔너리로 반환하거나
                // 여기서는 그냥 null 반환하고 로그 남김 (혹은 예외 처리)
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

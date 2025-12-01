using System.Text.Json.Serialization;

namespace scarDesktop.Models
{
    public class StudyRequest
    {
        [JsonPropertyName("title")]
        public string Title { get; set; }

        [JsonPropertyName("content")]
        public string Content { get; set; }

        [JsonPropertyName("maxMember")]
        public int MaxMember { get; set; }
    }
}

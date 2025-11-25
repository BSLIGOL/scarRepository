using System.Text.Json.Serialization;

namespace scarDesktop.Models
{
    public class DashboardStudy
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }

        [JsonPropertyName("name")]
        public string Name { get; set; }

        [JsonPropertyName("memberCount")]
        public int MemberCount { get; set; }

        [JsonPropertyName("maxMembers")]
        public int MaxMembers { get; set; }
    }
}

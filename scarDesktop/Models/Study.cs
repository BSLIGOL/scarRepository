namespace scarDesktop.Models
{
    public class Study
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public string Content { get; set; }
        public int MaxMember { get; set; }
        public string CreatorNickName { get; set; }
        public int CurrentMemberCount { get; set; }
    }
}

using System.Collections.Generic;

namespace scarDesktop.Models
{
    public class StudyDetail
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public string Content { get; set; }
        public int MaxMember { get; set; }
        public string CreatorNickName { get; set; }
        public int CurrentMemberCount { get; set; }
        public List<StudyMember> Members { get; set; }
        public bool JoinedByCurrentUser { get; set; }
        public bool AppliedByCurrentUser { get; set; }
        public bool IsLeader { get; set; }
        // Schedules are DashboardScheduleDto in frontend, let's check if we have it or need to create it.
        // We have DashboardSchedule in models.ts but maybe not in C# yet.
        // Let's use a generic list for now or create ScheduleDto.
        public List<DashboardSchedule> Schedules { get; set; }
    }

    public class StudyMember
    {
        public int UserId { get; set; }
        public string NickName { get; set; }
        public string Role { get; set; } // "LEADER" or "MEMBER"
    }

    public class DashboardSchedule
    {
        public int Id { get; set; }
        public string StudyName { get; set; }
        public string Name { get; set; }
        public string Date { get; set; }
        public string Time { get; set; }
        public string Location { get; set; }
    }
}

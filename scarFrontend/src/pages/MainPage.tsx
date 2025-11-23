import { useAuth } from '../contexts/AuthContext';
import { Link } from 'react-router-dom';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Calendar } from '../components/ui/calendar';
import { Calendar as CalendarIcon, Users, Clock, CheckCircle, MapPin } from 'lucide-react';
import { useState, useEffect, useMemo } from 'react';
import { ImageWithFallback } from '../components/figma/ImageWithFallback';
import { studyService } from '../api/services/studyService';
import { scheduleService } from '../api/services/scheduleService';
import type { DashboardStudy, DashboardSchedule } from '../types/models';
import { format } from 'date-fns';

export default function MainPage() {
  const { isAuthenticated } = useAuth();
  const [date, setDate] = useState<Date | undefined>(new Date());
  const [currentMonth, setCurrentMonth] = useState<Date>(new Date());

  // 상태 관리
  const [myStudies, setMyStudies] = useState<DashboardStudy[]>([]);
  const [upcomingSchedules, setUpcomingSchedules] = useState<DashboardSchedule[]>([]);
  const [todaySchedules, setTodaySchedules] = useState<DashboardSchedule[]>([]);
  const [monthSchedules, setMonthSchedules] = useState<DashboardSchedule[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 데이터 로딩
  useEffect(() => {
    if (isAuthenticated) {
      loadDashboardData();
    }
  }, [isAuthenticated]);

  // 월별 일정 로딩
  useEffect(() => {
    if (isAuthenticated) {
      loadMonthSchedules(currentMonth);
    }
  }, [isAuthenticated, currentMonth]);

  const loadDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [studies, upcoming, today] = await Promise.all([
        studyService.getMyStudies(),
        scheduleService.getUpcomingSchedules(),
        scheduleService.getTodaySchedules(),
      ]);
      setMyStudies(studies);
      setUpcomingSchedules(upcoming);
      setTodaySchedules(today);
    } catch (err) {
      console.error('데이터 로딩 실패:', err);
      setError('데이터를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const loadMonthSchedules = async (month: Date) => {
    try {
      const year = month.getFullYear();
      const monthNum = month.getMonth() + 1; // 0-indexed to 1-indexed
      const schedules = await scheduleService.getMySchedules(year, monthNum);
      setMonthSchedules(schedules);
    } catch (err) {
      console.error('월별 일정 로딩 실패:', err);
    }
  };

  // 달력에 표시할 일정 날짜들 (중복 제거)
  const scheduledDates = useMemo(() => {
    const dates = new Set<string>();
    monthSchedules.forEach(schedule => {
      if (schedule.date) {
        dates.add(schedule.date);
      }
    });
    return Array.from(dates).map(dateStr => new Date(dateStr));
  }, [monthSchedules]);

  // 선택된 날짜의 일정 필터링
  const selectedDateSchedules = useMemo(() => {
    if (!date) return [];
    const dateStr = format(date, 'yyyy-MM-dd');
    return monthSchedules.filter(
      schedule => schedule.date === dateStr
    );
  }, [date, monthSchedules]);

  if (!isAuthenticated) {
    return (
      <div className="max-w-6xl mx-auto">
        <div className="grid md:grid-cols-2 gap-8 items-center min-h-[600px]">
          <div className="space-y-6">
            <h1 className="text-4xl md:text-5xl">
              함께 성장하는
              <br />
              <span className="text-primary">스터디 플랫폼</span>
            </h1>
            <p className="text-lg text-muted-foreground">
              스터디 그룹을 만들고, 일정을 관리하며, 함께 목표를 달성하세요.
            </p>
            <div className="flex gap-4">
              <Link to="/signup">
                <Button size="lg">지금 시작하기</Button>
              </Link>
              <Link to="/studies">
                <Button size="lg" variant="outline">
                  스터디 둘러보기
                </Button>
              </Link>
            </div>
          </div>
          <div className="rounded-lg overflow-hidden shadow-2xl">
            <ImageWithFallback
              src="https://images.unsplash.com/photo-1758270705290-62b6294dd044?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzdHVkeSUyMGdyb3VwJTIwY29sbGFib3JhdGlvbnxlbnwxfHx8fDE3NjM1NzE5MjV8MA&ixlib=rb-4.1.0&q=80&w=1080"
              alt="Study group collaboration"
              className="w-full h-[500px] object-cover"
            />
          </div>
        </div>

        <div className="grid md:grid-cols-3 gap-6 mt-16">
          <Card>
            <CardHeader>
              <Users className="size-8 text-primary mb-2" />
              <CardTitle>스터디 그룹</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-muted-foreground">같은 목표를 가진 사람들과 함께 스터디 그룹을 만들어보세요.</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CalendarIcon className="size-8 text-primary mb-2" />
              <CardTitle>일정 관리</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-muted-foreground">스터디 일정을 체계적으로 관리하고 참석 여부를 확인하세요.</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CheckCircle className="size-8 text-primary mb-2" />
              <CardTitle>목표 달성</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-muted-foreground">꾸준한 스터디로 목표를 달성하고 성장하세요.</p>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto">
      <h1 className="text-3xl mb-8">대시보드</h1>

      {loading && (
        <div className="text-center py-8">
          <p className="text-muted-foreground">데이터를 불러오는 중...</p>
        </div>
      )}

      {error && (
        <div className="text-center py-8">
          <p className="text-red-600">{error}</p>
          <Button onClick={loadDashboardData} className="mt-4">다시 시도</Button>
        </div>
      )}

      {!loading && !error && (
        <>
          <div className="grid lg:grid-cols-3 gap-6">
            {/* Calendar Section */}
            <Card className="lg:col-span-2">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <CalendarIcon className="size-5" />
                  일정 캘린더
                </CardTitle>
              </CardHeader>
              <CardContent className="flex flex-col md:flex-row gap-6">
                <div className="flex-shrink-0">
                  <Calendar
                    mode="single"
                    selected={date}
                    onSelect={setDate}
                    onMonthChange={setCurrentMonth}
                    className="rounded-md border"
                    modifiers={{
                      booked: scheduledDates
                    }}
                    modifiersClassNames={{
                      booked: 'booked-date'
                    }}
                  />
                </div>
                <div className="flex-grow">
                  <h3 className="font-medium mb-4">
                    {date ? format(date, 'yyyy년 MM월 dd일') : '날짜를 선택하세요'} 일정
                  </h3>
                  <div className="space-y-3 max-h-[300px] overflow-y-auto pr-2">
                    {selectedDateSchedules.length > 0 ? (
                      selectedDateSchedules.map((schedule) => (
                        <Link key={schedule.id} to={`/schedules/${schedule.id}`}>
                          <div className="p-3 rounded-lg border hover:bg-accent transition-colors cursor-pointer">
                            <div className="flex justify-between items-start mb-1">
                              <div>
                                <p className="text-xs text-muted-foreground">{schedule.studyName}</p>
                                <p className="font-medium">{schedule.name}</p>
                              </div>
                              <span className="text-sm text-primary font-medium">{schedule.time}</span>
                            </div>
                            <div className="flex items-center gap-1 text-xs text-muted-foreground">
                              <MapPin className="size-3" />
                              {schedule.location}
                            </div>
                          </div>
                        </Link>
                      ))
                    ) : (
                      <div className="text-center py-8 text-muted-foreground bg-muted/30 rounded-lg">
                        해당 날짜에 예정된 일정이 없습니다.
                      </div>
                    )}
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* My Studies */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Users className="size-5" />
                  내 스터디
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {myStudies.length > 0 ? (
                  myStudies.map((study) => (
                    <Link key={study.id} to={`/studies/${study.id}`} className="block mb-2 last:mb-0">
                      <div className="p-3 rounded-lg border hover:bg-accent transition-colors cursor-pointer">
                        <div className="flex items-center justify-between">
                          <span>{study.name}</span>
                          <span className="text-sm text-muted-foreground">
                            {study.memberCount}/{study.maxMembers}
                          </span>
                        </div>
                      </div>
                    </Link>
                  ))
                ) : (
                  <div className="text-center py-4 text-muted-foreground">
                    가입한 스터디가 없습니다.
                  </div>
                )}
                <Link to="/studies/create">
                  <Button className="w-full mt-4" variant="outline">
                    + 새 스터디 만들기
                  </Button>
                </Link>
              </CardContent>
            </Card>
          </div>

          <div className="grid md:grid-cols-2 gap-6 mt-6">
            {/* Upcoming Schedules List */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Clock className="size-5" />
                  다가오는 일정
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {upcomingSchedules.length > 0 ? (
                  upcomingSchedules.slice(0, 5).map((schedule) => (
                    <Link key={schedule.id} to={`/schedules/${schedule.id}`}>
                      <div className="p-4 rounded-lg border hover:bg-accent transition-colors cursor-pointer">
                        <div className="flex justify-between items-start mb-2">
                          <div>
                            <p className="text-sm text-muted-foreground">{schedule.studyName}</p>
                            <p>{schedule.name}</p>
                          </div>
                          <span className="text-sm text-primary">{schedule.date}</span>
                        </div>
                        <p className="text-sm text-muted-foreground">{schedule.location}</p>
                      </div>
                    </Link>
                  ))
                ) : (
                  <div className="text-center py-8 text-muted-foreground">
                    다가오는 일정이 없습니다.
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Today's Schedules List */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <CheckCircle className="size-5" />
                  오늘의 일정
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {todaySchedules.length > 0 ? (
                  todaySchedules.map((schedule) => (
                    <Link key={schedule.id} to={`/schedules/${schedule.id}`}>
                      <div className="p-4 rounded-lg border hover:bg-accent transition-colors cursor-pointer">
                        <div className="flex justify-between items-start mb-2">
                          <div>
                            <p className="text-sm text-muted-foreground">{schedule.studyName}</p>
                            <p>{schedule.name}</p>
                          </div>
                          <span className="text-sm text-primary">{schedule.time}</span>
                        </div>
                        <p className="text-sm text-muted-foreground">{schedule.location}</p>
                      </div>
                    </Link>
                  ))
                ) : (
                  <div className="text-center py-8 text-muted-foreground">오늘 예정된 일정이 없습니다.</div>
                )}
              </CardContent>
            </Card>
          </div>
        </>
      )}
    </div>
  );
}

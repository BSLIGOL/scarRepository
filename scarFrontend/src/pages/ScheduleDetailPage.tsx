import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '../components/ui/alert-dialog';
import { Calendar, MapPin, Users, ArrowLeft, Check, X, Edit, Trash2 } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'sonner';
import { scheduleService } from '../api/services/scheduleService';
import type { ScheduleDetail } from '../types/models';
import { AttendanceStatus } from '../types/models';

export default function ScheduleDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const [schedule, setSchedule] = useState<ScheduleDetail | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchSchedule = async () => {
      if (!id) return;
      try {
        const data = await scheduleService.getScheduleDetail(Number(id));
        setSchedule(data);
      } catch (error) {
        console.error('Failed to fetch schedule', error);
        toast.error('일정을 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchSchedule();
  }, [id]);

  if (loading) {
    return <div className="text-center mt-20">로딩 중...</div>;
  }

  if (!schedule) {
    return (
      <div className="max-w-4xl mx-auto text-center py-20">
        <h1 className="text-2xl mb-4">일정을 찾을 수 없습니다</h1>
        <Button onClick={() => navigate('/studies')}>목록으로 돌아가기</Button>
      </div>
    );
  }

  const userMember = schedule.members.find((m) => m.nickName === user?.nickName);
  console.log('Current User:', user);
  console.log('Schedule Members:', schedule.members);
  console.log('Matched Member:', userMember);

  const attendingCount = schedule.members.filter((m) => m.status === AttendanceStatus.ATTENDED).length;
  const notAttendingCount = schedule.members.filter((m) => m.status === AttendanceStatus.ABSENT).length;

  const handleAttendance = async (status: AttendanceStatus) => {
    if (!isAuthenticated || !user) {
      toast.error('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    try {
      await scheduleService.attendSchedule(schedule.id, user.id, status);

      // Optimistic update or refetch
      setSchedule(prev => {
        if (!prev) return null;
        return {
          ...prev,
          members: prev.members.map(m =>
            m.nickName === user.nickName ? { ...m, status } : m
          )
        };
      });

      toast.success(status === AttendanceStatus.ATTENDED ? '참여로 응답했습니다.' : '불참으로 응답했습니다.');
    } catch (error) {
      toast.error('출석 체크에 실패했습니다.');
    }
  };

  // ⭐ 새로 추가: 일정 삭제 핸들러 (리더만 사용)
  const handleDeleteSchedule = async () => {
    try {
      await scheduleService.deleteSchedule(schedule.id);
      toast.success('일정이 삭제되었습니다.');
      navigate(`/studies/${schedule.studyId}`);
    } catch (error) {
      toast.error('일정 삭제에 실패했습니다.');
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <Button variant="ghost" onClick={() => navigate(`/studies/${schedule.studyId}`)} className="mb-6">
        <ArrowLeft className="size-4 mr-2" />
        스터디로 돌아가기
      </Button>

      <Card className="mb-6">
        <CardHeader>
          <div className="flex items-start justify-between gap-4 mb-2">
            <div className="flex-1">
              {/* Study Name is not in ScheduleDetailDto, might need to fetch study or just omit */}
              <Badge variant="secondary" className="mb-2">스터디 일정</Badge>
              <CardTitle className="text-3xl">{schedule.title}</CardTitle>
            </div>
            {/* ⭐ 새로 추가: 리더에게만 보이는 수정/삭제 버튼 */}
            {/* Note: ScheduleDetailDto does not have isLeader or studyLeaderId. 
                We might need to check if current user is the creator of the schedule or fetch study info.
                For now, we will assume `creatorNickName` exists in ScheduleDetail (it does in models.ts) and compare.
             */}
            {schedule.isLeader && (
              <div className="flex gap-2">
                {/* 수정 버튼 */}
                <Button variant="outline" size="sm" onClick={() => navigate(`/schedules/${id}/edit`)}>
                  <Edit className="size-4 mr-2" />
                  수정
                </Button>
                {/* 삭제 버튼 (확인 다이얼로그 포함) */}
                <AlertDialog>
                  <AlertDialogTrigger asChild>
                    <button className="inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50 border border-input bg-background shadow-sm hover:bg-accent hover:text-accent-foreground h-8 px-3 text-destructive hover:text-destructive">
                      <Trash2 className="size-4 mr-2" />
                      삭제
                    </button>
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>일정을 삭제하시겠습니까?</AlertDialogTitle>
                      <AlertDialogDescription>
                        이 작업은 되돌릴 수 없습니다. {attendingCount > 0 && `${attendingCount}명이 참석 예정인 일정입니다.`}
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel>취소</AlertDialogCancel>
                      <AlertDialogAction
                        onClick={handleDeleteSchedule}
                        className="bg-destructive hover:bg-destructive/90"
                      >
                        삭제
                      </AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              </div>
            )}
          </div>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground mb-6">{schedule.content}</p>

          <div className="grid md:grid-cols-2 gap-4 mb-6">
            <div className="flex items-center gap-3 p-4 rounded-lg bg-accent">
              <Calendar className="size-5 text-primary" />
              <div>
                <p className="text-sm text-muted-foreground">일시</p>
                <p>
                  {new Date(schedule.startTime).toLocaleDateString()} {new Date(schedule.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  {schedule.endTime && ` ~ ${new Date(schedule.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`}
                </p>
              </div>
            </div>
            <div className="flex items-center gap-3 p-4 rounded-lg bg-accent">
              <MapPin className="size-5 text-primary" />
              <div>
                <p className="text-sm text-muted-foreground">장소</p>
                <p>{schedule.location}</p>
              </div>
            </div>
          </div>

          <div className="border-t pt-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg flex items-center gap-2">
                <Users className="size-5" />
                참불참 현황
              </h3>
              <div className="flex gap-4 text-sm">
                <span className="text-green-600">참여: {attendingCount}명</span>
                <span className="text-red-600">불참: {notAttendingCount}명</span>
              </div>
            </div>

            <div className="space-y-2">
              {schedule.members.map((member, index) => (
                <div
                  key={index}
                  className="flex items-center justify-between p-3 rounded-lg border"
                >
                  <div className="flex items-center gap-3">
                    <span>{member.nickName}</span>
                    {member.nickName === user?.nickName && (
                      <Badge variant="outline">나</Badge>
                    )}
                  </div>
                  <div className="flex items-center gap-2">
                    {member.status === AttendanceStatus.ATTENDED && (
                      <Badge className="bg-green-600">
                        <Check className="size-3 mr-1" />
                        참여
                      </Badge>
                    )}
                    {member.status === AttendanceStatus.ABSENT && (
                      <Badge variant="destructive">
                        <X className="size-3 mr-1" />
                        불참
                      </Badge>
                    )}
                    {!member.status && (
                      <Badge variant="secondary">미응답</Badge>
                    )}
                    {member.nickName === user?.nickName && (
                      <div className="flex gap-2 ml-2">
                        <Button
                          size="sm"
                          variant={userMember?.status === AttendanceStatus.ATTENDED ? 'default' : 'outline'}
                          onClick={() => handleAttendance(AttendanceStatus.ATTENDED)}
                        >
                          <Check className="size-4" />
                        </Button>
                        <Button
                          size="sm"
                          variant={userMember?.status === AttendanceStatus.ABSENT ? 'destructive' : 'outline'}
                          onClick={() => handleAttendance(AttendanceStatus.ABSENT)}
                        >
                          <X className="size-4" />
                        </Button>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

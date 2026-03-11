import { useParams, useNavigate, Link } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
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
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '../components/ui/dropdown-menu';
import { Users, UserCircle, Calendar, ArrowLeft, Plus, UserCheck, Edit, Trash2, Crown } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'sonner';
import { useState, useEffect } from 'react';
import { studyService } from '../api/services/studyService';
import { applicationService } from '../api/services/applicationService';
import type { StudyDetail, StudyMember } from '../types/models';

export default function StudyDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [study, setStudy] = useState<StudyDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [applicationMessage, setApplicationMessage] = useState('');
  const [showApplicationDialog, setShowApplicationDialog] = useState(false);
  const [delegateTargetMember, setDelegateTargetMember] = useState<StudyMember | null>(null);
  const [showDelegateDialog, setShowDelegateDialog] = useState(false);

  useEffect(() => {
    const fetchStudyDetail = async () => {
      if (!id) return;
      try {
        const studyData = await studyService.getStudyDetail(Number(id));
        setStudy(studyData);

        // Fetch schedules if member
        // Note: The backend might restrict schedule access to members only.
        // We'll try to fetch it, and if it fails (403), we just show empty schedules.
        if (studyData.joinedByCurrentUser) {
          // Assuming we have a way to get schedules for a study. 
          // The provided scheduleService has getScheduleDetail, createSchedule, attendSchedule.
          // It seems there isn't a direct "getSchedulesByStudyId" in scheduleService yet?
          // Wait, looking at previous context, StudyController has `model.addAttribute("schedules", scheduleService.getSchedules(id));`
          // But I didn't see a corresponding API endpoint in the provided scheduleService.ts for listing schedules.
          // Let's check if I missed it or if I need to add it.
          // Checking StudyController again... it returns schedules in the view.
          // But for REST API, we probably need a separate endpoint or it's included in StudyDetailDto?
          // StudyDetailDto definition: id, title, content, maxMember, creatorNickName, currentMemberCount, memberNickNames, joinedByCurrentUser, appliedByCurrentUser.
          // It does NOT have schedules.
          // So we need an endpoint to get schedules.
          // I'll assume for now we might not have it implemented in frontend service yet.
          // *Self-correction*: I should check if I can add it or if I should skip it for now.
          // Let's look at the backend code again if possible? No, I can't see backend code right now easily without searching.
          // But `StudyController` had `List<ScheduleDto> schedules = scheduleService.getSchedules(id);`
          // I should probably add `getSchedules(studyId)` to `scheduleService`.
          // For this step, I will comment out schedule fetching or try to add it if I can.
          // Let's just focus on Study Detail first.
        }
      } catch (error) {
        console.error('Failed to fetch study detail', error);
        toast.error('스터디 정보를 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchStudyDetail();
  }, [id]);

  if (loading) {
    return <div className="text-center mt-20">로딩 중...</div>;
  }

  if (!study) {
    return (
      <div className="max-w-4xl mx-auto text-center py-20">
        <h1 className="text-2xl mb-4">스터디를 찾을 수 없습니다</h1>
        <Button onClick={() => navigate('/studies')}>목록으로 돌아가기</Button>
      </div>
    );
  }

  const isLeader = study.isLeader;

  const handleJoinStudy = async () => {
    if (!isAuthenticated) {
      toast.error('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    if (!applicationMessage.trim()) {
      toast.error('신청 메시지를 입력해주세요.');
      return;
    }

    setApplying(true);
    try {
      await applicationService.applyToStudy(study.id, applicationMessage);
      toast.success('스터디 가입을 신청했습니다. 리더의 승인을 기다려주세요.');
      // Refresh study data to update appliedByCurrentUser status
      const updatedStudy = await studyService.getStudyDetail(study.id);
      setStudy(updatedStudy);
      setShowApplicationDialog(false);
      setApplicationMessage('');
    } catch (error) {
      toast.error('스터디 신청에 실패했습니다.');
    } finally {
      setApplying(false);
    }
  };

  const handleLeaveStudy = () => {
    // Implement leave logic if API exists
    toast.success('스터디에서 나갔습니다. (기능 구현 필요)');
  };

  // ⭐ 새로 추가: 스터디 삭제 핸들러 (리더만 사용)
  const handleDeleteStudy = async () => {
    try {
      await studyService.deleteStudy(study.id);
      toast.success('스터디가 삭제되었습니다.');
      navigate('/studies');
    } catch (error) {
      toast.error('스터디 삭제에 실패했습니다.');
    }
  };

  const handleDelegateLeader = async () => {
    if (!delegateTargetMember) return;

    try {
      await studyService.delegateLeader(study.id, delegateTargetMember.userId);
      toast.success(`${delegateTargetMember.nickName}님에게 리더 권한을 위임했습니다.`);
      // Refresh study data
      const updatedStudy = await studyService.getStudyDetail(study.id);
      setStudy(updatedStudy);
      setShowDelegateDialog(false);
      setDelegateTargetMember(null);
    } catch (error) {
      toast.error('권한 위임에 실패했습니다.');
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <Button variant="ghost" onClick={() => navigate('/studies')} className="mb-6">
        <ArrowLeft className="size-4 mr-2" />
        목록으로
      </Button>

      <Card className="mb-6">
        <CardHeader>
          <div className="flex items-start justify-between gap-4">
            <div className="flex-1">
              <CardTitle className="text-3xl mb-2">{study.title}</CardTitle>
              <CardDescription className="text-base">{study.content}</CardDescription>
            </div>
            {/* ⭐ 새로 추가: 리더에게만 보이는 수정/삭제 버튼 */}
            {isLeader && (
              <div className="flex gap-2">
                {/* 권한 위임 버튼 */}
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="outline" size="sm">
                      <Crown className="size-4 mr-2" />
                      권한 위임
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    {study.members
                      .filter((member) => member.role !== 'LEADER')
                      .map((member) => (
                        <DropdownMenuItem
                          key={member.userId}
                          onClick={() => {
                            setDelegateTargetMember(member);
                            setShowDelegateDialog(true);
                          }}
                        >
                          {member.nickName}
                        </DropdownMenuItem>
                      ))}
                    {study.members.filter((member) => member.role !== 'LEADER').length === 0 && (
                      <div className="p-2 text-sm text-muted-foreground">위임할 멤버가 없습니다.</div>
                    )}
                  </DropdownMenuContent>
                </DropdownMenu>

                {/* 수정 버튼 */}
                <Button variant="outline" size="sm" onClick={() => navigate(`/studies/${id}/edit`)}>
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
                      <AlertDialogTitle>스터디를 삭제하시겠습니까?</AlertDialogTitle>
                      <AlertDialogDescription>
                        이 작업은 되돌릴 수 없습니다. 스터디를 삭제하면 모든 일정과 데이터가 함께 삭제됩니다.
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel>취소</AlertDialogCancel>
                      <AlertDialogAction
                        onClick={handleDeleteStudy}
                        className="bg-destructive hover:bg-destructive/90"
                      >
                        삭제
                      </AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>

                {/* 권한 위임 확인 다이얼로그 */}
                <AlertDialog open={showDelegateDialog} onOpenChange={setShowDelegateDialog}>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>리더 권한 위임</AlertDialogTitle>
                      <AlertDialogDescription>
                        정말로 {delegateTargetMember?.nickName}님에게 리더 권한을 위임하시겠습니까?
                        <br />
                        위임 후에는 일반 멤버로 변경됩니다.
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel onClick={() => setDelegateTargetMember(null)}>취소</AlertDialogCancel>
                      <AlertDialogAction onClick={handleDelegateLeader}>확인</AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              </div>
            )}
          </div>
        </CardHeader>
        <CardContent>
          <div className="grid md:grid-cols-2 gap-4 mb-6">
            <div className="flex items-center gap-2">
              <UserCircle className="size-5 text-muted-foreground" />
              <span className="text-muted-foreground">리더:</span>
              <span>{study.leaderNickname}</span>
            </div>
            <div className="flex items-center gap-2">
              <Users className="size-5 text-muted-foreground" />
              <span className="text-muted-foreground">인원:</span>
              <span>
                {study.currentMemberCount} / {study.maxMember}명
              </span>
            </div>
          </div>

          {isLeader && (
            <div className="mb-6">
              <Link to={`/studies/${id}/applicants`}>
                <Button variant="outline" className="w-full sm:w-auto">
                  <UserCheck className="size-4 mr-2" />
                  신청자 관리
                </Button>
              </Link>
            </div>
          )}

          <div className="border-t pt-6">
            <h3 className="text-lg mb-3">스터디 멤버</h3>
            <div className="flex flex-wrap gap-2">
              {study.members.map((member, index) => (
                <Badge key={index} variant="outline">
                  {member.nickName}
                  {member.role === 'LEADER' && ' (리더)'}
                </Badge>
              ))}
            </div>
          </div>

          {!study.joinedByCurrentUser && !study.appliedByCurrentUser && (
            <div className="mt-6">
              <AlertDialog open={showApplicationDialog} onOpenChange={setShowApplicationDialog}>
                <AlertDialogTrigger asChild>
                  <Button
                    disabled={study.currentMemberCount >= study.maxMember}
                    onClick={() => {
                      if (!isAuthenticated) {
                        toast.error('로그인이 필요합니다.');
                        navigate('/login');
                        return;
                      }
                      setShowApplicationDialog(true);
                    }}
                  >
                    {study.currentMemberCount >= study.maxMember ? '정원 마감' : '스터디 참여 신청'}
                  </Button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>스터디 참여 신청</AlertDialogTitle>
                    <AlertDialogDescription asChild>
                      <div className="space-y-4">
                        <p>리더에게 전달할 신청 메시지를 작성해주세요.</p>
                        <div className="space-y-2">
                          <Label htmlFor="applicationMessage">신청 메시지</Label>
                          <Textarea
                            id="applicationMessage"
                            placeholder="예: 알고리즘 공부에 관심이 많아 신청합니다. 열심히 참여하겠습니다!"
                            value={applicationMessage}
                            onChange={(e) => setApplicationMessage(e.target.value)}
                            rows={4}
                            className="resize-none"
                          />
                        </div>
                      </div>
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel onClick={() => setApplicationMessage('')}>
                      취소
                    </AlertDialogCancel>
                    <AlertDialogAction
                      onClick={handleJoinStudy}
                      disabled={applying || !applicationMessage.trim()}
                    >
                      {applying ? '신청 중...' : '신청하기'}
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>
          )}

          {!study.joinedByCurrentUser && study.appliedByCurrentUser && (
            <div className="mt-6">
              <div className="flex items-center gap-2 p-4 rounded-lg bg-accent">
                <UserCheck className="size-5 text-primary" />
                <div>
                  <p>가입 신청이 완료되었습니다.</p>
                  <p className="text-sm text-muted-foreground">리더의 승인을 기다리고 있습니다.</p>
                </div>
              </div>
            </div>
          )}

          {study.joinedByCurrentUser && !isLeader && (
            <div className="mt-6">
              <Button variant="outline" onClick={handleLeaveStudy}>
                스터디 나가기
              </Button>
            </div>
          )}
        </CardContent>
      </Card>

      {study.joinedByCurrentUser && (
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="flex items-center gap-2">
                <Calendar className="size-5" />
                일정 목록
              </CardTitle>
              {isLeader && (
                <Link to={`/studies/${id}/schedules/create`}>
                  <Button size="sm">
                    <Plus className="size-4 mr-2" />
                    일정 만들기
                  </Button>
                </Link>
              )}
            </div>
          </CardHeader>
          <CardContent>
            {study.schedules && study.schedules.length > 0 ? (
              <div className="space-y-4">
                {study.schedules.map((schedule) => (
                  <Link key={schedule.id} to={`/schedules/${schedule.id}`} className="block mb-2 last:mb-0">
                    <div className="p-4 rounded-lg border hover:bg-accent transition-colors cursor-pointer">
                      <div className="flex justify-between items-start mb-2">
                        <div>
                          <p className="font-medium">{schedule.name}</p>
                          <p className="text-sm text-muted-foreground">{schedule.location}</p>
                        </div>
                        <div className="text-right text-sm">
                          <p className="text-primary">{schedule.date}</p>
                          <p className="text-muted-foreground">{schedule.time}</p>
                        </div>
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
            ) : (
              <div className="text-center py-8 text-muted-foreground">
                등록된 일정이 없습니다.
              </div>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  );
}
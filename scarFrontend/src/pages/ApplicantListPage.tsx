import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { ArrowLeft, Check, X, Mail, Clock, Users } from 'lucide-react';
// import { useAuth } from '../contexts/AuthContext';
import { toast } from 'sonner';
import { applicationService } from '../api/services/applicationService';
import { studyService } from '../api/services/studyService';
import type { StudyApplication, StudyDetail } from '../types/models';
import { ApplicationStatus } from '../types/models';

export default function ApplicantListPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  // const { user, isAuthenticated } = useAuth(); // Not used anymore
  const [applications, setApplications] = useState<StudyApplication[]>([]);
  const [study, setStudy] = useState<StudyDetail | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      try {
        const [studyData, appsData] = await Promise.all([
          studyService.getStudyDetail(Number(id)),
          applicationService.getApplications(Number(id))
        ]);
        setStudy(studyData);
        setApplications(appsData);
      } catch (error) {
        console.error('Failed to fetch data', error);
        toast.error('데이터를 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
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

  // Check if current user is the creator (leader)
  const isLeader = study.isLeader;

  if (!isLeader) {
    return (
      <div className="max-w-4xl mx-auto text-center py-20">
        <h1 className="text-2xl mb-4">접근 권한이 없습니다</h1>
        <p className="text-muted-foreground mb-4">스터디 리더만 신청자 목록을 볼 수 있습니다.</p>
        <Button onClick={() => navigate(`/studies/${id}`)}>스터디로 돌아가기</Button>
      </div>
    );
  }

  const handleApprove = async (applicationId: number, nickName: string) => {
    try {
      await applicationService.approveApplication(applicationId);
      setApplications(apps => apps.map(app =>
        app.id === applicationId ? { ...app, status: ApplicationStatus.APPROVED } : app
      ));
      toast.success(`${nickName}님의 가입을 승인했습니다.`);
    } catch (error) {
      toast.error('승인 처리에 실패했습니다.');
    }
  };

  const handleReject = async (applicationId: number, nickName: string) => {
    try {
      await applicationService.rejectApplication(applicationId);
      setApplications(apps => apps.map(app =>
        app.id === applicationId ? { ...app, status: ApplicationStatus.REJECTED } : app
      ));
      toast.success(`${nickName}님의 가입을 거절했습니다.`);
    } catch (error) {
      toast.error('거절 처리에 실패했습니다.');
    }
  };

  const pendingApplicants = applications.filter(app => app.status === ApplicationStatus.PENDING);
  const processedApplicants = applications.filter(app => app.status !== ApplicationStatus.PENDING);

  return (
    <div className="max-w-4xl mx-auto">
      <Button variant="ghost" onClick={() => navigate(`/studies/${id}`)} className="mb-6">
        <ArrowLeft className="size-4 mr-2" />
        스터디로 돌아가기
      </Button>

      <div className="mb-6">
        <h1 className="text-3xl mb-2">신청자 목록</h1>
        <p className="text-muted-foreground">{study.title}</p>
      </div>

      {/* 대기중인 신청자 */}
      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Clock className="size-5" />
            승인 대기중
            {pendingApplicants.length > 0 && (
              <Badge variant="default">{pendingApplicants.length}</Badge>
            )}
          </CardTitle>
          <CardDescription>가입 신청을 검토하고 승인 또는 거절하세요</CardDescription>
        </CardHeader>
        <CardContent>
          {pendingApplicants.length > 0 ? (
            <div className="space-y-3">
              {pendingApplicants.map((applicant) => (
                <div
                  key={applicant.id}
                  className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-4 rounded-lg border"
                >
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <Users className="size-4 text-muted-foreground" />
                      <span>{applicant.nickName}</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground mb-1">
                      <Mail className="size-4" />
                      <span>{applicant.message}</span>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <Button
                      size="sm"
                      onClick={() => handleApprove(applicant.id, applicant.nickName)}
                      className="bg-green-600 hover:bg-green-700"
                    >
                      <Check className="size-4 mr-2" />
                      승인
                    </Button>
                    <Button
                      size="sm"
                      variant="destructive"
                      onClick={() => handleReject(applicant.id, applicant.nickName)}
                    >
                      <X className="size-4 mr-2" />
                      거절
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-muted-foreground">
              대기중인 신청자가 없습니다.
            </div>
          )}
        </CardContent>
      </Card>

      {/* 처리된 신청자 */}
      {processedApplicants.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>처리 완료</CardTitle>
            <CardDescription>승인 또는 거절된 신청자 목록입니다</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {processedApplicants.map((applicant) => (
                <div
                  key={applicant.id}
                  className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-4 rounded-lg border"
                >
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <Users className="size-4 text-muted-foreground" />
                      <span>{applicant.nickName}</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Mail className="size-4" />
                      <span>{applicant.message}</span>
                    </div>
                  </div>
                  <div>
                    {applicant.status === ApplicationStatus.APPROVED && (
                      <Badge className="bg-green-600">
                        <Check className="size-3 mr-1" />
                        승인됨
                      </Badge>
                    )}
                    {applicant.status === ApplicationStatus.REJECTED && (
                      <Badge variant="destructive">
                        <X className="size-3 mr-1" />
                        거절됨
                      </Badge>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}

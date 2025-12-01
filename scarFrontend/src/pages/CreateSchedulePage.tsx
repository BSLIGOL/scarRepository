import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { toast } from 'sonner';
import { ArrowLeft } from 'lucide-react';
import { scheduleService } from '../api/services/scheduleService';
import { toKSTISOString } from '../utils/dateUtils';

export default function CreateSchedulePage() {
  const { studyId } = useParams();
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [location, setLocation] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!title || !content || !startTime || !endTime || !location) {
      toast.error('모든 필드를 입력해주세요.');
      return;
    }

    // Validate that endTime is after startTime
    if (new Date(endTime) <= new Date(startTime)) {
      toast.error('종료 시간은 시작 시간보다 늦어야 합니다.');
      return;
    }

    if (!studyId) {
      toast.error('잘못된 접근입니다.');
      return;
    }

    setLoading(true);
    try {
      // Convert datetime-local to ISO string with Korea timezone (UTC+9)
      const formattedStartTime = toKSTISOString(startTime);
      const formattedEndTime = toKSTISOString(endTime);

      await scheduleService.createSchedule({
        studyId: Number(studyId),
        title,
        content,
        startTime: formattedStartTime,
        endTime: formattedEndTime,
        location,
      });
      toast.success('일정이 생성되었습니다!');
      navigate(`/studies/${studyId}`);
    } catch (error) {
      toast.error('일정 생성에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <Button variant="ghost" onClick={() => navigate(`/studies/${studyId}`)} className="mb-6">
        <ArrowLeft className="size-4 mr-2" />
        스터디로 돌아가기
      </Button>

      <Card>
        <CardHeader>
          <CardTitle>새 일정 만들기</CardTitle>
          <CardDescription>스터디 일정 정보를 입력하세요</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="title">일정 이름</Label>
              <Input
                id="title"
                placeholder="예: 주간 모임"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="content">일정 내용</Label>
              <Textarea
                id="content"
                placeholder="일정에 대한 설명을 입력하세요..."
                value={content}
                onChange={(e) => setContent(e.target.value)}
                rows={4}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="startTime">시작 시간</Label>
              <Input
                id="startTime"
                type="datetime-local"
                value={startTime}
                onChange={(e) => setStartTime(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="endTime">종료 시간</Label>
              <Input
                id="endTime"
                type="datetime-local"
                value={endTime}
                onChange={(e) => setEndTime(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="location">장소</Label>
              <Input
                id="location"
                placeholder="예: 스터디룸 A, 온라인"
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                required
              />
            </div>

            <div className="flex gap-3">
              <Button type="submit" disabled={loading}>
                {loading ? '생성 중...' : '일정 만들기'}
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate(`/studies/${studyId}`)}
              >
                취소
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

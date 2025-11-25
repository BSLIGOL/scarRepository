import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { toast } from 'sonner';
import { ArrowLeft } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

import { scheduleService } from '../api/services/scheduleService';
import type { ScheduleDetail } from '../types/models';
import { toKSTISOString, fromISOStringToLocal } from '../utils/dateUtils';

export default function EditSchedulePage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(false);

  const [schedule, setSchedule] = useState<ScheduleDetail | null>(null);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [location, setLocation] = useState('');

  useEffect(() => {
    const fetchSchedule = async () => {
      if (!id) return;
      try {
        const data = await scheduleService.getScheduleDetail(Number(id));
        setSchedule(data);
        setName(data.title);
        setDescription(data.content);
        // Convert ISO string to datetime-local format (YYYY-MM-DDTHH:mm)
        setStartTime(fromISOStringToLocal(data.startTime));
        setEndTime(fromISOStringToLocal(data.endTime));
        setLocation(data.location);
      } catch (error) {
        console.error('Failed to fetch schedule', error);
        toast.error('일정을 불러오는데 실패했습니다.');
      }
    };

    fetchSchedule();
  }, [id]);

  if (!schedule) {
    return (
      <div className="max-w-4xl mx-auto text-center py-20">
        <h1 className="text-2xl mb-4">일정을 찾을 수 없습니다</h1>
        <Button onClick={() => navigate('/studies')}>목록으로 돌아가기</Button>
      </div>
    );
  }

  // Note: ScheduleDetail might not have studyLeaderId directly. 
  // We need to check if the current user is the creator of the schedule (or study leader if available).
  // Assuming creatorNickName is available and we compare with user.nickName
  const isLeader = isAuthenticated && schedule.creatorNickName === user?.nickName;

  if (!isLeader) {
    return (
      <div className="max-w-4xl mx-auto text-center py-20">
        <h1 className="text-2xl mb-4">접근 권한이 없습니다</h1>
        <p className="text-muted-foreground mb-4">스터디 리더만 수정할 수 있습니다.</p>
        <Button onClick={() => navigate(`/schedules/${id}`)}>일정으로 돌아가기</Button>
      </div>
    );
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!name || !description || !startTime || !endTime || !location) {
      toast.error('모든 필드를 입력해주세요.');
      return;
    }

    // Validate that endTime is after startTime
    if (new Date(endTime) <= new Date(startTime)) {
      toast.error('종료 시간은 시작 시간보다 늦어야 합니다.');
      return;
    }

    setLoading(true);
    try {
      // Convert datetime-local to ISO string with Korea timezone (UTC+9)
      const formattedStartTime = toKSTISOString(startTime);
      const formattedEndTime = toKSTISOString(endTime);

      await scheduleService.updateSchedule(Number(id), {
        studyId: schedule?.studyId || 0,
        title: name,
        content: description,
        startTime: formattedStartTime,
        endTime: formattedEndTime,
        location,
      });
      toast.success('일정이 수정되었습니다!');
      navigate(`/schedules/${id}`);
    } catch (error) {
      toast.error('일정 수정에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <Button variant="ghost" onClick={() => navigate(`/schedules/${id}`)} className="mb-6">
        <ArrowLeft className="size-4 mr-2" />
        일정으로 돌아가기
      </Button>

      <Card>
        <CardHeader>
          <CardTitle>일정 수정</CardTitle>
          <CardDescription>일정 정보를 수정하세요</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="name">일정 이름</Label>
              <Input
                id="name"
                placeholder="예: 주간 모임"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">일정 내용</Label>
              <Textarea
                id="description"
                placeholder="일정에 대한 설명을 입력하세요..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
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
                {loading ? '수정 중...' : '수정 완료'}
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => navigate(`/schedules/${id}`)}
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

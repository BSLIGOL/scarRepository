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

import { studyService } from '../api/services/studyService';
import type { StudyDetail } from '../types/models';

export default function EditStudyPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(false);

  const [study, setStudy] = useState<StudyDetail | null>(null);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [maxMembers, setMaxMembers] = useState('');

  useEffect(() => {
    const fetchStudy = async () => {
      if (!id) return;
      try {
        const data = await studyService.getStudyDetail(Number(id));
        setStudy(data);
        setName(data.title);
        setDescription(data.content);
        setMaxMembers(data.maxMember.toString());
      } catch (error) {
        console.error('Failed to fetch study', error);
        toast.error('스터디 정보를 불러오는데 실패했습니다.');
      }
    };

    fetchStudy();
  }, [id]);

  if (!study) {
    return (
      <div className="max-w-4xl mx-auto text-center py-20">
        <h1 className="text-2xl mb-4">스터디를 찾을 수 없습니다</h1>
        <Button onClick={() => navigate('/studies')}>목록으로 돌아가기</Button>
      </div>
    );
  }

  const isLeader = isAuthenticated && study.isLeader;

  if (!isLeader) {
    return (
      <div className="max-w-4xl mx-auto text-center py-20">
        <h1 className="text-2xl mb-4">접근 권한이 없습니다</h1>
        <p className="text-muted-foreground mb-4">스터디 리더만 수정할 수 있습니다.</p>
        <Button onClick={() => navigate(`/studies/${id}`)}>스터디로 돌아가기</Button>
      </div>
    );
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!name || !description) {
      toast.error('모든 필드를 입력해주세요.');
      return;
    }

    if (parseInt(maxMembers) < 2) {
      toast.error('최대 인원은 2명 이상이어야 합니다.');
      return;
    }

    setLoading(true);
    try {
      await studyService.updateStudy(Number(id), {
        title: name,
        content: description,
        maxMember: parseInt(maxMembers),
      });
      toast.success('스터디가 수정되었습니다!');
      navigate(`/studies/${id}`);
    } catch (error) {
      toast.error('스터디 수정에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <Button variant="ghost" onClick={() => navigate(`/studies/${id}`)} className="mb-6">
        <ArrowLeft className="size-4 mr-2" />
        스터디로 돌아가기
      </Button>

      <Card>
        <CardHeader>
          <CardTitle>스터디 수정</CardTitle>
          <CardDescription>스터디 정보를 수정하세요</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="name">스터디 이름</Label>
              <Input
                id="name"
                placeholder="예: 알고리즘 스터디"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">스터디 내용</Label>
              <Textarea
                id="description"
                placeholder="스터디에 대한 설명을 입력하세요..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={6}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="maxMembers">최대 인원 수</Label>
              <Input
                id="maxMembers"
                type="number"
                min="2"
                max="100"
                value={maxMembers}
                onChange={(e) => setMaxMembers(e.target.value)}
                required
              />
            </div>

            <div className="flex gap-3">
              <Button type="submit" disabled={loading}>
                {loading ? '수정 중...' : '수정 완료'}
              </Button>
              <Button type="button" variant="outline" onClick={() => navigate(`/studies/${id}`)}>
                취소
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

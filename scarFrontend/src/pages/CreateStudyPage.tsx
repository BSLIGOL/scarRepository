import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { toast } from 'sonner';
import { ArrowLeft } from 'lucide-react';
import { studyService } from '../api/services/studyService';

export default function CreateStudyPage() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [maxMember, setMaxMember] = useState('10');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!title || !content) {
      toast.error('모든 필드를 입력해주세요.');
      return;
    }

    if (parseInt(maxMember) < 2) {
      toast.error('최대 인원은 2명 이상이어야 합니다.');
      return;
    }

    setLoading(true);
    try {
      await studyService.createStudy({
        title,
        content,
        maxMember: parseInt(maxMember),
      });
      toast.success('스터디가 생성되었습니다!');
      navigate('/studies');
    } catch (error) {
      toast.error('스터디 생성에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <Button variant="ghost" onClick={() => navigate('/studies')} className="mb-6">
        <ArrowLeft className="size-4 mr-2" />
        목록으로
      </Button>

      <Card>
        <CardHeader>
          <CardTitle>새 스터디 만들기</CardTitle>
          <CardDescription>스터디 정보를 입력하여 새로운 스터디를 만들어보세요</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="title">스터디 이름</Label>
              <Input
                id="title"
                placeholder="예: 알고리즘 스터디"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="content">스터디 내용</Label>
              <Textarea
                id="content"
                placeholder="스터디에 대한 설명을 입력하세요..."
                value={content}
                onChange={(e) => setContent(e.target.value)}
                rows={6}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="maxMember">최대 인원 수</Label>
              <Input
                id="maxMember"
                type="number"
                min="2"
                max="100"
                value={maxMember}
                onChange={(e) => setMaxMember(e.target.value)}
                required
              />
            </div>

            <div className="flex gap-3">
              <Button type="submit" disabled={loading}>
                {loading ? '생성 중...' : '스터디 만들기'}
              </Button>
              <Button type="button" variant="outline" onClick={() => navigate('/studies')}>
                취소
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

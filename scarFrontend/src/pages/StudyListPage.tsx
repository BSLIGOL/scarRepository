import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Users, UserCircle, Plus } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { studyService } from '../api/services/studyService';
import type { Study } from '../types/models';

export default function StudyListPage() {
  const { isAuthenticated } = useAuth();
  const [studies, setStudies] = useState<Study[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStudies = async () => {
      try {
        const data = await studyService.getAllStudies();
        setStudies(data);
      } catch (error) {
        console.error('Failed to fetch studies', error);
      } finally {
        setLoading(false);
      }
    };

    fetchStudies();
  }, []);

  if (loading) {
    return <div className="text-center mt-20">로딩 중...</div>;
  }

  return (
    <div className="max-w-6xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl mb-2">스터디 목록</h1>
          <p className="text-muted-foreground">관심있는 스터디를 찾아보세요</p>
        </div>
        {isAuthenticated && (
          <Link to="/studies/create">
            <Button>
              <Plus className="size-4 mr-2" />
              스터디 만들기
            </Button>
          </Link>
        )}
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {studies.map((study) => (
          <Link key={study.id} to={`/studies/${study.id}`}>
            <Card className="h-full hover:shadow-lg transition-shadow cursor-pointer">
              <CardHeader>
                <CardTitle>
                  {study.title}
                </CardTitle>
                <CardDescription className="line-clamp-2">{study.content}</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="flex items-center gap-2 text-sm">
                    <UserCircle className="size-4 text-muted-foreground" />
                    <span className="text-muted-foreground">리더:</span>
                    <span>{study.leaderNickname}</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm">
                    <Users className="size-4 text-muted-foreground" />
                    <span className="text-muted-foreground">인원:</span>
                    <span>
                      {study.currentMemberCount} / {study.maxMember}명
                    </span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}

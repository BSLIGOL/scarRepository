import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
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
import { toast } from 'sonner';
import { User, Lock, AlertTriangle } from 'lucide-react';
import { authService } from '../api/services/authService';

export default function ProfilePage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  // 정보 수정
  const [nickname, setNickname] = useState(user?.nickName || '');
  const [loading, setLoading] = useState(false);

  // 비밀번호 변경
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmNewPassword, setConfirmNewPassword] = useState('');
  const [passwordLoading, setPasswordLoading] = useState(false);

  // 회원 탈퇴
  const [deleteConfirmText, setDeleteConfirmText] = useState('');

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!nickname.trim()) {
      toast.error('닉네임을 입력해주세요.');
      return;
    }

    setLoading(true);
    try {
      await authService.updateProfile({ nickName: nickname });
      toast.success('회원 정보가 수정되었습니다.');
      // Refresh user data (re-login or fetch me) - for simplicity, we might need to update context
      // Assuming AuthContext has a way to refresh user or we just force reload
      window.location.reload();
    } catch (error) {
      toast.error('정보 수정에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!currentPassword || !newPassword || !confirmNewPassword) {
      toast.error('모든 필드를 입력해주세요.');
      return;
    }

    if (newPassword !== confirmNewPassword) {
      toast.error('새 비밀번호가 일치하지 않습니다.');
      return;
    }

    if (newPassword.length < 6) {
      toast.error('비밀번호는 최소 6자 이상이어야 합니다.');
      return;
    }

    setPasswordLoading(true);
    try {
      await authService.changePassword({ currentPassword, newPassword });
      toast.success('비밀번호가 변경되었습니다.');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmNewPassword('');
    } catch (error) {
      toast.error('비밀번호 변경에 실패했습니다.');
    } finally {
      setPasswordLoading(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (deleteConfirmText !== user?.nickName) {
      toast.error('닉네임이 일치하지 않습니다.');
      return;
    }

    try {
      await authService.deleteAccount();
      toast.success('회원 탈퇴가 완료되었습니다.');
      logout();
      navigate('/');
    } catch (error) {
      toast.error('회원 탈퇴에 실패했습니다.');
    }
  };

  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="text-3xl mb-8">내 정보</h1>

      <Tabs defaultValue="profile" className="space-y-6">
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="profile">
            <User className="size-4 mr-2" />
            회원 정보
          </TabsTrigger>
          <TabsTrigger value="password">
            <Lock className="size-4 mr-2" />
            비밀번호 변경
          </TabsTrigger>
          <TabsTrigger value="delete">
            <AlertTriangle className="size-4 mr-2" />
            회원 탈퇴
          </TabsTrigger>
        </TabsList>

        {/* 회원 정보 수정 */}
        <TabsContent value="profile">
          <Card>
            <CardHeader>
              <CardTitle>회원 정보 수정</CardTitle>
              <CardDescription>닉네임을 변경할 수 있습니다</CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleUpdateProfile} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="email">이메일</Label>
                  <Input id="email" type="email" value={user?.email} disabled />
                  <p className="text-sm text-muted-foreground">이메일은 변경할 수 없습니다</p>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="nickname">닉네임</Label>
                  <Input
                    id="nickname"
                    type="text"
                    value={nickname}
                    onChange={(e) => setNickname(e.target.value)}
                    required
                  />
                </div>

                <Button type="submit" disabled={loading}>
                  {loading ? '저장 중...' : '저장'}
                </Button>
              </form>
            </CardContent>
          </Card>
        </TabsContent>

        {/* 비밀번호 변경 */}
        <TabsContent value="password">
          <Card>
            <CardHeader>
              <CardTitle>비밀번호 변경</CardTitle>
              <CardDescription>현재 비밀번호를 입력하고 새 비밀번호를 설정하세요</CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleChangePassword} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="currentPassword">현재 비밀번호</Label>
                  <Input
                    id="currentPassword"
                    type="password"
                    value={currentPassword}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="newPassword">새 비밀번호</Label>
                  <Input
                    id="newPassword"
                    type="password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="confirmNewPassword">새 비밀번호 확인</Label>
                  <Input
                    id="confirmNewPassword"
                    type="password"
                    value={confirmNewPassword}
                    onChange={(e) => setConfirmNewPassword(e.target.value)}
                    required
                  />
                </div>

                <Button type="submit" disabled={passwordLoading}>
                  {passwordLoading ? '변경 중...' : '비밀번호 변경'}
                </Button>
              </form>
            </CardContent>
          </Card>
        </TabsContent>

        {/* 회원 탈퇴 */}
        <TabsContent value="delete">
          <Card className="border-destructive">
            <CardHeader>
              <CardTitle className="text-destructive">회원 탈퇴</CardTitle>
              <CardDescription>
                회원 탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="p-4 rounded-lg bg-destructive/10 border border-destructive/20">
                <h4 className="text-sm mb-2">탈퇴 시 주의사항:</h4>
                <ul className="text-sm space-y-1 list-disc list-inside text-muted-foreground">
                  <li>스터디 리더인 경우 리더를 위임해야 탈퇴할 수 있습니다</li>
                  <li>참여중인 모든 스터디에서 자동으로 탈퇴됩니다</li>
                  <li>작성한 모든 데이터가 삭제됩니다</li>
                  <li>탈퇴 후 7일간 재가입이 제한됩니다</li>
                </ul>
              </div>

              <AlertDialog>
                <AlertDialogTrigger asChild>
                  <button className="inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50 bg-destructive text-destructive-foreground shadow hover:bg-destructive/90 h-9 px-4 py-2">
                    회원 탈퇴
                  </button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>정말 탈퇴하시겠습니까?</AlertDialogTitle>
                    <AlertDialogDescription asChild>
                      <div className="space-y-4">
                        <p>이 작업은 되돌릴 수 없습니다.</p>
                        <div className="space-y-2">
                          <Label htmlFor="confirmDelete">
                            탈퇴를 진행하려면 닉네임 <strong>{user?.nickName}</strong>을 입력하세요
                          </Label>
                          <Input
                            id="confirmDelete"
                            value={deleteConfirmText}
                            onChange={(e) => setDeleteConfirmText(e.target.value)}
                            placeholder="닉네임 입력"
                          />
                        </div>
                      </div>
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel onClick={() => setDeleteConfirmText('')}>
                      취소
                    </AlertDialogCancel>
                    <AlertDialogAction
                      onClick={handleDeleteAccount}
                      className="bg-destructive hover:bg-destructive/90"
                      disabled={deleteConfirmText !== user?.nickName}
                    >
                      탈퇴하기
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
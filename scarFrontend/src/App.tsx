import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import { ThemeProvider } from "./contexts/ThemeContext";
import { Toaster } from "./components/ui/sonner";
import MainPage from "./pages/MainPage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import StudyListPage from "./pages/StudyListPage";
import StudyDetailPage from "./pages/StudyDetailPage";
import CreateStudyPage from "./pages/CreateStudyPage";
import CreateSchedulePage from "./pages/CreateSchedulePage";
import ScheduleDetailPage from "./pages/ScheduleDetailPage";
import ApplicantListPage from "./pages/ApplicantListPage";
import Layout from "./components/Layout";
import ProtectedRoute from "./components/ProtectedRoute";
import ProfilePage from "./pages/ProfilePage";
import EditSchedulePage from "./pages/EditSchedulePage";
import EditStudyPage from "./pages/EditStudyPage";

export default function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <Router>
          <Layout>
            <Routes>
              <Route path="/" element={<MainPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/signup" element={<SignupPage />} />
              <Route
                path="/studies"
                element={<StudyListPage />}
              />
              <Route
                path="/studies/:id"
                element={<StudyDetailPage />}
              />
              <Route
                path="/studies/:id/applicants"
                element={
                  <ProtectedRoute>
                    <ApplicantListPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/studies/create"
                element={
                  <ProtectedRoute>
                    <CreateStudyPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/studies/:id/edit"
                element={
                  <ProtectedRoute>
                    <EditStudyPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/studies/:studyId/schedules/create"
                element={
                  <ProtectedRoute>
                    <CreateSchedulePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/schedules/:id"
                element={<ScheduleDetailPage />}
              />
              <Route
                path="/schedules/:id/edit"
                element={
                  <ProtectedRoute>
                    <EditSchedulePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/profile"
                element={
                  <ProtectedRoute>
                    <ProfilePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="*"
                element={<Navigate to="/" replace />}
              />
            </Routes>
          </Layout>
        </Router>
        <Toaster />
      </AuthProvider>
    </ThemeProvider>
  );
}
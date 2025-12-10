import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';

import { authService } from '../api/services/authService';
import type { User } from '../types/models';

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string, nickName: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchUser = async () => {
    try {
      const userData = await authService.getMe();
      setUser(userData);
      localStorage.setItem('user', JSON.stringify(userData));
    } catch (error: any) {
      // 401 means simply "Not logged in", which is fine.
      // Also ignore our custom "CloudFront Error Page Interception" error
      const isCloudFrontError = error.message && error.message.includes('CloudFront Error Page Interception');

      if ((error.response && error.response.status === 401) || isCloudFrontError) {
        // just stay as guest (silent)
      } else {
        console.error('Failed to fetch user', error);
      }
      setUser(null);
      localStorage.removeItem('user');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUser();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      // 1. Call Login API
      await authService.login({ email, password });

      // 2. Fetch user profile
      await fetchUser();

    } catch (error) {
      console.error('Login failed', error);
      throw error;
    }
  };

  const signup = async (email: string, password: string, nickName: string) => {
    try {
      await authService.join({ email, password, nickName });
      // Auto login after signup or redirect to login page
    } catch (error) {
      console.error('Signup failed', error);
      throw error;
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout failed', error);
    } finally {
      setUser(null);
      localStorage.removeItem('user');
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        signup,
        logout,
        isAuthenticated: !!user,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

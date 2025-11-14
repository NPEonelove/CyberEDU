import { API_BASE_URL } from '@/config/api';

interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

interface SignInResponse {
  accessToken: string;
  refreshToken: string;
  user?: any;
}

interface RefreshTokenResponse {
  accessToken: string;
  refreshToken?: string;
}

class AuthService {
  private static instance: AuthService;
  private accessToken: string | null = null;
  private refreshToken: string | null = null;

  private constructor() {
    if (typeof window !== 'undefined') {
      this.accessToken = localStorage.getItem('accessToken');
      this.refreshToken = localStorage.getItem('refreshToken');
    }
  }

  static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  async signIn(userId: number): Promise<SignInResponse> {
    const apiUrl = `${API_BASE_URL}/api/v1`;
    const response = await fetch(`${apiUrl}/auth/sign-in`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userId }),
    });

    if (!response.ok) {
      throw new Error(`Sign-in failed: ${response.status}`);
    }

    const data: SignInResponse = await response.json();
    this.setTokens(data.accessToken, data.refreshToken);
    return data;
  }

  async signUp(userId: number): Promise<SignInResponse> {
    const apiUrl = `${API_BASE_URL}/api/v1`;
    const response = await fetch(`${apiUrl}/auth/sign-up`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userId }),
    });

    if (!response.ok) {
      throw new Error(`Sign-up failed: ${response.status}`);
    }

    const data: SignInResponse = await response.json();
    this.setTokens(data.accessToken, data.refreshToken);
    return data;
  }

  async refreshAccessToken(): Promise<string> {
    if (!this.refreshToken) {
      throw new Error('No refresh token available');
    }

    const apiUrl = `${API_BASE_URL}/api/v1`;
    const response = await fetch(`${apiUrl}/auth/refresh-access-token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        refreshToken: this.refreshToken
      }),
    });

    if (!response.ok) {
      this.clearTokens();
      throw new Error(`Token refresh failed: ${response.status}`);
    }

    const data: RefreshTokenResponse = await response.json();
    this.setTokens(data.accessToken, data.refreshToken || this.refreshToken);
    return data.accessToken;
  }

  private setTokens(accessToken: string, refreshToken: string): void {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    
    if (typeof window !== 'undefined') {
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
    }
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  getRefreshToken(): string | null {
    return this.refreshToken;
  }

  clearTokens(): void {
    this.accessToken = null;
    this.refreshToken = null;
    
    if (typeof window !== 'undefined') {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  }

  isAuthenticated(): boolean {
    return !!this.accessToken;
  }
}

export const authService = AuthService.getInstance();
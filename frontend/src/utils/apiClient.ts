import { authService } from '@/services/auth';
import { API_BASE_URL } from '@/config/api';

interface ApiRequestOptions extends RequestInit {
  headers?: Record<string, string>;
  skipAuth?: boolean;
}

class ApiClient {
  private baseUrl: string = `${API_BASE_URL}/api/v1`;
  private isRefreshing: boolean = false;
  private failedQueue: Array<{
    resolve: (value?: unknown) => void;
    reject: (reason?: any) => void;
  }> = [];

  private processQueue(error: any, token: string | null = null) {
    this.failedQueue.forEach(({ resolve, reject }) => {
      if (error) {
        reject(error);
      } else {
        resolve(token);
      }
    });
    
    this.failedQueue = [];
  }

  async request<T = any>(endpoint: string, options: ApiRequestOptions = {}): Promise<T> {
    const { skipAuth = false, headers = {}, ...restOptions } = options;
    
    if (!skipAuth && authService.isAuthenticated()) {
      const accessToken = authService.getAccessToken();
      if (accessToken) {
        headers['Authorization'] = `Bearer ${accessToken}`;
      }
    }

    const url = `${this.baseUrl}${endpoint}`;
    const config: RequestInit = {
      ...restOptions,
      headers: {
        'Content-Type': 'application/json',
        ...headers,
      },
    };

    try {
      const response = await fetch(url, config);

      if (response.status === 401 && !endpoint.includes('/auth/refresh-access-token') && !skipAuth) {
        return this.handleTokenRefresh(endpoint, options);
      }

      if (!response.ok) {
        throw new Error(`API Error: ${response.status} ${response.statusText}`);
      }

      return await response.json();
    } catch (error) {
      throw error;
    }
  }

  private async handleTokenRefresh<T>(endpoint: string, originalOptions: ApiRequestOptions): Promise<T> {
    if (this.isRefreshing) {
      return new Promise((resolve, reject) => {
        this.failedQueue.push({ resolve, reject });
      }).then(() => {
        return this.request<T>(endpoint, originalOptions);
      });
    }

    this.isRefreshing = true;

    try {
      await authService.refreshAccessToken();
      this.processQueue(null, authService.getAccessToken());
      
      return this.request<T>(endpoint, originalOptions);
    } catch (error) {
      this.processQueue(error, null);
      authService.clearTokens();
      throw error;
    } finally {
      this.isRefreshing = false;
    }
  }

  async get<T = any>(endpoint: string, options?: Omit<ApiRequestOptions, 'method' | 'body'>): Promise<T> {
    return this.request<T>(endpoint, { ...options, method: 'GET' });
  }

  async post<T = any>(endpoint: string, data?: any, options?: Omit<ApiRequestOptions, 'method' | 'body'>): Promise<T> {
    return this.request<T>(endpoint, {
      ...options,
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async put<T = any>(endpoint: string, data?: any, options?: Omit<ApiRequestOptions, 'method' | 'body'>): Promise<T> {
    return this.request<T>(endpoint, {
      ...options,
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async delete<T = any>(endpoint: string, options?: Omit<ApiRequestOptions, 'method' | 'body'>): Promise<T> {
    return this.request<T>(endpoint, { ...options, method: 'DELETE' });
  }
}

export const apiClient = new ApiClient();
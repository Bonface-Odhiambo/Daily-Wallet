import { API_CONFIG } from '@/config/api';

interface RequestConfig extends RequestInit {
  timeout?: number;
}

class HttpClient {
  private baseURL: string;
  private timeout: number;

  constructor(baseURL: string, timeout: number = 30000) {
    this.baseURL = baseURL;
    this.timeout = timeout;
  }

  private async request<T>(endpoint: string, config: RequestConfig = {}): Promise<T> {
    const token = localStorage.getItem('token');
    const headers = {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...config.headers,
    };

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), config.timeout || this.timeout);

    try {
      const response = await fetch(`${this.baseURL}${endpoint}`, {
        ...config,
        headers,
        signal: controller.signal,
      });

      clearTimeout(timeoutId);

      if (response.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/';
        throw new Error('Unauthorized');
      }

      if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'Request failed' }));
        throw new Error(error.message || `HTTP ${response.status}`);
      }

      return response.json();
    } catch (error) {
      clearTimeout(timeoutId);
      if (error instanceof Error && error.name === 'AbortError') {
        throw new Error('Request timeout');
      }
      throw error;
    }
  }

  async get<T>(endpoint: string, config?: RequestConfig): Promise<T> {
    return this.request<T>(endpoint, { ...config, method: 'GET' });
  }

  async post<T>(endpoint: string, data?: any, config?: RequestConfig): Promise<T> {
    return this.request<T>(endpoint, {
      ...config,
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async put<T>(endpoint: string, data?: any, config?: RequestConfig): Promise<T> {
    return this.request<T>(endpoint, {
      ...config,
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async delete<T>(endpoint: string, config?: RequestConfig): Promise<T> {
    return this.request<T>(endpoint, { ...config, method: 'DELETE' });
  }
}

const httpClient = new HttpClient(API_CONFIG.BASE_URL, API_CONFIG.TIMEOUT);

export default httpClient;

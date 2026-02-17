import httpClient from '@/lib/axios';
import { API_ENDPOINTS } from '@/config/api';

export interface RegisterRequest {
  phoneNumber: string;
  fullName: string;
  password: string;
}

export interface LoginRequest {
  phoneNumber: string;
  password: string;
}

export interface VerifyOtpRequest {
  phoneNumber: string;
  otpCode: string;
}

export interface AuthResponse {
  token: string;
  user: {
    id: number;
    phoneNumber: string;
    fullName: string;
    email?: string;
    isPhoneVerified: boolean;
  };
  isNewUser: boolean;
}

class AuthService {
  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response: any = await httpClient.post(API_ENDPOINTS.AUTH.REGISTER, data);
    if (response.success && response.data) {
      const authData = response.data;
      localStorage.setItem('token', authData.token);
      localStorage.setItem('user', JSON.stringify(authData.user));
      return authData;
    }
    throw new Error(response.message || 'Registration failed');
  }

  async login(data: LoginRequest): Promise<AuthResponse> {
    const response: any = await httpClient.post(API_ENDPOINTS.AUTH.LOGIN, data);
    if (response.success && response.data) {
      const authData = response.data;
      localStorage.setItem('token', authData.token);
      localStorage.setItem('user', JSON.stringify(authData.user));
      return authData;
    }
    throw new Error(response.message || 'Login failed');
  }

  async sendOtp(phoneNumber: string): Promise<void> {
    const response: any = await httpClient.post(API_ENDPOINTS.AUTH.SEND_OTP, { phoneNumber });
    if (!response.success) {
      throw new Error(response.message || 'Failed to send OTP');
    }
  }

  async verifyOtp(data: VerifyOtpRequest): Promise<void> {
    const response: any = await httpClient.post(API_ENDPOINTS.AUTH.VERIFY_OTP, data);
    if (!response.success) {
      throw new Error(response.message || 'OTP verification failed');
    }
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    user.isPhoneVerified = true;
    localStorage.setItem('user', JSON.stringify(user));
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/';
  }

  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}

export default new AuthService();

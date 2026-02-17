import httpClient from '@/lib/axios';
import { API_ENDPOINTS } from '@/config/api';

export interface Wallet {
  id: number;
  walletType: 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'SAVINGS';
  balance: number;
  lockedBalance: number;
  availableBalance: number;
  nextReleaseTime?: string;
  createdAt: string;
  updatedAt: string;
}

export interface SetAllocationRequest {
  dailyPercentage: number;
  weeklyPercentage: number;
  monthlyPercentage: number;
  savingsPercentage: number;
}

export interface ReallocateRequest {
  fromWalletType: string;
  toWalletType: string;
  amount: number;
}

class WalletService {
  async getAllWallets(): Promise<Wallet[]> {
    const response: any = await httpClient.get(API_ENDPOINTS.WALLETS.GET_ALL);
    if (response.success && response.data) {
      return response.data;
    }
    throw new Error(response.message || 'Failed to fetch wallets');
  }

  async setAllocation(data: SetAllocationRequest): Promise<void> {
    const response: any = await httpClient.post(API_ENDPOINTS.WALLETS.SET_ALLOCATION, data);
    if (!response.success) {
      throw new Error(response.message || 'Failed to set allocation');
    }
  }

  async reallocateFunds(data: ReallocateRequest): Promise<void> {
    const response: any = await httpClient.post(API_ENDPOINTS.WALLETS.REALLOCATE, data);
    if (!response.success) {
      throw new Error(response.message || 'Failed to reallocate funds');
    }
  }
}

export default new WalletService();

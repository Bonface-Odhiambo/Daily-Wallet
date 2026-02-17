import httpClient from '@/lib/axios';
import { API_ENDPOINTS } from '@/config/api';

export interface Transaction {
  id: number;
  transactionType: string;
  transactionCategory?: string;
  amount: number;
  status: string;
  description?: string;
  referenceNumber: string;
  mpesaReceiptNumber?: string;
  createdAt: string;
}

export interface DepositRequest {
  amount: number;
  phoneNumber: string;
}

export interface WithdrawRequest {
  walletType: string;
  amount: number;
  recipientPhoneNumber: string;
}

class TransactionService {
  async deposit(data: DepositRequest): Promise<void> {
    const response: any = await httpClient.post(API_ENDPOINTS.TRANSACTIONS.DEPOSIT, data);
    if (!response.success) {
      throw new Error(response.message || 'Deposit failed');
    }
  }

  async withdraw(data: WithdrawRequest): Promise<void> {
    const response: any = await httpClient.post(API_ENDPOINTS.TRANSACTIONS.WITHDRAW, data);
    if (!response.success) {
      throw new Error(response.message || 'Withdrawal failed');
    }
  }

  async getTransactions(page: number = 0, size: number = 20): Promise<Transaction[]> {
    const response: any = await httpClient.get(`${API_ENDPOINTS.TRANSACTIONS.GET_ALL}?page=${page}&size=${size}`);
    if (response.success && response.data) {
      return response.data.content || response.data;
    }
    throw new Error(response.message || 'Failed to fetch transactions');
  }

  async getRecentTransactions(): Promise<Transaction[]> {
    const response: any = await httpClient.get(API_ENDPOINTS.TRANSACTIONS.GET_RECENT);
    if (response.success && response.data) {
      return response.data;
    }
    throw new Error(response.message || 'Failed to fetch recent transactions');
  }
}

export default new TransactionService();

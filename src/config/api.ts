export const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  TIMEOUT: 30000,
};

export const API_ENDPOINTS = {
  AUTH: {
    REGISTER: '/auth/register',
    LOGIN: '/auth/login',
    SEND_OTP: '/auth/send-otp',
    VERIFY_OTP: '/auth/verify-otp',
  },
  WALLETS: {
    GET_ALL: '/wallets',
    GET_BY_TYPE: (type: string) => `/wallets/${type}`,
    SET_ALLOCATION: '/wallets/allocation',
    REALLOCATE: '/wallets/reallocate',
  },
  TRANSACTIONS: {
    DEPOSIT: '/transactions/deposit',
    WITHDRAW: '/transactions/withdraw',
    GET_ALL: '/transactions',
    GET_RECENT: '/transactions/recent',
  },
};

export const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_URL || 'https://daily-wallet-8ej6.onrender.com/api',
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
  // Jimudu Wallet Discipline endpoints
  DISCIPLINE: {
    BUCKETS: '/discipline/buckets',
    HEALTH_SCORE: '/discipline/health-score',
    SET_ALLOWANCE: '/discipline/daily-allowance',
    STREAKS: '/discipline/streaks',
    INSIGHTS: '/discipline/insights',
    REALLOCATE_FUNDS: '/discipline/reallocate',
  },
  // Jimudu Wallet MMF endpoints
  MMF: {
    INVESTMENTS: '/mmf/investments',
    SWEEP: '/mmf/sweep',
    YIELD_HISTORY: '/mmf/yield-history',
    PERFORMANCE: '/mmf/performance',
    AUTO_SWEEP_TOGGLE: '/mmf/auto-sweep-toggle',
    SWEEP_PREVIEW: '/mmf/sweep-preview',
  },
  // Jimudu Wallet Overdraft endpoints
  OVERDRAFT: {
    FACILITY: '/overdraft/facility',
    APPLY: '/overdraft/apply',
    PRICING: '/overdraft/pricing',
    DRAW: '/overdraft/draw',
    REPAYMENT_SCHEDULE: '/overdraft/repayment-schedule',
    USAGE_HISTORY: '/overdraft/usage-history',
    PRE_APPROVAL: '/overdraft/pre-approval',
  },
  // Jimudu Wallet Transparency endpoints
  TRANSPARENCY: {
    FEE_BREAKDOWN: '/transparency/fee-breakdown',
    TRANSACTION_COST: '/transparency/transaction-cost',
    PRICING_MODEL: '/transparency/pricing-model',
    FEE_HISTORY: '/transparency/fee-history',
  },
  // Jimudu Wallet Nudges endpoints
  NUDGES: {
    PERSONALIZED: '/nudges/personalized',
    SPENDING_INSIGHTS: '/nudges/spending-insights',
    ACT_ON_NUDGE: '/nudges/act-on-nudge',
    BEHAVIORAL_SCORES: '/nudges/behavioral-scores',
    RECOMMENDATIONS: '/nudges/recommendations',
    PROGRESS_MILESTONES: '/nudges/progress-milestones',
    SUBMIT_FEEDBACK: '/nudges/submit-feedback',
  },
  // Jimudu Wallet Analytics endpoints
  ANALYTICS: {
    WELLNESS_METRICS: '/analytics/wellness-metrics',
    SPENDING_PATTERNS: '/analytics/spending-patterns',
    EMERGENCY_PROGRESS: '/analytics/emergency-progress',
    HEALTH_TRENDS: '/analytics/health-trends',
    DISCIPLINE_METRICS: '/analytics/discipline-metrics',
    WEALTH_BUILDING: '/analytics/wealth-building',
    MONTHLY_SUMMARY: '/analytics/monthly-summary',
    COMPARATIVE_ANALYSIS: '/analytics/comparative-analysis',
  },
  // M-Pesa endpoints
  MPESA: {
    STK_PUSH: '/mpesa/stk-push',
    CALLBACK: '/mpesa/callback',
    DEPOSIT: '/mpesa/deposit',
    WITHDRAW: '/mpesa/withdraw',
  },
  // Market data endpoints (Alpha Vantage for NSE stocks)
  MARKET: {
    QUOTE: (symbol: string) => `/market/quote/${symbol}`,
    HISTORY: (symbol: string) => `/market/history/${symbol}`,
    QUOTES: (symbols: string) => `/market/quotes?symbols=${symbols}`,
    HEALTH: '/market/health',
  },
};

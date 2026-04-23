import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { Wallet, Calendar, CalendarDays, PiggyBank, ChevronRight } from "lucide-react";
import { Header } from "@/components/layout/Header";
import { WalletCard } from "@/components/wallet/WalletCard";
import { InterestBanner } from "@/components/wallet/InterestBanner";
import { QuickActions } from "@/components/wallet/QuickActions";
import { NudgeCard } from "@/components/wallet/NudgeCard";
import { TransactionItem } from "@/components/wallet/TransactionItem";
import { AddMoneyModal, SendMoneyModal, ReallocateModal } from "@/components/modals";
import { toast } from "sonner";

type TransactionType = "incoming" | "outgoing";
type TransactionCategory = "transfer" | "shopping" | "transport" | "food";

interface Transaction {
  type: TransactionType;
  category: TransactionCategory;
  title: string;
  amount: number;
  time: string;
}

// Mock data for demonstration
const initialWalletData = {
  daily: { amount: 100, progress: 60 },
  weekly: { amount: 375 },
  monthly: { amount: 1500 },
  savings: { amount: 4525, interest: 1.60 },
};

const mockTransactions: Transaction[] = [
  { type: "outgoing", category: "transport", title: "Matatu to Work", amount: 60, time: "8:30 AM" },
  { type: "outgoing", category: "food", title: "Lunch - Mama Pima", amount: 80, time: "1:15 PM" },
  { type: "incoming", category: "transfer", title: "M-Pesa Deposit", amount: 1200, time: "Yesterday" },
  { type: "outgoing", category: "shopping", title: "Airtime Purchase", amount: 50, time: "Yesterday" },
];

export default function Index() {
  const navigate = useNavigate();
  const [showNudge, setShowNudge] = useState(true);
  const [walletData, setWalletData] = useState(initialWalletData);
  const [transactions, setTransactions] = useState<Transaction[]>(mockTransactions);
  
  // Modal states
  const [isAddMoneyOpen, setIsAddMoneyOpen] = useState(false);
  const [isSendMoneyOpen, setIsSendMoneyOpen] = useState(false);
  const [isReallocateOpen, setIsReallocateOpen] = useState(false);

  const handleAddMoney = () => {
    setIsAddMoneyOpen(true);
  };

  const handleSendMoney = () => {
    setIsSendMoneyOpen(true);
  };

  const handleAddMoneySuccess = (amount: number) => {
    // Update wallet balance
    setWalletData(prev => ({
      ...prev,
      savings: {
        ...prev.savings,
        amount: prev.savings.amount + amount
      }
    }));
    
    // Add transaction to history
    const newTransaction: Transaction = {
      type: "incoming",
      category: "transfer",
      title: "M-Pesa Deposit",
      amount: amount,
      time: "Just now"
    };
    setTransactions(prev => [newTransaction, ...prev]);
    
    toast.success("Money added successfully!", {
      description: `KES ${amount.toLocaleString()} has been added to your wallet`
    });
  };

  const handleSendMoneySuccess = (amount: number, recipient: string) => {
    // Update wallet balance
    setWalletData(prev => ({
      ...prev,
      daily: {
        ...prev.daily,
        amount: Math.max(0, prev.daily.amount - amount),
        progress: Math.min(100, prev.daily.progress + (amount / 100) * 40)
      }
    }));
    
    // Add transaction to history
    const newTransaction: Transaction = {
      type: "outgoing",
      category: "transfer",
      title: `Sent to ${recipient.slice(-8)}`,
      amount: amount,
      time: "Just now"
    };
    setTransactions(prev => [newTransaction, ...prev]);
    
    toast.success("Money sent successfully!", {
      description: `KES ${amount.toLocaleString()} sent to ${recipient}`
    });
  };

  const handleReallocate = () => {
    setIsReallocateOpen(true);
  };

  const handleReallocateSuccess = (from: string, to: string, amount: number) => {
    const fromKey = from as keyof typeof walletData;
    const toKey = to as keyof typeof walletData;
    
    setWalletData(prev => ({
      ...prev,
      [fromKey]: {
        ...prev[fromKey],
        amount: (prev[fromKey].amount || 0) - amount
      },
      [toKey]: {
        ...prev[toKey],
        amount: (prev[toKey].amount || 0) + amount
      }
    }));
    
    toast.success("Funds reallocated!", {
      description: `KES ${amount.toLocaleString()} moved successfully`
    });
  };

  const handleHistory = () => {
    navigate("/history");
  };

  const totalBalance = walletData.daily.amount + walletData.weekly.amount + 
                       walletData.monthly.amount + walletData.savings.amount;

  return (
    <div className="min-h-screen bg-background">
      <div className="mx-auto max-w-md px-4 pb-24">
        <Header userName="John" />

        {/* Main Content */}
        <main className="space-y-5">
          {/* Behavioral Nudge */}
          {showNudge && (
            <NudgeCard
              type="achievement"
              title="Great job today! 🎉"
              message="You've stuck to your budget for 7 days straight. Keep it up!"
              onDismiss={() => setShowNudge(false)}
            />
          )}

          {/* Daily Wallet - Featured */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.5 }}
          >
            <WalletCard
              title="Today's Money"
              amount={walletData.daily.amount}
              icon={Wallet}
              variant="daily"
              subtitle={`KES ${Math.max(0, walletData.daily.amount - 60)} remaining`}
              progress={walletData.daily.progress}
            />
          </motion.div>

          {/* Secondary Wallets Grid */}
          <div className="grid grid-cols-2 gap-3">
            <WalletCard
              title="This Week"
              amount={walletData.weekly.amount}
              icon={Calendar}
              variant="weekly"
              delay={0.1}
            />
            <WalletCard
              title="This Month"
              amount={walletData.monthly.amount}
              icon={CalendarDays}
              variant="monthly"
              delay={0.2}
            />
          </div>

          {/* Savings & Interest */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            <WalletCard
              title="Total Savings"
              amount={walletData.savings.amount}
              icon={PiggyBank}
              variant="savings"
              subtitle="Earning 13% annual interest"
            />
          </motion.div>

          {/* Interest Banner */}
          <InterestBanner
            interestEarned={walletData.savings.interest}
            totalSavings={walletData.savings.amount}
          />

          {/* Quick Actions */}
          <QuickActions
            onAddMoney={handleAddMoney}
            onSendMoney={handleSendMoney}
            onReallocate={handleReallocate}
            onHistory={handleHistory}
          />

          {/* Recent Transactions */}
          <motion.section
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.6 }}
            className="space-y-3"
          >
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-foreground">
                Recent Activity
              </h2>
              <button 
                onClick={() => navigate("/history")}
                className="flex items-center gap-1 text-sm text-primary font-medium hover:text-primary-glow transition-colors"
              >
                See all
                <ChevronRight className="h-4 w-4" />
              </button>
            </div>

            <div className="rounded-2xl border border-border bg-card overflow-hidden">
              {transactions.slice(0, 4).map((tx, index) => (
                <TransactionItem
                  key={`${tx.title}-${index}`}
                  type={tx.type}
                  category={tx.category}
                  title={tx.title}
                  amount={tx.amount}
                  time={tx.time}
                  delay={0.7 + index * 0.1}
                />
              ))}
            </div>
          </motion.section>
        </main>
      </div>

      {/* Modals */}
      <AddMoneyModal
        isOpen={isAddMoneyOpen}
        onClose={() => setIsAddMoneyOpen(false)}
        onSuccess={handleAddMoneySuccess}
      />

      <SendMoneyModal
        isOpen={isSendMoneyOpen}
        onClose={() => setIsSendMoneyOpen(false)}
        availableBalance={walletData.daily.amount}
        onSuccess={handleSendMoneySuccess}
      />

      <ReallocateModal
        isOpen={isReallocateOpen}
        onClose={() => setIsReallocateOpen(false)}
        walletBalances={{
          daily: walletData.daily.amount,
          weekly: walletData.weekly.amount,
          monthly: walletData.monthly.amount,
          savings: walletData.savings.amount,
        }}
        onSuccess={handleReallocateSuccess}
      />
    </div>
  );
}

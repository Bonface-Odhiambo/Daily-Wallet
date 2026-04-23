import { useState } from "react";
import { motion } from "framer-motion";
import { 
  Wallet, Calendar, CalendarDays, PiggyBank, 
  Plus, Minus, ArrowRight, Lock, Unlock 
} from "lucide-react";
import { Header } from "@/components/layout/Header";
import { Button } from "@/components/ui/button";
import { AddMoneyModal, SendMoneyModal } from "@/components/modals";
import { toast } from "sonner";

interface WalletDetailProps {
  title: string;
  amount: number;
  color: string;
  icon: React.ReactNode;
  description: string;
  isLocked?: boolean;
  releaseInfo?: string;
  onAdd?: () => void;
  onWithdraw?: () => void;
}

function WalletDetailCard({ 
  title, amount, color, icon, description, isLocked, releaseInfo, onAdd, onWithdraw 
}: WalletDetailProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className={`relative overflow-hidden rounded-2xl p-5 ${color}`}
    >
      <div className="flex items-start justify-between">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <p className="text-sm font-medium text-white/80">{title}</p>
            {isLocked ? (
              <Lock className="h-3.5 w-3.5 text-white/60" />
            ) : (
              <Unlock className="h-3.5 w-3.5 text-white/60" />
            )}
          </div>
          <p className="text-3xl font-bold text-white">
            KES {amount.toLocaleString()}
          </p>
          <p className="text-xs text-white/70">{description}</p>
          {releaseInfo && (
            <p className="text-xs text-white/90 font-medium mt-2">
              {releaseInfo}
            </p>
          )}
        </div>
        <div className="rounded-xl p-3 bg-white/20">
          {icon}
        </div>
      </div>

      <div className="mt-4 flex gap-2">
        <Button 
          size="sm" 
          variant="glass"
          className="flex-1 text-white border-white/30 hover:bg-white/20"
          onClick={onAdd}
        >
          <Plus className="h-4 w-4" /> Add
        </Button>
        <Button 
          size="sm" 
          variant="glass"
          className="flex-1 text-white border-white/30 hover:bg-white/20"
          disabled={isLocked}
          onClick={onWithdraw}
        >
          <Minus className="h-4 w-4" /> Withdraw
        </Button>
      </div>

      <div className="absolute -right-8 -bottom-8 h-32 w-32 rounded-full bg-white/10" />
    </motion.div>
  );
}

export default function WalletsPage() {
  const [isAddMoneyOpen, setIsAddMoneyOpen] = useState(false);
  const [isSendMoneyOpen, setIsSendMoneyOpen] = useState(false);
  
  const [walletBalances, setWalletBalances] = useState({
    daily: 100,
    weekly: 375,
    monthly: 1500,
    savings: 4525,
  });

  const handleAddMoneySuccess = (amount: number) => {
    setWalletBalances(prev => ({
      ...prev,
      savings: prev.savings + amount
    }));
    toast.success("Money added successfully!");
  };

  const handleSendMoneySuccess = (amount: number) => {
    setWalletBalances(prev => ({
      ...prev,
      daily: Math.max(0, prev.daily - amount)
    }));
    toast.success("Money sent successfully!");
  };

  const wallets = [
    {
      title: "Daily Wallet",
      amount: walletBalances.daily,
      color: "bg-gradient-to-br from-primary to-primary-glow",
      icon: <Wallet className="h-6 w-6 text-white" />,
      description: "Releases KES 100 every morning at 6:00 AM",
      isLocked: false,
      releaseInfo: "Tomorrow: KES 100 at 6:00 AM",
    },
    {
      title: "Weekly Wallet",
      amount: walletBalances.weekly,
      color: "bg-gradient-to-br from-[hsl(200,85%,45%)] to-[hsl(200,85%,55%)]",
      icon: <Calendar className="h-6 w-6 text-white" />,
      description: "Releases every Sunday at midnight",
      isLocked: true,
      releaseInfo: "Next release: Sunday, Dec 22 - KES 375",
    },
    {
      title: "Monthly Wallet",
      amount: walletBalances.monthly,
      color: "bg-gradient-to-br from-[hsl(260,70%,55%)] to-[hsl(260,70%,65%)]",
      icon: <CalendarDays className="h-6 w-6 text-white" />,
      description: "For rent, bills, and monthly obligations",
      isLocked: true,
      releaseInfo: "Next release: Jan 1, 2025 - Full amount",
    },
    {
      title: "Savings (MMF)",
      amount: walletBalances.savings,
      color: "bg-gradient-to-br from-accent to-accent-glow",
      icon: <PiggyBank className="h-6 w-6 text-accent-foreground" />,
      description: "Earning 13% annual interest • Instant access",
      isLocked: false,
      releaseInfo: "Interest earned today: KES 1.60",
    },
  ];

  const totalBalance = walletBalances.daily + walletBalances.weekly + 
                       walletBalances.monthly + walletBalances.savings;

  return (
    <div className="min-h-screen bg-background">
      <div className="mx-auto max-w-md px-4 pb-24">
        <Header userName="John" />

        <main className="space-y-4">
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-1"
          >
            <h1 className="text-2xl font-bold text-foreground">Your Wallets</h1>
            <p className="text-sm text-muted-foreground">
              Manage your income allocation across different time periods
            </p>
          </motion.div>

          {/* Total Balance Card */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="rounded-2xl border border-border bg-card p-5"
          >
            <p className="text-sm text-muted-foreground">Total Balance</p>
            <p className="text-3xl font-bold text-foreground">
              KES {totalBalance.toLocaleString()}
            </p>
            <div className="mt-3 flex items-center gap-2">
              <Button 
                size="sm" 
                variant="hero" 
                className="flex-1"
                onClick={() => setIsAddMoneyOpen(true)}
              >
                <Plus className="h-4 w-4" /> Add Money
              </Button>
              <Button size="sm" variant="outline" className="flex-1">
                <ArrowRight className="h-4 w-4" /> Reallocate
              </Button>
            </div>
          </motion.div>

          {/* Wallet Cards */}
          <div className="space-y-3">
            {wallets.map((wallet, index) => (
              <motion.div
                key={wallet.title}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
              >
                <WalletDetailCard 
                  {...wallet}
                  onAdd={() => setIsAddMoneyOpen(true)}
                  onWithdraw={() => setIsSendMoneyOpen(true)}
                />
              </motion.div>
            ))}
          </div>
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
        availableBalance={walletBalances.daily}
        onSuccess={handleSendMoneySuccess}
      />
    </div>
  );
}

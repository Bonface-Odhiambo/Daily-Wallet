import { useState, useMemo } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { 
  ArrowLeft, Search, Filter, Download, Calendar, 
  Wallet, ChevronDown, X, FileText, Share2
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { TransactionItem } from "@/components/wallet/TransactionItem";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { toast } from "sonner";

import { TransactionType, TransactionCategory } from "@/components/wallet/TransactionItem";

type WalletType = "daily" | "weekly" | "monthly" | "savings" | "all";

interface Transaction {
  id: string;
  type: TransactionType;
  category: TransactionCategory;
  title: string;
  amount: number;
  time: string;
  date: Date;
  wallet: WalletType;
}

// Extended mock data
const mockTransactions: Transaction[] = [
  { id: "1", type: "outgoing", category: "transport", title: "Matatu to Work", amount: 60, time: "8:30 AM", date: new Date(), wallet: "daily" },
  { id: "2", type: "outgoing", category: "food", title: "Lunch - Mama Pima", amount: 80, time: "1:15 PM", date: new Date(), wallet: "daily" },
  { id: "3", type: "incoming", category: "transfer", title: "M-Pesa Deposit", amount: 1200, time: "Yesterday", date: new Date(Date.now() - 86400000), wallet: "savings" },
  { id: "4", type: "outgoing", category: "shopping", title: "Airtime Purchase", amount: 50, time: "Yesterday", date: new Date(Date.now() - 86400000), wallet: "weekly" },
  { id: "5", type: "outgoing", category: "bills", title: "Electricity Token", amount: 500, time: "2 days ago", date: new Date(Date.now() - 172800000), wallet: "monthly" },
  { id: "6", type: "incoming", category: "transfer", title: "Weekly Allocation", amount: 375, time: "3 days ago", date: new Date(Date.now() - 259200000), wallet: "weekly" },
  { id: "7", type: "outgoing", category: "entertainment", title: "Movie Tickets", amount: 800, time: "4 days ago", date: new Date(Date.now() - 345600000), wallet: "weekly" },
  { id: "8", type: "outgoing", category: "food", title: "Groceries - Naivas", amount: 1500, time: "5 days ago", date: new Date(Date.now() - 432000000), wallet: "weekly" },
  { id: "9", type: "incoming", category: "transfer", title: "Salary Deposit", amount: 25000, time: "1 week ago", date: new Date(Date.now() - 604800000), wallet: "savings" },
  { id: "10", type: "outgoing", category: "transport", title: "Uber Ride", amount: 350, time: "1 week ago", date: new Date(Date.now() - 604800000), wallet: "daily" },
];

const dateFilters = [
  { value: "all", label: "All Time" },
  { value: "today", label: "Today" },
  { value: "week", label: "This Week" },
  { value: "month", label: "This Month" },
];

const categoryFilters = [
  { value: "all", label: "All Categories" },
  { value: "transfer", label: "Transfers" },
  { value: "shopping", label: "Shopping" },
  { value: "transport", label: "Transport" },
  { value: "food", label: "Food" },
  { value: "bills", label: "Bills" },
  { value: "entertainment", label: "Entertainment" },
];

const walletFilters = [
  { value: "all", label: "All Wallets" },
  { value: "daily", label: "Daily Wallet" },
  { value: "weekly", label: "Weekly Wallet" },
  { value: "monthly", label: "Monthly Wallet" },
  { value: "savings", label: "Savings" },
];

export default function History() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");
  const [dateFilter, setDateFilter] = useState("all");
  const [categoryFilter, setCategoryFilter] = useState("all");
  const [walletFilter, setWalletFilter] = useState("all");
  const [isFilterOpen, setIsFilterOpen] = useState(false);

  const filteredTransactions = useMemo(() => {
    return mockTransactions.filter((tx) => {
      // Search filter
      if (searchQuery && !tx.title.toLowerCase().includes(searchQuery.toLowerCase())) {
        return false;
      }

      // Date filter
      if (dateFilter !== "all") {
        const now = new Date();
        const txDate = tx.date;
        
        if (dateFilter === "today") {
          if (txDate.toDateString() !== now.toDateString()) return false;
        } else if (dateFilter === "week") {
          const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
          if (txDate < weekAgo) return false;
        } else if (dateFilter === "month") {
          const monthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
          if (txDate < monthAgo) return false;
        }
      }

      // Category filter
      if (categoryFilter !== "all" && tx.category !== categoryFilter) {
        return false;
      }

      // Wallet filter
      if (walletFilter !== "all" && tx.wallet !== walletFilter) {
        return false;
      }

      return true;
    });
  }, [searchQuery, dateFilter, categoryFilter, walletFilter]);

  const totalIncoming = filteredTransactions
    .filter(tx => tx.type === "incoming")
    .reduce((sum, tx) => sum + tx.amount, 0);

  const totalOutgoing = filteredTransactions
    .filter(tx => tx.type === "outgoing")
    .reduce((sum, tx) => sum + tx.amount, 0);

  const handleExport = (format: "csv" | "pdf") => {
    toast.success(`Exporting as ${format.toUpperCase()}`, {
      description: "Your transaction history will be downloaded shortly"
    });
  };

  const handleShare = () => {
    toast.success("Share link copied!", {
      description: "Transaction summary link copied to clipboard"
    });
  };

  const clearFilters = () => {
    setDateFilter("all");
    setCategoryFilter("all");
    setWalletFilter("all");
    setSearchQuery("");
  };

  const hasActiveFilters = dateFilter !== "all" || categoryFilter !== "all" || walletFilter !== "all" || searchQuery;

  return (
    <div className="min-h-screen bg-background">
      <div className="mx-auto max-w-md pb-24">
        {/* Header */}
        <header className="sticky top-0 z-10 bg-background/80 backdrop-blur-lg border-b border-border px-4 py-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Button
                variant="ghost"
                size="icon-sm"
                onClick={() => navigate("/")}
              >
                <ArrowLeft className="h-5 w-5" />
              </Button>
              <h1 className="text-lg font-semibold text-foreground">Transaction History</h1>
            </div>
            
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon-sm">
                  <Download className="h-5 w-5" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={() => handleExport("csv")}>
                  <FileText className="h-4 w-4 mr-2" />
                  Export as CSV
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => handleExport("pdf")}>
                  <FileText className="h-4 w-4 mr-2" />
                  Export as PDF
                </DropdownMenuItem>
                <DropdownMenuItem onClick={handleShare}>
                  <Share2 className="h-4 w-4 mr-2" />
                  Share Summary
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </header>

        <div className="px-4 py-4 space-y-4">
          {/* Search Bar */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Search transactions..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 pr-10 bg-card border-border"
            />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery("")}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              >
                <X className="h-4 w-4" />
              </button>
            )}
          </div>

          {/* Quick Filters */}
          <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
            <Select value={dateFilter} onValueChange={setDateFilter}>
              <SelectTrigger className="w-auto min-w-[120px] bg-card">
                <Calendar className="h-4 w-4 mr-2" />
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {dateFilters.map(filter => (
                  <SelectItem key={filter.value} value={filter.value}>
                    {filter.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>

            <Sheet open={isFilterOpen} onOpenChange={setIsFilterOpen}>
              <SheetTrigger asChild>
                <Button variant="outline" className="gap-2 bg-card relative">
                  <Filter className="h-4 w-4" />
                  More Filters
                  {hasActiveFilters && (
                    <span className="absolute -top-1 -right-1 h-2 w-2 bg-primary rounded-full" />
                  )}
                </Button>
              </SheetTrigger>
              <SheetContent side="bottom" className="h-[60vh] rounded-t-3xl">
                <SheetHeader>
                  <SheetTitle>Filter Transactions</SheetTitle>
                </SheetHeader>
                <div className="space-y-6 py-6">
                  <div className="space-y-2">
                    <label className="text-sm font-medium text-foreground">Category</label>
                    <Select value={categoryFilter} onValueChange={setCategoryFilter}>
                      <SelectTrigger className="w-full">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        {categoryFilters.map(filter => (
                          <SelectItem key={filter.value} value={filter.value}>
                            {filter.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    <label className="text-sm font-medium text-foreground">Wallet Type</label>
                    <Select value={walletFilter} onValueChange={setWalletFilter}>
                      <SelectTrigger className="w-full">
                        <Wallet className="h-4 w-4 mr-2" />
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        {walletFilters.map(filter => (
                          <SelectItem key={filter.value} value={filter.value}>
                            {filter.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="flex gap-3 pt-4">
                    <Button 
                      variant="outline" 
                      className="flex-1"
                      onClick={clearFilters}
                    >
                      Clear All
                    </Button>
                    <Button 
                      variant="default" 
                      className="flex-1"
                      onClick={() => setIsFilterOpen(false)}
                    >
                      Apply Filters
                    </Button>
                  </div>
                </div>
              </SheetContent>
            </Sheet>

            {hasActiveFilters && (
              <Button 
                variant="ghost" 
                size="sm" 
                onClick={clearFilters}
                className="text-destructive hover:text-destructive"
              >
                <X className="h-4 w-4 mr-1" />
                Clear
              </Button>
            )}
          </div>

          {/* Summary Cards */}
          <div className="grid grid-cols-2 gap-3">
            <motion.div 
              className="rounded-xl bg-gradient-to-br from-primary/10 to-primary/5 p-4 border border-primary/20"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
            >
              <p className="text-xs text-muted-foreground mb-1">Total Incoming</p>
              <p className="text-lg font-bold text-primary">+KES {totalIncoming.toLocaleString()}</p>
            </motion.div>
            <motion.div 
              className="rounded-xl bg-gradient-to-br from-destructive/10 to-destructive/5 p-4 border border-destructive/20"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
            >
              <p className="text-xs text-muted-foreground mb-1">Total Outgoing</p>
              <p className="text-lg font-bold text-destructive">-KES {totalOutgoing.toLocaleString()}</p>
            </motion.div>
          </div>

          {/* Transaction List */}
          <div className="rounded-2xl border border-border bg-card overflow-hidden">
            <AnimatePresence mode="popLayout">
              {filteredTransactions.length > 0 ? (
                filteredTransactions.map((tx, index) => (
                  <motion.div
                    key={tx.id}
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: "auto" }}
                    exit={{ opacity: 0, height: 0 }}
                    transition={{ duration: 0.2 }}
                  >
                    <TransactionItem
                      type={tx.type}
                      category={tx.category}
                      title={tx.title}
                      amount={tx.amount}
                      time={tx.time}
                      delay={0}
                    />
                  </motion.div>
                ))
              ) : (
                <motion.div 
                  className="p-8 text-center"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                >
                  <Search className="h-12 w-12 text-muted-foreground/50 mx-auto mb-3" />
                  <p className="text-muted-foreground">No transactions found</p>
                  <p className="text-sm text-muted-foreground/70">Try adjusting your filters</p>
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          <p className="text-center text-xs text-muted-foreground">
            Showing {filteredTransactions.length} of {mockTransactions.length} transactions
          </p>
        </div>
      </div>
    </div>
  );
}

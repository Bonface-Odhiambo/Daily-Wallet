import { useState } from "react";
import { motion } from "framer-motion";
import { 
  TrendingUp, Calendar, PieChart as PieChartIcon, 
  ArrowUpRight, ArrowDownRight, Wallet
} from "lucide-react";
import { Header } from "@/components/layout/Header";
import { PieChart, Pie, Cell, ResponsiveContainer } from "recharts";

const spendingData = [
  { name: "Transport", value: 2400, color: "hsl(168, 80%, 35%)" },
  { name: "Food", value: 1800, color: "hsl(200, 85%, 45%)" },
  { name: "Shopping", value: 900, color: "hsl(260, 70%, 55%)" },
  { name: "Airtime", value: 600, color: "hsl(38, 95%, 55%)" },
  { name: "Other", value: 300, color: "hsl(220, 15%, 60%)" },
];

const weeklyTrend = [
  { day: "Mon", spent: 120, budget: 150 },
  { day: "Tue", spent: 180, budget: 150 },
  { day: "Wed", spent: 90, budget: 150 },
  { day: "Thu", spent: 140, budget: 150 },
  { day: "Fri", spent: 200, budget: 150 },
  { day: "Sat", spent: 100, budget: 150 },
  { day: "Sun", spent: 70, budget: 150 },
];

export default function InsightsPage() {
  const totalSpent = spendingData.reduce((acc, item) => acc + item.value, 0);

  return (
    <div className="min-h-screen bg-background">
      <div className="mx-auto max-w-md px-4 pb-24">
        <Header userName="John" />

        <main className="space-y-5">
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-1"
          >
            <h1 className="text-2xl font-bold text-foreground">Insights</h1>
            <p className="text-sm text-muted-foreground">
              Understand your spending patterns
            </p>
          </motion.div>

          {/* Summary Cards */}
          <div className="grid grid-cols-2 gap-3">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="rounded-2xl border border-border bg-card p-4"
            >
              <div className="flex items-center gap-2 text-primary">
                <ArrowDownRight className="h-4 w-4" />
                <span className="text-xs font-medium">This Month</span>
              </div>
              <p className="text-2xl font-bold text-foreground mt-1">
                KES {totalSpent.toLocaleString()}
              </p>
              <p className="text-xs text-muted-foreground">Total Spent</p>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="rounded-2xl border border-border bg-card p-4"
            >
              <div className="flex items-center gap-2 text-accent">
                <ArrowUpRight className="h-4 w-4" />
                <span className="text-xs font-medium">Interest</span>
              </div>
              <p className="text-2xl font-bold text-foreground mt-1">
                KES 48.50
              </p>
              <p className="text-xs text-muted-foreground">Earned This Month</p>
            </motion.div>
          </div>

          {/* Spending Breakdown */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="rounded-2xl border border-border bg-card p-5"
          >
            <div className="flex items-center gap-2 mb-4">
              <PieChartIcon className="h-5 w-5 text-primary" />
              <h2 className="font-semibold text-foreground">Spending Breakdown</h2>
            </div>

            <div className="flex items-center gap-4">
              <div className="h-36 w-36">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={spendingData}
                      cx="50%"
                      cy="50%"
                      innerRadius={35}
                      outerRadius={60}
                      paddingAngle={3}
                      dataKey="value"
                    >
                      {spendingData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                  </PieChart>
                </ResponsiveContainer>
              </div>

              <div className="flex-1 space-y-2">
                {spendingData.map((item) => (
                  <div key={item.name} className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <div 
                        className="h-2.5 w-2.5 rounded-full" 
                        style={{ backgroundColor: item.color }}
                      />
                      <span className="text-xs text-muted-foreground">{item.name}</span>
                    </div>
                    <span className="text-xs font-medium text-foreground">
                      KES {item.value.toLocaleString()}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </motion.div>

          {/* Weekly Trend */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="rounded-2xl border border-border bg-card p-5"
          >
            <div className="flex items-center gap-2 mb-4">
              <TrendingUp className="h-5 w-5 text-primary" />
              <h2 className="font-semibold text-foreground">Daily Spending</h2>
            </div>

            <div className="flex items-end justify-between gap-2 h-32">
              {weeklyTrend.map((day, index) => {
                const percentage = (day.spent / 200) * 100;
                const isOverBudget = day.spent > day.budget;
                return (
                  <motion.div
                    key={day.day}
                    initial={{ height: 0 }}
                    animate={{ height: `${percentage}%` }}
                    transition={{ delay: 0.4 + index * 0.05, duration: 0.5 }}
                    className="flex-1 flex flex-col items-center gap-1"
                  >
                    <div
                      className={`w-full rounded-t-lg ${
                        isOverBudget ? "bg-destructive/80" : "bg-primary"
                      }`}
                      style={{ height: "100%" }}
                    />
                    <span className="text-[10px] text-muted-foreground">{day.day}</span>
                  </motion.div>
                );
              })}
            </div>
            
            <div className="mt-3 flex items-center justify-center gap-4 text-xs">
              <div className="flex items-center gap-1.5">
                <div className="h-2 w-2 rounded-full bg-primary" />
                <span className="text-muted-foreground">Under budget</span>
              </div>
              <div className="flex items-center gap-1.5">
                <div className="h-2 w-2 rounded-full bg-destructive/80" />
                <span className="text-muted-foreground">Over budget</span>
              </div>
            </div>
          </motion.div>

          {/* Budget Status */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="rounded-2xl border border-border bg-card p-5"
          >
            <div className="flex items-center gap-2 mb-3">
              <Wallet className="h-5 w-5 text-primary" />
              <h2 className="font-semibold text-foreground">Budget Status</h2>
            </div>

            <div className="space-y-3">
              <div>
                <div className="flex items-center justify-between text-sm mb-1.5">
                  <span className="text-muted-foreground">Daily Budget</span>
                  <span className="font-medium text-foreground">KES 60 / 100</span>
                </div>
                <div className="h-2 rounded-full bg-muted">
                  <div className="h-full w-[60%] rounded-full bg-primary" />
                </div>
              </div>

              <div>
                <div className="flex items-center justify-between text-sm mb-1.5">
                  <span className="text-muted-foreground">Weekly Budget</span>
                  <span className="font-medium text-foreground">KES 680 / 1,050</span>
                </div>
                <div className="h-2 rounded-full bg-muted">
                  <div className="h-full w-[65%] rounded-full bg-[hsl(200,85%,45%)]" />
                </div>
              </div>

              <div>
                <div className="flex items-center justify-between text-sm mb-1.5">
                  <span className="text-muted-foreground">Monthly Budget</span>
                  <span className="font-medium text-foreground">KES 6,000 / 10,000</span>
                </div>
                <div className="h-2 rounded-full bg-muted">
                  <div className="h-full w-[60%] rounded-full bg-[hsl(260,70%,55%)]" />
                </div>
              </div>
            </div>
          </motion.div>
        </main>
      </div>
    </div>
  );
}

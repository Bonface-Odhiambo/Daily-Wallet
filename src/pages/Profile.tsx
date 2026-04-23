import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { 
  User, Shield, Bell, HelpCircle, 
  ChevronRight, LogOut, Phone, Mail,
  Building2, CreditCard, FileText, Settings,
  Edit2
} from "lucide-react";
import { Header } from "@/components/layout/Header";
import { Button } from "@/components/ui/button";
import { SettingsModal, ProfileEditModal, HelpModal } from "@/components/modals";
import { toast } from "sonner";

interface MenuItem {
  icon: React.ElementType;
  label: string;
  description?: string;
  onClick?: () => void;
}

function MenuSection({ title, items }: { title: string; items: MenuItem[] }) {
  return (
    <div className="space-y-2">
      <h3 className="text-xs font-semibold text-muted-foreground uppercase tracking-wider px-1">
        {title}
      </h3>
      <div className="rounded-2xl border border-border bg-card overflow-hidden">
        {items.map((item, index) => (
          <motion.button
            key={item.label}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.05 }}
            onClick={item.onClick}
            className="w-full flex items-center gap-3 p-4 hover:bg-muted/50 transition-colors border-b border-border last:border-b-0"
          >
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary-light">
              <item.icon className="h-5 w-5 text-primary" />
            </div>
            <div className="flex-1 text-left">
              <p className="font-medium text-foreground">{item.label}</p>
              {item.description && (
                <p className="text-xs text-muted-foreground">{item.description}</p>
              )}
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </motion.button>
        ))}
      </div>
    </div>
  );
}

export default function ProfilePage() {
  const navigate = useNavigate();
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [isProfileEditOpen, setIsProfileEditOpen] = useState(false);
  const [isHelpOpen, setIsHelpOpen] = useState(false);

  const [profile, setProfile] = useState({
    name: "John Mwangi",
    phone: "+254 712 345 678",
    email: "john.mwangi@email.com",
  });

  const handleLogout = () => {
    toast.success("Logged out successfully");
    navigate("/onboarding");
  };

  const handleProfileSave = (updatedProfile: typeof profile) => {
    setProfile(updatedProfile);
  };

  const accountItems: MenuItem[] = [
    { icon: User, label: "Personal Information", description: "Name, ID, phone number", onClick: () => setIsProfileEditOpen(true) },
    { icon: Building2, label: "Bank Connection", description: "Equity Bank linked", onClick: () => toast.info("Bank settings coming soon") },
    { icon: CreditCard, label: "Payment Methods", description: "M-Pesa, Bank transfer", onClick: () => toast.info("Payment methods coming soon") },
  ];

  const settingsItems: MenuItem[] = [
    { icon: Bell, label: "Notifications", description: "Manage your alerts", onClick: () => setIsSettingsOpen(true) },
    { icon: Shield, label: "Security", description: "PIN, biometrics, privacy", onClick: () => setIsSettingsOpen(true) },
    { icon: Settings, label: "Preferences", description: "App settings", onClick: () => setIsSettingsOpen(true) },
  ];

  const supportItems: MenuItem[] = [
    { icon: HelpCircle, label: "Help Center", description: "FAQs and guides", onClick: () => setIsHelpOpen(true) },
    { icon: Phone, label: "Contact Support", description: "Get in touch", onClick: () => toast.info("Call: 0800 123 456") },
    { icon: FileText, label: "Terms & Privacy", description: "Legal documents", onClick: () => toast.info("Opening terms...") },
  ];

  return (
    <div className="min-h-screen bg-background">
      <div className="mx-auto max-w-md px-4 pb-24">
        <Header userName={profile.name.split(" ")[0]} />

        <main className="space-y-5">
          {/* Profile Card */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="rounded-2xl gradient-hero p-6 text-primary-foreground relative"
          >
            <button 
              onClick={() => setIsProfileEditOpen(true)}
              className="absolute top-4 right-4 p-2 rounded-xl bg-white/20 hover:bg-white/30 transition-colors"
            >
              <Edit2 className="h-4 w-4" />
            </button>
            
            <div className="flex items-center gap-4">
              <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white/20 text-2xl font-bold">
                {profile.name.split(" ").map(n => n[0]).join("").slice(0, 2)}
              </div>
              <div className="flex-1">
                <h2 className="text-xl font-bold">{profile.name}</h2>
                <p className="text-sm opacity-80">{profile.phone}</p>
                <p className="text-xs opacity-60 mt-0.5">Member since Dec 2024</p>
              </div>
            </div>

            <div className="mt-5 grid grid-cols-3 gap-3">
              <div className="rounded-xl bg-white/10 p-3 text-center">
                <p className="text-lg font-bold">28</p>
                <p className="text-[10px] opacity-70">Days Active</p>
              </div>
              <div className="rounded-xl bg-white/10 p-3 text-center">
                <p className="text-lg font-bold">KES 48</p>
                <p className="text-[10px] opacity-70">Interest Earned</p>
              </div>
              <div className="rounded-xl bg-white/10 p-3 text-center">
                <p className="text-lg font-bold">85%</p>
                <p className="text-[10px] opacity-70">Budget Score</p>
              </div>
            </div>
          </motion.div>

          {/* Verification Badge */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="rounded-2xl border border-primary/20 bg-primary-light p-4 flex items-center gap-3"
          >
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary">
              <Shield className="h-5 w-5 text-primary-foreground" />
            </div>
            <div className="flex-1">
              <p className="font-medium text-foreground">Account Verified</p>
              <p className="text-xs text-muted-foreground">
                Your account is secured with Equity Bank
              </p>
            </div>
          </motion.div>

          {/* Menu Sections */}
          <MenuSection title="Account" items={accountItems} />
          <MenuSection title="Settings" items={settingsItems} />
          <MenuSection title="Support" items={supportItems} />

          {/* Logout */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <Button 
              variant="ghost" 
              className="w-full text-destructive hover:text-destructive hover:bg-destructive/10"
              onClick={handleLogout}
            >
              <LogOut className="h-5 w-5" />
              Log Out
            </Button>
          </motion.div>

          {/* App Version */}
          <p className="text-center text-xs text-muted-foreground">
            DailyWallet v1.0.0
          </p>
        </main>
      </div>

      {/* Modals */}
      <SettingsModal isOpen={isSettingsOpen} onClose={() => setIsSettingsOpen(false)} />
      <ProfileEditModal 
        isOpen={isProfileEditOpen} 
        onClose={() => setIsProfileEditOpen(false)} 
        currentProfile={profile}
        onSave={handleProfileSave}
      />
      <HelpModal isOpen={isHelpOpen} onClose={() => setIsHelpOpen(false)} />
    </div>
  );
}

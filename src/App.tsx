import { useState, useEffect } from "react";
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import authService from "@/services/authService";
import LoginScreen from "./components/screens/LoginScreen.tsx";
import HomeScreen from "./components/screens/HomeScreen.tsx";
import MarketsScreen from "./components/screens/MarketsScreen.tsx";
import WalletScreen from "./components/screens/WalletScreen.tsx";
import BorrowScreen from "./components/screens/BorrowScreen.tsx";
import ProfileScreen from "./components/screens/ProfileScreen.tsx";
import PWAInstallButton from "./components/PWAInstallButton.tsx";

const queryClient = new QueryClient();

const App = () => {
  const [activeScreen, setActiveScreen] = useState("home");
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Check if user is already authenticated on app load
    const checkAuth = () => {
      const authenticated = authService.isAuthenticated();
      setIsAuthenticated(authenticated);
    };
    
    checkAuth();
  }, []);

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    authService.logout();
    setIsAuthenticated(false);
  };

  if (!isAuthenticated) {
    return (
      <QueryClientProvider client={queryClient}>
        <TooltipProvider>
          <Toaster />
          <Sonner />
          <div className="wrap">
            <div className="phone">
              <div className="phone-inner">
                <div className="sbar" id="sbar" style={{background: "#1C2340"}}>
                  <span className="stime" id="clk">9:41</span>
                  <div className="sicons">
                    <svg width="15" height="11" viewBox="0 0 15 11" fill="none">
                      <rect x="0" y="5" width="2.5" height="6" rx="1" fill="white" opacity=".4"/>
                      <rect x="3.5" y="3.5" width="2.5" height="7.5" rx="1" fill="white" opacity=".6"/>
                      <rect x="7" y="2" width="2.5" height="9" rx="1" fill="white" opacity=".8"/>
                      <rect x="10.5" y="0" width="2.5" height="11" rx="1" fill="white"/>
                    </svg>
                    <svg width="14" height="11" viewBox="0 0 14 11" fill="none">
                      <path d="M7 3C8.7 3 10.2 3.7 11.3 4.9L12.8 3.4C11.3 1.6 9.3.5 7 .5S2.7 1.6 1.2 3.4L2.7 4.9C3.8 3.7 5.3 3 7 3z" fill="white"/>
                      <path d="M7 6c1 0 1.9.4 2.6 1.1L11 5.7C9.9 4.5 8.5 3.8 7 3.8S4.1 4.5 3 5.7l1.4 1.4C5.1 6.4 6 6 7 6z" fill="white"/>
                      <circle cx="7" cy="9.5" r="1.5" fill="white"/>
                    </svg>
                    <svg width="23" height="11" viewBox="0 0 23 11" fill="none">
                      <rect x=".5" y="1.5" width="19" height="8" rx="2" stroke="white" strokeWidth="1"/>
                      <rect x="1.5" y="2.5" width="15" height="6" rx="1" fill="white"/>
                      <path d="M21 4v3a1.5 1.5 0 000-3z" fill="white" opacity=".45"/>
                    </svg>
                  </div>
                </div>
                <LoginScreen onLoginSuccess={handleLoginSuccess} />
              </div>
            </div>
          </div>
        </TooltipProvider>
      </QueryClientProvider>
    );
  }

  return (
    <QueryClientProvider client={queryClient}>
      <TooltipProvider>
        <Toaster />
        <Sonner />
        <div className="wrap">
          <div className="tabs">
            <button 
              className={`tab ${activeScreen === "home" ? "on" : ""}`} 
              onClick={() => setActiveScreen("home")}
            >
              Home
            </button>
            <button 
              className={`tab ${activeScreen === "market" ? "on" : ""}`} 
              onClick={() => setActiveScreen("market")}
            >
              Markets
            </button>
            <button 
              className={`tab ${activeScreen === "wallet" ? "on" : ""}`} 
              onClick={() => setActiveScreen("wallet")}
            >
              Wallet
            </button>
            <button 
              className={`tab ${activeScreen === "borrow" ? "on" : ""}`} 
              onClick={() => setActiveScreen("borrow")}
            >
              Borrow
            </button>
            <button 
              className={`tab ${activeScreen === "profile" ? "on" : ""}`} 
              onClick={() => setActiveScreen("profile")}
            >
              Profile
            </button>
          </div>

          <div className="phone">
            <div className="phone-inner">
              <div className="sbar" id="sbar" style={{background: "#1C2340"}}>
                <span className="stime" id="clk">9:41</span>
                <div className="sicons">
                  <svg width="15" height="11" viewBox="0 0 15 11" fill="none">
                    <rect x="0" y="5" width="2.5" height="6" rx="1" fill="white" opacity=".4"/>
                    <rect x="3.5" y="3.5" width="2.5" height="7.5" rx="1" fill="white" opacity=".6"/>
                    <rect x="7" y="2" width="2.5" height="9" rx="1" fill="white" opacity=".8"/>
                    <rect x="10.5" y="0" width="2.5" height="11" rx="1" fill="white"/>
                  </svg>
                  <svg width="14" height="11" viewBox="0 0 14 11" fill="none">
                    <path d="M7 3C8.7 3 10.2 3.7 11.3 4.9L12.8 3.4C11.3 1.6 9.3.5 7 .5S2.7 1.6 1.2 3.4L2.7 4.9C3.8 3.7 5.3 3 7 3z" fill="white"/>
                    <path d="M7 6c1 0 1.9.4 2.6 1.1L11 5.7C9.9 4.5 8.5 3.8 7 3.8S4.1 4.5 3 5.7l1.4 1.4C5.1 6.4 6 6 7 6z" fill="white"/>
                    <circle cx="7" cy="9.5" r="1.5" fill="white"/>
                  </svg>
                  <svg width="23" height="11" viewBox="0 0 23 11" fill="none">
                    <rect x=".5" y="1.5" width="19" height="8" rx="2" stroke="white" strokeWidth="1"/>
                    <rect x="1.5" y="2.5" width="15" height="6" rx="1" fill="white"/>
                    <path d="M21 4v3a1.5 1.5 0 000-3z" fill="white" opacity=".45"/>
                  </svg>
                </div>
              </div>

              {activeScreen === "home" && <HomeScreen setActiveScreen={setActiveScreen} />}
              {activeScreen === "market" && <MarketsScreen setActiveScreen={setActiveScreen} />}
              {activeScreen === "wallet" && <WalletScreen setActiveScreen={setActiveScreen} />}
              {activeScreen === "borrow" && <BorrowScreen setActiveScreen={setActiveScreen} />}
              {activeScreen === "profile" && <ProfileScreen setActiveScreen={setActiveScreen} onLogout={handleLogout} />}
            </div>
          </div>
        </div>
        <PWAInstallButton />
      </TooltipProvider>
    </QueryClientProvider>
  );
};

export default App;

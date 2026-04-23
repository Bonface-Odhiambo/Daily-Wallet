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
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    // Check if user is already authenticated on app load
    const checkAuth = () => {
      const authenticated = authService.isAuthenticated();
      setIsAuthenticated(authenticated);
    };
    
    checkAuth();
  }, []);

  useEffect(() => {
    // Update time every second
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    
    return () => clearInterval(timer);
  }, []);

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString('en-US', { 
      hour: 'numeric', 
      minute: '2-digit',
      hour12: false 
    });
  };

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
          <div className="min-h-screen w-full">
            <div className="hidden md:block sbar" id="sbar" style={{background: "#1C2340"}}>
              <span className="stime" id="clk">{formatTime(currentTime)}</span>
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
        </TooltipProvider>
      </QueryClientProvider>
    );
  }

  return (
    <QueryClientProvider client={queryClient}>
      <TooltipProvider>
        <Toaster />
        <Sonner />
        <div className="min-h-screen w-full pb-20">
          <div className="hidden md:block sbar" id="sbar" style={{background: "#1C2340"}}>
            <span className="stime" id="clk">{formatTime(currentTime)}</span>
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
        
        <nav className="fixed bottom-0 left-0 right-0 bg-white border-t flex justify-around items-center h-16 z-50">
          <button 
            className={`flex flex-col items-center justify-center w-full h-full ${activeScreen === "home" ? "text-orange-500" : "text-gray-500"}`}
            onClick={() => setActiveScreen("home")}
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
            <span className="text-xs mt-1">Home</span>
          </button>
          <button 
            className={`flex flex-col items-center justify-center w-full h-full ${activeScreen === "market" ? "text-orange-500" : "text-gray-500"}`}
            onClick={() => setActiveScreen("market")}
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
            </svg>
            <span className="text-xs mt-1">Markets</span>
          </button>
          <button 
            className={`flex flex-col items-center justify-center w-full h-full ${activeScreen === "wallet" ? "text-orange-500" : "text-gray-500"}`}
            onClick={() => setActiveScreen("wallet")}
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
            </svg>
            <span className="text-xs mt-1">Wallet</span>
          </button>
          <button 
            className={`flex flex-col items-center justify-center w-full h-full ${activeScreen === "borrow" ? "text-orange-500" : "text-gray-500"}`}
            onClick={() => setActiveScreen("borrow")}
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
            </svg>
            <span className="text-xs mt-1">Borrow</span>
          </button>
          <button 
            className={`flex flex-col items-center justify-center w-full h-full ${activeScreen === "profile" ? "text-orange-500" : "text-gray-500"}`}
            onClick={() => setActiveScreen("profile")}
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
            <span className="text-xs mt-1">Profile</span>
          </button>
        </nav>
        
        <PWAInstallButton />
      </TooltipProvider>
    </QueryClientProvider>
  );
};

export default App;

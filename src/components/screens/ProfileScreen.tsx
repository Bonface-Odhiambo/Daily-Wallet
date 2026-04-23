import { useState, useEffect } from 'react';
import authService from '@/services/authService';

interface ProfileScreenProps {
  setActiveScreen: (screen: string) => void;
  onLogout?: () => void;
}

export default function ProfileScreen({ setActiveScreen, onLogout }: ProfileScreenProps) {
  const [currentTime, setCurrentTime] = useState('9:41');

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      const time = now.getHours().toString().padStart(2, '0') + ':' + 
                   now.getMinutes().toString().padStart(2, '0');
      setCurrentTime(time);
    };
    
    updateTime();
    const interval = setInterval(updateTime, 15000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="screen on">
      <div className="topbar" style={{justifyContent: 'center', flexDirection: 'column', alignItems: 'center', padding: '16px 18px 22px'}}>
        <div style={{width: '68px', height: '68px', borderRadius: '50%', background: 'var(--or)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '24px', fontWeight: '500', color: '#fff', marginBottom: '10px', border: '3px solid ' + 'rgba(255,255,255,.2)'}}>
          AK
        </div>
        <div style={{fontSize: '16px', fontWeight: '500', color: '#fff', marginBottom: '3px'}}>
          Amara Kariuki
        </div>
        <div style={{fontSize: '10px', color: 'rgba(255,255,255,.45)', marginBottom: '10px'}}>
          amara.k@gmail.com · +254 712 345 678
        </div>
        <div style={{display: 'flex', gap: '6px', flexWrap: 'wrap', justifyContent: 'center'}}>
          <span style={{background: 'rgba(93,190,60,.2)', color: '#5DBE3C', padding: '3px 10px', borderRadius: '10px', fontSize: '10px', fontWeight: '500', border: '0.5px solid rgba(93,190,60,.3)'}}>
            KYC verified
          </span>
          <span style={{background: 'rgba(244,124,32,.2)', color: '#F47C20', padding: '3px 10px', borderRadius: '10px', fontSize: '10px', fontWeight: '500', border: '0.5px solid rgba(244,124,32,.3)'}}>
            14-day streak
          </span>
          <span style={{background: 'rgba(255,255,255,.1)', color: 'rgba(255,255,255,.7)', padding: '3px 10px', borderRadius: '10px', fontSize: '10px', fontWeight: '500'}}>
            Score 87
          </span>
        </div>
      </div>

      <div className="scr-body">
        <div style={{padding: '14px 16px', display: 'flex', flexDirection: 'column', gap: '10px'}}>
          <div style={{fontSize: '10px', fontWeight: '500', color: 'var(--t3)', letterSpacing: '.05em', textTransform: 'uppercase', padding: '0 2px'}}>
            Account
          </div>
          
          <div className="card">
            <div className="set-row">
              <div className="sico" style={{background: '#FEF3EA'}}>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <circle cx="8" cy="5.5" r="3" stroke="#F47C20" strokeWidth="1.2"/>
                  <path d="M2 14c0-3 2.7-4.5 6-4.5s6 1.5 6 4.5" stroke="#F47C20" strokeWidth="1.2" strokeLinecap="round"/>
                </svg>
              </div>
              <div className="si">
                <div className="sit">Personal details</div>
                <div className="sis">Name · ID · phone</div>
              </div>
              <span style={{color: 'var(--t3)', fontSize: '16px'}}>{'>'}</span>
            </div>
            
            <div className="set-row">
              <div className="sico" style={{background: '#EBF7E6'}}>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M8 1.5l2 4 4.5.5-3.5 3 1 4.5L8 11l-4 2.5 1-4.5-3.5-3L6 6l2-4.5z" stroke="#5DBE3C" strokeWidth="1.1" strokeLinejoin="round"/>
                </svg>
              </div>
              <div className="si">
                <div className="sit">Discipline streaks</div>
                <div className="sis">14-day current · 890 pts</div>
              </div>
              <span style={{color: 'var(--t3)', fontSize: '16px'}}>{'>'}</span>
            </div>
            
            <div className="set-row" style={{borderBottom: 'none'}}>
              <div className="sico" style={{background: '#EFF6FF'}}>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <rect x="1" y="4" width="14" height="9" rx="2" stroke="#3B82F6" strokeWidth="1.2"/>
                  <path d="M1 7.5h14" stroke="#3B82F6" strokeWidth="1.1"/>
                  <circle cx="12" cy="10" r="1" fill="#3B82F6"/>
                </svg>
              </div>
              <div className="si">
                <div className="sit">Linked accounts</div>
                <div className="sis">M-PESA · Equity Bank</div>
              </div>
              <span style={{color: 'var(--t3)', fontSize: '16px'}}>{'>'}</span>
            </div>
          </div>

          <div style={{fontSize: '10px', fontWeight: '500', color: 'var(--t3)', letterSpacing: '.05em', textTransform: 'uppercase', padding: '0 2px'}}>
            Settings
          </div>
          
          <div className="card">
            <div className="set-row">
              <div className="sico" style={{background: '#FEF3EA'}}>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <circle cx="8" cy="8" r="5.5" stroke="#F47C20" strokeWidth="1.2"/>
                  <path d="M8 5v4l2 1.5" stroke="#F47C20" strokeWidth="1.2" strokeLinecap="round"/>
                </svg>
              </div>
              <div className="si">
                <div className="sit">Daily allowance</div>
                <div className="sis">Currently KES 600/day</div>
              </div>
              <span style={{color: 'var(--t3)', fontSize: '16px'}}>{'>'}</span>
            </div>
            
            <div className="set-row">
              <div className="sico" style={{background: '#F5F3FF'}}>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M8 1.5a4.5 4.5 0 00-4.5 4.5v3L2 11h12l-1.5-2V6A4.5 4.5 0 008 1.5z" stroke="#7C3AED" strokeWidth="1.2"/>
                  <path d="M6.5 12.5a1.5 1.5 0 003 0" stroke="#7C3AED" strokeWidth="1.2" strokeLinecap="round"/>
                </svg>
              </div>
              <div className="si">
                <div className="sit">Notifications</div>
                <div className="sis">Nudges · alerts · sweeps</div>
              </div>
              <span style={{color: 'var(--t3)', fontSize: '16px'}}>{'>'}</span>
            </div>
            
            <div className="set-row" style={{borderBottom: 'none'}}>
              <div className="sico" style={{background: '#EBF7E6'}}>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M2 8l3.5-4 3 2.5 5-6" stroke="#5DBE3C" strokeWidth="1.3" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              </div>
              <div className="si">
                <div className="sit">MMF auto-sweep</div>
                <div className="sis">Enabled · over KES 500</div>
              </div>
              <div className="tog">
                <div className="tog-k"></div>
              </div>
            </div>
          </div>

          <button className="btn-o">
            Manage allowance settings
          </button>
          
          <button 
            className="btn-o" 
            onClick={() => {
              if (onLogout) {
                onLogout();
              } else {
                authService.logout();
              }
            }}
            style={{
              background: '#FEE2E2',
              color: '#DC2626',
              border: '1px solid #FCA5A5',
              marginTop: '10px'
            }}
          >
            Logout
          </button>
        </div>
      </div>

      <div className="navbar">
        <button className="nbi" onClick={() => setActiveScreen('home')}>
          <svg viewBox="0 0 22 22" fill="none">
            <path d="M3 10l8-7 8 7v10H3V10z" stroke="#9CA3AF" strokeWidth="1.4" strokeLinejoin="round"/>
          </svg>
          <span className="nbl">Home</span>
        </button>
        <button className="nbi" onClick={() => setActiveScreen('market')}>
          <svg viewBox="0 0 22 22" fill="none">
            <path d="M3 17l4.5-5.5 4 3.5 6-9" stroke="#9CA3AF" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          <span className="nbl">Markets</span>
        </button>
        <button className="nbi" onClick={() => setActiveScreen('wallet')}>
          <svg viewBox="0 0 22 22" fill="none">
            <rect x="2" y="5" width="18" height="13" rx="2" stroke="#9CA3AF" strokeWidth="1.4"/>
            <path d="M2 9.5h18" stroke="#9CA3AF" strokeWidth="1.2"/>
            <circle cx="16" cy="13.5" r="1.5" fill="#9CA3AF"/>
          </svg>
          <span className="nbl">Wallet</span>
        </button>
        <button className="nbi" onClick={() => setActiveScreen('borrow')}>
          <svg viewBox="0 0 22 22" fill="none">
            <circle cx="11" cy="11" r="7.5" stroke="#9CA3AF" strokeWidth="1.4"/>
            <path d="M11 7.5v5l3 2" stroke="#9CA3AF" strokeWidth="1.4" strokeLinecap="round"/>
          </svg>
          <span className="nbl">Borrow</span>
        </button>
        <button className="nbi on">
          <svg viewBox="0 0 22 22" fill="none">
            <circle cx="11" cy="8" r="3.5" stroke="#F47C20" strokeWidth="1.4"/>
            <path d="M3 20c0-4 3.6-6 8-6s8 2 8 6" stroke="#F47C20" strokeWidth="1.4" strokeLinecap="round"/>
          </svg>
          <span className="nbl" style={{color: 'var(--or)'}}>Profile</span>
        </button>
      </div>
    </div>
  );
}

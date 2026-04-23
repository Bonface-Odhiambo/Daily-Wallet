import { useState, useEffect } from 'react';

interface WalletScreenProps {
  setActiveScreen: (screen: string) => void;
}

export default function WalletScreen({ setActiveScreen }: WalletScreenProps) {
  return (
    <div className="screen on">
      <div className="scr-body">
        <div style={{padding: '14px 16px', display: 'flex', flexDirection: 'column', gap: '9px'}}>
          <div className="bkt-big" style={{borderLeft: '3px solid var(--or)'}}>
            <div className="bktb-hd">
              <div className="bktb-left">
                <div className="bktb-ico" style={{background: '#FEF3EA'}}>
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M2 6h12v8a2 2 0 01-2 2H4a2 2 0 01-2-2V6z" stroke="#F47C20" strokeWidth="1.2"/>
                    <path d="M0.5 6h15" stroke="#F47C20" strokeWidth="1.1" strokeLinecap="round"/>
                    <path d="M5 6V4a4 4 0 018 0v2" stroke="#F47C20" strokeWidth="1.2" strokeLinecap="round"/>
                  </svg>
                </div>
                <div>
                  <div className="bktb-name">Spending wallet</div>
                  <div className="bktb-sub">Daily controlled release</div>
                </div>
              </div>
              <span className="pill" style={{background: '#FEF3EA', color: '#8C4208'}}>Active</span>
            </div>
            <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '5px'}}>
              <span style={{fontSize: '11px', color: 'var(--t3)'}}>Spent today</span>
              <span style={{fontSize: '12px', fontWeight: '500', color: 'var(--t1)'}}>KES 420 / 600</span>
            </div>
            <div className="pb">
              <div className="pf" style={{width: '70%', background: 'var(--or)'}}></div>
            </div>
          </div>

          <div className="bkt-big" style={{borderLeft: '3px solid var(--pur)'}}>
            <div className="bktb-hd">
              <div className="bktb-left">
                <div className="bktb-ico" style={{background: 'var(--pur-lt)'}}>
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M8 1.5l2 4.5h5l-4 3 1.5 5L8 11.5l-4.5 3 1.5-5L1 6h5l2-4.5z" stroke="#7C3AED" strokeWidth="1.1" strokeLinejoin="round"/>
                  </svg>
                </div>
                <div>
                  <div className="bktb-name">Emergency savings</div>
                  <div className="bktb-sub">Ring-fenced reserve</div>
                </div>
              </div>
              <span className="pill" style={{background: 'var(--pur-lt)', color: 'var(--pur-dk)'}}>73%</span>
            </div>
            <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '5px'}}>
              <span style={{fontSize: '11px', color: 'var(--t3)'}}>KES 2,180 of KES 3,000</span>
              <span style={{fontSize: '10px', color: 'var(--pur)', fontWeight: '500'}}>9 days to complete</span>
            </div>
            <div className="pb">
              <div className="pf" style={{width: '73%', background: 'var(--pur)'}}></div>
            </div>
          </div>

          <div className="bkt-big" style={{borderLeft: '3px solid var(--blue)'}}>
            <div className="bktb-hd">
              <div className="bktb-left">
                <div className="bktb-ico" style={{background: 'var(--blue-lt)'}}>
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <path d="M2 12l3.5-4.5 3 2.5 5-6.5" stroke="#3B82F6" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
                    <circle cx="14" cy="4" r="2" fill="#3B82F6"/>
                  </svg>
                </div>
                <div>
                  <div className="bktb-name">MMF growth bucket</div>
                  <div className="bktb-sub">Nightly auto-sweep</div>
                </div>
              </div>
              <span className="pill" style={{background: 'var(--blue-lt)', color: 'var(--blue-dk)'}}>Earning</span>
            </div>
            <div style={{display: 'flex', justifyContent: 'space-between'}}>
              <span style={{fontSize: '11px', color: 'var(--t3)'}}>Invested</span>
              <span style={{fontSize: '13px', fontWeight: '500', color: 'var(--t1)'}}>KES 12,400</span>
            </div>
            <div style={{fontSize: '10px', color: 'var(--gr2)', marginTop: '3px'}}>
              +KES 148 earned this month · 1.2%/mo
            </div>
          </div>

          <div className="bkt-big" style={{borderLeft: '3px solid var(--amb)'}}>
            <div className="bktb-hd">
              <div className="bktb-left">
                <div className="bktb-ico" style={{background: 'var(--amb-lt)'}}>
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <circle cx="8" cy="8" r="5.5" stroke="#F59E0B" strokeWidth="1.2"/>
                    <path d="M8 5v4l2.5 1.5" stroke="#F59E0B" strokeWidth="1.2" strokeLinecap="round"/>
                  </svg>
                </div>
                <div>
                  <div className="bktb-name">Overdraft facility</div>
                  <div className="bktb-sub">Behavior-linked · 10%/mo</div>
                </div>
              </div>
              <span className="pill" style={{background: 'var(--amb-lt)', color: '#92400E'}}>Available</span>
            </div>
            <div style={{display: 'flex', justifyContent: 'space-between'}}>
              <span style={{fontSize: '11px', color: 'var(--t3)'}}>Available limit</span>
              <span style={{fontSize: '13px', fontWeight: '500', color: 'var(--gr2)'}}>KES 4,000</span>
            </div>
            <div className="pb">
              <div className="pf" style={{width: '0%', background: 'var(--amb)'}}></div>
            </div>
            <div style={{fontSize: '10px', color: 'var(--t3)', textAlign: 'center'}}>
              Full cost breakdown before every transaction
            </div>
          </div>

          <div className="card" style={{padding: '14px'}}>
            <div style={{fontSize: '13px', fontWeight: '500', color: 'var(--t1)', marginBottom: '10px'}}>
              Fee transparency preview
            </div>
            <div style={{background: '#F5F7FA', borderRadius: '9px', padding: '10px', marginBottom: '10px'}}>
              <div className="fee-ln">
                <span className="fl">Principal</span>
                <span className="fv">KES 2,000.00</span>
              </div>
              <div className="fee-ln">
                <span className="fl">Management fee</span>
                <span className="fv">KES 10.00</span>
              </div>
              <div className="fee-ln">
                <span className="fl">Transaction fee (0.2%)</span>
                <span className="fv">KES 4.00</span>
              </div>
              <div className="fee-ln">
                <span className="fl">Overdraft charge</span>
                <span className="fv" style={{color: 'var(--gr2)'}}>None</span>
              </div>
            </div>
            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: '#FEF3EA', borderRadius: '9px', padding: '10px 12px'}}>
              <div>
                <div style={{fontSize: '10px', color: 'var(--or-dk)', fontWeight: '500'}}>Total charged</div>
                <div style={{fontSize: '16px', fontWeight: '500', color: 'var(--or-dk)'}}>KES 2,014.00</div>
              </div>
              <button className="btn-p" style={{width: 'auto', padding: '9px 16px', fontSize: '12px'}}>
                Confirm
              </button>
            </div>
          </div>
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
        <button className="nbi on">
          <svg viewBox="0 0 22 22" fill="none">
            <rect x="2" y="5" width="18" height="13" rx="2" stroke="#F47C20" strokeWidth="1.4"/>
            <path d="M2 9.5h18" stroke="#F47C20" strokeWidth="1.2"/>
            <circle cx="16" cy="13.5" r="1.5" fill="#F47C20"/>
          </svg>
          <span className="nbl" style={{color: 'var(--or)'}}>Wallet</span>
        </button>
        <button className="nbi" onClick={() => setActiveScreen('borrow')}>
          <svg viewBox="0 0 22 22" fill="none">
            <circle cx="11" cy="11" r="7.5" stroke="#9CA3AF" strokeWidth="1.4"/>
            <path d="M11 7.5v5l3 2" stroke="#9CA3AF" strokeWidth="1.4" strokeLinecap="round"/>
          </svg>
          <span className="nbl">Borrow</span>
        </button>
        <button className="nbi" onClick={() => setActiveScreen('profile')}>
          <svg viewBox="0 0 22 22" fill="none">
            <circle cx="11" cy="8" r="3.5" stroke="#9CA3AF" strokeWidth="1.4"/>
            <path d="M3 20c0-4 3.6-6 8-6s8 2 8 6" stroke="#9CA3AF" strokeWidth="1.4" strokeLinecap="round"/>
          </svg>
          <span className="nbl">Profile</span>
        </button>
      </div>
    </div>
  );
}

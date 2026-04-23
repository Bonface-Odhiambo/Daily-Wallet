import { useState, useEffect } from 'react';
import { API_CONFIG } from '@/config/api';

interface BorrowScreenProps {
  setActiveScreen: (screen: string) => void;
}

export default function BorrowScreen({ setActiveScreen }: BorrowScreenProps) {
  const [borrowAmount, setBorrowAmount] = useState(1000);
  const [loading, setLoading] = useState(false);

  const calculateBorrowDetails = (amount: number) => {
    const interest = Math.round(amount * 0.1);
    const dailyCost = (amount / 30).toFixed(2);
    const total = amount + interest;
    
    return { principal: amount, interest, dailyCost, total };
  };

  const borrowDetails = calculateBorrowDetails(borrowAmount);

  const handleApplyOverdraft = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('Please login first to apply for overdraft.');
        return;
      }
      
      // Call overdraft application API
      const response = await fetch(`${API_CONFIG.BASE_URL}/overdraft/apply`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          requestedAmount: borrowAmount,
          purpose: 'Personal expenses',
          repaymentPlan: 'Next payday',
          repaymentDays: 30,
          acceptanceOfTerms: true,
          incomeSource: 'Employment',
          monthlyIncome: 50000,
          employmentStatus: 'Employed',
          reasonForOverdraft: 'Personal emergency fund'
        })
      });
      
      if (response.ok) {
        alert('Overdraft application submitted successfully! You will receive a decision shortly.');
      } else {
        const errorData = await response.json();
        if (response.status === 403) {
          alert('Authentication failed. Please login again.');
          localStorage.removeItem('token');
        } else {
          alert(`Failed to submit application: ${errorData.message || 'Please try again.'}`);
        }
      }
    } catch (error) {
      alert('Error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="screen on">
      <div className="scr-body">
        <div className="sec" style={{paddingTop: '14px'}}>
          <div className="mg">
            <div className="mc2">
              <div className="ml">Available limit</div>
              <div className="mv" style={{color: 'var(--gr2)'}}>KES 4,000</div>
              <div className="mch" style={{color: 'var(--gr2)'}}>Fully available</div>
            </div>
            <div className="mc2">
              <div className="ml">Discipline score</div>
              <div className="mv" style={{color: 'var(--or)'}}>87 / 100</div>
              <div className="mch" style={{color: 'var(--gr2)'}}>Excellent</div>
            </div>
          </div>

          <div className="card" style={{padding: '14px', marginBottom: '9px'}}>
            <div style={{fontSize: '13px', fontWeight: '500', color: 'var(--t1)', marginBottom: '10px'}}>
              How much do you need?
            </div>
            <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '6px'}}>
              <span style={{fontSize: '12px', color: 'var(--t3)'}}>Borrow amount</span>
              <span style={{fontSize: '14px', fontWeight: '500', color: 'var(--or)'}}>
                KES {borrowAmount.toLocaleString()}
              </span>
            </div>
            <input 
              type="range" 
              min="500" 
              max="4000" 
              step="500" 
              value={borrowAmount}
              style={{width: '100%', accentColor: '#F47C20'}}
              onChange={(e) => setBorrowAmount(parseInt(e.target.value))}
            />
            <div style={{display: 'flex', justifyContent: 'space-between', fontSize: '10px', color: 'var(--t3)', margin: '3px 0 12px'}}>
              <span>KES 500</span>
              <span>KES 4,000</span>
            </div>

            <div style={{background: '#F5F7FA', borderRadius: '9px', padding: '11px', marginBottom: '10px'}}>
              <div className="fee-ln">
                <span className="fl">Principal</span>
                <span className="fv">KES {borrowDetails.principal.toLocaleString()}</span>
              </div>
              <div className="fee-ln">
                <span className="fl">Interest (10%/mo)</span>
                <span className="fv">KES {borrowDetails.interest.toLocaleString()}</span>
              </div>
              <div className="fee-ln">
                <span className="fl">Daily cost</span>
                <span className="fv" style={{color: 'var(--gr2)'}}>KES {borrowDetails.dailyCost}</span>
              </div>
              <div className="fee-ln" style={{borderBottom: 'none', paddingBottom: '0'}}>
                <span className="fl" style={{fontWeight: '500', color: 'var(--t1)'}}>Total repay</span>
                <span style={{fontSize: '14px', fontWeight: '500', color: 'var(--or)'}}>
                  KES {borrowDetails.total.toLocaleString()}
                </span>
              </div>
            </div>

            <div style={{background: '#FEF3EA', borderRadius: '8px', padding: '9px 11px', fontSize: '11px', color: 'var(--or-dk)', marginBottom: '10px', lineHeight: '1.5'}}>
              No hidden fees · auto-repaid from next income · no rollover traps
            </div>

            <div style={{background: '#EBF7E6', borderRadius: '8px', padding: '9px 11px', fontSize: '11px', color: '#1E5C0E', marginBottom: '10px', lineHeight: '1.5'}}>
              <strong>Behavior-linked pricing:</strong> Your 87 discipline score qualifies you for 8% rate (vs standard 10%)
            </div>

            <button className="btn-p" onClick={handleApplyOverdraft} disabled={loading}>
              {loading ? 'Applying...' : 'Apply for overdraft'}
            </button>
          </div>

          <div className="card" style={{padding: '14px'}}>
            <div style={{fontSize: '13px', fontWeight: '500', color: 'var(--t1)', marginBottom: '12px'}}>
              Discipline score breakdown
            </div>
            <div className="sbr-row">
              <span className="sbr-lbl">Spending discipline</span>
              <div style={{flex: '1'}}>
                <div className="pb">
                  <div className="pf" style={{width: '92%', background: 'var(--or)'}}></div>
                </div>
              </div>
              <span className="sbr-val">92</span>
            </div>
            <div className="sbr-row">
              <span className="sbr-lbl">Emergency savings</span>
              <div style={{flex: '1'}}>
                <div className="pb">
                  <div className="pf" style={{width: '73%', background: 'var(--pur)'}}></div>
                </div>
              </div>
              <span className="sbr-val">73</span>
            </div>
            <div className="sbr-row">
              <span className="sbr-lbl">MMF consistency</span>
              <div style={{flex: '1'}}>
                <div className="pb">
                  <div className="pf" style={{width: '95%', background: 'var(--blue)'}}></div>
                </div>
              </div>
              <span className="sbr-val">95</span>
            </div>
            <div className="sbr-row" style={{marginBottom: '0'}}>
              <span className="sbr-lbl">Repayment history</span>
              <div style={{flex: '1'}}>
                <div className="pb">
                  <div className="pf" style={{width: '100%', background: 'var(--gr2)'}}></div>
                </div>
              </div>
              <span className="sbr-val">100</span>
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
        <button className="nbi" onClick={() => setActiveScreen('wallet')}>
          <svg viewBox="0 0 22 22" fill="none">
            <rect x="2" y="5" width="18" height="13" rx="2" stroke="#9CA3AF" strokeWidth="1.4"/>
            <path d="M2 9.5h18" stroke="#9CA3AF" strokeWidth="1.2"/>
            <circle cx="16" cy="13.5" r="1.5" fill="#9CA3AF"/>
          </svg>
          <span className="nbl">Wallet</span>
        </button>
        <button className="nbi on">
          <svg viewBox="0 0 22 22" fill="none">
            <circle cx="11" cy="11" r="7.5" stroke="#F47C20" strokeWidth="1.4"/>
            <path d="M11 7.5v5l3 2" stroke="#F47C20" strokeWidth="1.4" strokeLinecap="round"/>
          </svg>
          <span className="nbl" style={{color: 'var(--or)'}}>Borrow</span>
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

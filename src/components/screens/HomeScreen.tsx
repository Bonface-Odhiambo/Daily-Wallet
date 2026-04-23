import { useState, useEffect } from 'react';

interface HomeScreenProps {
  setActiveScreen: (screen: string) => void;
}

export default function HomeScreen({ setActiveScreen }: HomeScreenProps) {
  const [currentTime, setCurrentTime] = useState('9:41');
  const [showAddModal, setShowAddModal] = useState(false);
  const [showSendModal, setShowSendModal] = useState(false);
  const [showInvestModal, setShowInvestModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [addAmount, setAddAmount] = useState(1000);
  const [sendAmount, setSendAmount] = useState(500);
  const [recipientPhone, setRecipientPhone] = useState('');
  const [investAmount, setInvestAmount] = useState(1000);

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

  // API integration functions
  const handleAddMoney = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('Please login first to add money.');
        return;
      }
      
      // Get current user info from token or use a default
      const currentUserPhone = localStorage.getItem('userPhone') || '254712345684';
      
      // Call M-Pesa STK Push API
      const response = await fetch('http://localhost:8080/api/mpesa/deposit', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          phoneNumber: currentUserPhone,
          amount: addAmount
        })
      });
      
      if (response.ok) {
        alert(`M-Pesa deposit of KES ${addAmount} initiated! Check your phone for STK Push.`);
        setShowAddModal(false);
      } else {
        const errorData = await response.json();
        if (response.status === 403) {
          alert('Authentication failed. Please login again.');
          // Clear invalid token
          localStorage.removeItem('token');
        } else {
          alert(`Failed to initiate deposit: ${errorData.message || 'Please try again.'}`);
        }
      }
    } catch (error) {
      alert('Error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSendMoney = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('Please login first to send money.');
        return;
      }
      
      // Validate inputs
      if (!recipientPhone || recipientPhone.length < 10) {
        alert('Please enter a valid recipient phone number.');
        return;
      }
      
      if (sendAmount < 50 || sendAmount > 50000) {
        alert('Amount must be between KES 50 and KES 50,000.');
        return;
      }
      
      // Call transfer API
      const response = await fetch('http://localhost:8080/api/transactions/transfer', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          recipientPhone: recipientPhone,
          amount: sendAmount,
          transactionType: 'TRANSFER'
        })
      });
      
      if (response.ok) {
        alert(`KES ${sendAmount.toLocaleString()} sent successfully to ${recipientPhone}!`);
        setShowSendModal(false);
        setRecipientPhone('');
        setSendAmount(500);
      } else {
        const errorData = await response.json();
        if (response.status === 403) {
          alert('Authentication failed. Please login again.');
          localStorage.removeItem('token');
        } else {
          alert(`Failed to send money: ${errorData.message || 'Please try again.'}`);
        }
      }
    } catch (error) {
      alert('Error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleInvestMoney = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('Please login first to invest money.');
        return;
      }
      
      // Validate amount
      if (investAmount < 100 || investAmount > 100000) {
        alert('Investment amount must be between KES 100 and KES 100,000.');
        return;
      }
      
      // Call MMF sweep API
      const response = await fetch('http://localhost:8080/api/mmf/sweep', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          amount: investAmount,
          autoSweep: true,
          sweepFrequency: 'daily'
        })
      });
      
      if (response.ok) {
        alert(`Investment successful! KES ${investAmount.toLocaleString()} moved to MMF for better returns.`);
        setShowInvestModal(false);
        setInvestAmount(1000);
      } else {
        const errorData = await response.json();
        if (response.status === 403) {
          alert('Authentication failed. Please login again.');
          localStorage.removeItem('token');
        } else {
          alert(`Failed to invest: ${errorData.message || 'Please try again.'}`);
        }
      }
    } catch (error) {
      alert('Error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleBorrowMoney = () => {
    // Navigate to borrow screen
    setActiveScreen('borrow');
  };

  return (
    <div className="screen on">
      <div className="topbar">
        <div style={{display: 'flex', alignItems: 'center', gap: '9px'}}>
          <img 
            src="/JIMUDU APP LOGO.png" 
            alt="Jimudu Wallet Logo" 
            style={{width: '38px', height: '38px', borderRadius: '8px'}}
          />
          <div>
            <div style={{fontSize: '15px', fontWeight: '500', letterSpacing: '.4px', lineHeight: '1.1'}}>
              <span style={{color: '#F47C20'}}>JIMUDU</span> <span style={{color: '#10B981'}}>WALLET</span>
            </div>
            <div style={{fontSize: '9px', color: 'rgba(255,255,255,.45)', letterSpacing: '.7px'}}>
              SAVE. BUILD &amp; MANAGE WEALTH
            </div>
          </div>
        </div>
        <div style={{display: 'flex', gap: '7px'}}>
          <div className="ib">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M8 1.5a4.5 4.5 0 00-4.5 4.5v3L2 11h12l-1.5-2V6A4.5 4.5 0 008 1.5z" stroke="white" strokeWidth="1.2"/>
              <path d="M6.5 12.5a1.5 1.5 0 003 0" stroke="white" strokeWidth="1.2" strokeLinecap="round"/>
            </svg>
            <div className="ndot"></div>
          </div>
          <div className="ib">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <circle cx="8" cy="5.5" r="3" stroke="white" strokeWidth="1.2"/>
              <path d="M2 14c0-3.3 2.7-5 6-5s6 1.7 6 5" stroke="white" strokeWidth="1.2" strokeLinecap="round"/>
            </svg>
          </div>
        </div>
      </div>

      <div className="hero">
        <div className="hchip">
          <div className="hchip-dot"></div>
          <span className="hchip-txt">14-day discipline streak active</span>
        </div>
        <div style={{fontSize: '12px', color: 'rgba(255,255,255,.45)', marginBottom: '2px'}}>
          Good morning, Amara
        </div>
        <div className="bal-card">
          <div className="bal-lbl">Total balance</div>
          <div className="bal-num">KES 47,250</div>
          <div className="bal-sub">Updated just now · Nairobi, KE</div>
          <div className="bkts">
            <div className="bkt">
              <div className="bl">Today's spend</div>
              <div className="bv">KES 420</div>
              <div className="bs">of KES 600 limit</div>
            </div>
            <div className="bkt">
              <div className="bl">Emergency fund</div>
              <div className="bv">KES 2,180</div>
              <div className="bs">73% of goal</div>
            </div>
            <div className="bkt">
              <div className="bl">MMF yield</div>
              <div className="bv" style={{color: '#5DCAA5'}}>+KES 148</div>
              <div className="bs">this month</div>
            </div>
          </div>
        </div>
      </div>

      <div className="scr-body">
        <div className="qa-row" style={{background: 'var(--white)', paddingBottom: '14px'}}>
          <button className="qa" onClick={() => setShowAddModal(true)} disabled={loading}>
            <div className="qac" style={{background: '#FEF3EA'}}>
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                <path d="M9 3v12M3 9h12" stroke="#F47C20" strokeWidth="1.6" strokeLinecap="round"/>
              </svg>
            </div>
            <span className="qal">Add</span>
          </button>
          <button className="qa" onClick={() => setShowSendModal(true)} disabled={loading}>
            <div className="qac" style={{background: '#EBF7E6'}}>
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                <path d="M3 9h12M11 5l4 4-4 4" stroke="#5DBE3C" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <span className="qal">Send</span>
          </button>
          <button className="qa" onClick={() => setShowInvestModal(true)} disabled={loading}>
            <div className="qac" style={{background: '#EFF6FF'}}>
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                <path d="M2.5 14l4-4.5 3 2.5 5.5-6.5" stroke="#3B82F6" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </div>
            <span className="qal">Invest</span>
          </button>
          <button className="qa" onClick={handleBorrowMoney} disabled={loading}>
            <div className="qac" style={{background: '#F5F3FF'}}>
              <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
                <circle cx="9" cy="9" r="6.5" stroke="#7C3AED" strokeWidth="1.4"/>
                <path d="M9 6.5v4l2.5 1.5" stroke="#7C3AED" strokeWidth="1.4" strokeLinecap="round"/>
              </svg>
            </div>
            <span className="qal">Borrow</span>
          </button>
        </div>

        <div className="sec">
          <div className="sch">
            <span className="sct">Financial Health Score</span>
            <span style={{fontSize: '10px', background: '#FEF3EA', color: '#B45309', padding: '3px 9px', borderRadius: '10px', fontWeight: '500'}}>
              87/100
            </span>
          </div>
          <div className="card" style={{marginBottom: '0', padding: '12px 14px'}}>
            <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '8px'}}>
              <span style={{fontSize: '11px', color: 'var(--t2)'}}>Discipline Score</span>
              <span style={{fontSize: '12px', fontWeight: '500', color: 'var(--gr2)'}}>92</span>
            </div>
            <div className="pb" style={{marginBottom: '8px'}}>
              <div className="pf" style={{width: '92%', background: 'var(--gr2)'}}></div>
            </div>
            <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '8px'}}>
              <span style={{fontSize: '11px', color: 'var(--t2)'}}>Emergency Readiness</span>
              <span style={{fontSize: '12px', fontWeight: '500', color: 'var(--or)'}}>73</span>
            </div>
            <div className="pb" style={{marginBottom: '8px'}}>
              <div className="pf" style={{width: '73%', background: 'var(--or)'}}></div>
            </div>
            <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '3px'}}>
              <span style={{fontSize: '11px', color: 'var(--t2)'}}>Wealth Building</span>
              <span style={{fontSize: '12px', fontWeight: '500', color: 'var(--blue)'}}>85</span>
            </div>
            <div className="pb">
              <div className="pf" style={{width: '85%', background: 'var(--blue)'}}></div>
            </div>
          </div>
        </div>

        <div className="sec">
          <div className="sch">
            <span className="sct">Daily allowance</span>
            <span style={{fontSize: '10px', background: '#EBF7E6', color: '#1E5C0E', padding: '3px 9px', borderRadius: '10px', fontWeight: '500'}}>
              70% used
            </span>
          </div>
          <div className="card" style={{marginBottom: '0', padding: '12px 14px'}}>
            <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '5px'}}>
              <span style={{fontSize: '11px', color: 'var(--t2)'}}>Spent today</span>
              <span style={{fontSize: '12px', fontWeight: '500', color: 'var(--t1)'}}>
                KES 420 <span style={{fontWeight: '400', color: 'var(--t3)'}}>/ 600</span>
              </span>
            </div>
            <div className="pb">
              <div className="pf" style={{width: '70%', background: 'var(--or)'}}></div>
            </div>
            <div style={{display: 'flex', justifyContent: 'space-between', marginTop: '3px'}}>
              <span style={{fontSize: '10px', color: 'var(--t3)'}}>KES 180 remaining</span>
              <span style={{fontSize: '10px', color: 'var(--gr2)', fontWeight: '500'}}>On track</span>
            </div>
          </div>
        </div>

        <div className="sec">
          <div className="sch">
            <span className="sct">Recent activity</span>
            <button className="scm">See all</button>
          </div>
          <div className="card">
            <div className="tr">
              <div className="tic" style={{background: '#FFFBEB'}}>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M3 7h10v7a2 2 0 01-2 2H5a2 2 0 01-2-2V7z" stroke="#F59E0B" strokeWidth="1.2"/>
                  <path d="M1 7h14" stroke="#F59E0B" strokeWidth="1.2" strokeLinecap="round"/>
                  <path d="M6 7V5a3 3 0 016 0v2" stroke="#F59E0B" strokeWidth="1.2" strokeLinecap="round"/>
                </svg>
              </div>
              <div className="ti">
                <div className="tn2">Naivas Supermarket</div>
                <div className="tc">Food · 09:14</div>
              </div>
              <div className="tr2">
                <div className="ta neg">-KES 185</div>
                <div className="tt">Today</div>
              </div>
            </div>
            <div className="tr">
              <div className="tic" style={{background: '#EFF6FF'}}>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <rect x="1" y="4" width="14" height="9" rx="2" stroke="#3B82F6" strokeWidth="1.2"/>
                  <path d="M1 7.5h14" stroke="#3B82F6" strokeWidth="1.1"/>
                  <circle cx="12" cy="10" r="1" fill="#3B82F6"/>
                </svg>
              </div>
              <div className="ti">
                <div className="tn2">Matatu · CBD-Westlands</div>
                <div className="tc">Transport · 07:40</div>
              </div>
              <div className="tr2">
                <div className="ta neg">-KES 80</div>
                <div className="tt">Today</div>
              </div>
            </div>
            <div className="tr">
              <div className="tic" style={{background: '#EBF7E6'}}>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                  <path d="M2 12l3.5-4 3 2.5 5-6" stroke="#5DBE3C" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
                  <circle cx="13.5" cy="4.5" r="1.5" fill="#5DBE3C"/>
                </svg>
              </div>
              <div className="ti">
                <div className="tn2">MMF nightly sweep</div>
                <div className="tc">Auto-invest · midnight</div>
              </div>
              <div className="tr2">
                <div className="ta pos">+KES 148</div>
                <div className="tt">Yield</div>
              </div>
            </div>
          </div>
        </div>

        <div className="sec">
          <div className="sch">
            <span className="sct">Nudges for you</span>
          </div>
          <div className="nudge">
            <div className="nd" style={{background: 'var(--gr)'}}></div>
            <div>
              <div className="nt">
                You spend 34% less on weekends &mdash; a lower Saturday limit could finish your emergency fund 9 days sooner.
              </div>
              <div className="ns">Spending insight · today</div>
            </div>
          </div>
          <div className="nudge">
            <div className="nd" style={{background: 'var(--blue)'}}></div>
            <div>
              <div className="nt">
                Safaricom (SCOM) is 8% below its 90-day average. Your MMF balance can buy ~12 shares now.
              </div>
              <div className="ns">NSE market signal</div>
            </div>
          </div>
        </div>
      </div>

      <div className="navbar">
        <button className="nbi on">
          <svg viewBox="0 0 22 22" fill="none">
            <path d="M3 10l8-7 8 7v10H3V10z" stroke="#F47C20" strokeWidth="1.4" strokeLinejoin="round"/>
            <rect x="8" y="15" width="3" height="5" rx="1" fill="#F47C20" opacity=".35"/>
          </svg>
          <span className="nbl" style={{color: 'var(--or)'}}>Home</span>
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
        <button className="nbi" onClick={() => setActiveScreen('profile')}>
          <svg viewBox="0 0 22 22" fill="none">
            <circle cx="11" cy="8" r="3.5" stroke="#9CA3AF" strokeWidth="1.4"/>
            <path d="M3 20c0-4 3.6-6 8-6s8 2 8 6" stroke="#9CA3AF" strokeWidth="1.4" strokeLinecap="round"/>
          </svg>
          <span className="nbl">Profile</span>
        </button>
      </div>

      {/* Add Money Modal */}
      {showAddModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0,0,0,0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{
            background: 'white',
            padding: '20px',
            borderRadius: '12px',
            width: '320px',
            textAlign: 'center'
          }}>
            <h3 style={{marginBottom: '15px'}}>Add Money</h3>
            <p style={{marginBottom: '15px', color: '#666'}}>Enter amount to deposit via M-Pesa</p>
            
            <div style={{marginBottom: '15px'}}>
              <label style={{display: 'block', marginBottom: '5px', fontSize: '14px', color: '#374151'}}>
                Amount (KES)
              </label>
              <input
                type="number"
                value={addAmount}
                onChange={(e) => {
                  const value = parseInt(e.target.value);
                  if (!isNaN(value) && value > 0 && value <= 50000) {
                    setAddAmount(value);
                  }
                }}
                min="50"
                max="50000"
                step="50"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '16px',
                  textAlign: 'center'
                }}
                placeholder="Enter amount"
              />
              <div style={{fontSize: '12px', color: '#6b7280', marginTop: '5px'}}>
                Min: KES 50, Max: KES 50,000
              </div>
            </div>
            
            <div style={{display: 'flex', gap: '10px', justifyContent: 'center'}}>
              <button 
                onClick={handleAddMoney}
                disabled={loading || addAmount < 50 || addAmount > 50000}
                style={{
                  background: loading || addAmount < 50 || addAmount > 50000 ? '#9ca3af' : '#F47C20',
                  color: 'white',
                  border: 'none',
                  padding: '10px 16px',
                  borderRadius: '6px',
                  cursor: loading || addAmount < 50 || addAmount > 50000 ? 'not-allowed' : 'pointer',
                  fontSize: '14px'
                }}
              >
                {loading ? 'Processing...' : `Add KES ${addAmount.toLocaleString()}`}
              </button>
              <button 
                onClick={() => setShowAddModal(false)}
                style={{
                  background: '#e5e7eb',
                  color: '#374151',
                  border: 'none',
                  padding: '10px 16px',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '14px'
                }}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Send Money Modal */}
      {showSendModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0,0,0,0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{
            background: 'white',
            padding: '20px',
            borderRadius: '12px',
            width: '340px',
            textAlign: 'center'
          }}>
            <h3 style={{marginBottom: '15px'}}>Send Money</h3>
            <p style={{marginBottom: '15px', color: '#666'}}>Send money to another Jimudu Wallet user</p>
            
            <div style={{marginBottom: '15px'}}>
              <label style={{display: 'block', marginBottom: '5px', fontSize: '14px', color: '#374151', textAlign: 'left'}}>
                Recipient Phone Number
              </label>
              <input
                type="tel"
                value={recipientPhone}
                onChange={(e) => setRecipientPhone(e.target.value)}
                placeholder="254712345678"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px',
                  textAlign: 'center'
                }}
              />
              <div style={{fontSize: '12px', color: '#6b7280', marginTop: '5px'}}>
                Format: 2547XXXXXXXXX
              </div>
            </div>
            
            <div style={{marginBottom: '15px'}}>
              <label style={{display: 'block', marginBottom: '5px', fontSize: '14px', color: '#374151', textAlign: 'left'}}>
                Amount (KES)
              </label>
              <input
                type="number"
                value={sendAmount}
                onChange={(e) => {
                  const value = parseInt(e.target.value);
                  if (!isNaN(value) && value > 0 && value <= 50000) {
                    setSendAmount(value);
                  }
                }}
                min="50"
                max="50000"
                step="50"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '16px',
                  textAlign: 'center'
                }}
                placeholder="Enter amount"
              />
              <div style={{fontSize: '12px', color: '#6b7280', marginTop: '5px'}}>
                Min: KES 50, Max: KES 50,000
              </div>
            </div>
            
            <div style={{display: 'flex', gap: '10px', justifyContent: 'center'}}>
              <button 
                onClick={handleSendMoney}
                disabled={loading || !recipientPhone || recipientPhone.length < 10 || sendAmount < 50 || sendAmount > 50000}
                style={{
                  background: loading || !recipientPhone || recipientPhone.length < 10 || sendAmount < 50 || sendAmount > 50000 ? '#9ca3af' : '#5DBE3C',
                  color: 'white',
                  border: 'none',
                  padding: '10px 16px',
                  borderRadius: '6px',
                  cursor: loading || !recipientPhone || recipientPhone.length < 10 || sendAmount < 50 || sendAmount > 50000 ? 'not-allowed' : 'pointer',
                  fontSize: '14px'
                }}
              >
                {loading ? 'Sending...' : `Send KES ${sendAmount.toLocaleString()}`}
              </button>
              <button 
                onClick={() => {
                  setShowSendModal(false);
                  setRecipientPhone('');
                  setSendAmount(500);
                }}
                style={{
                  background: '#e5e7eb',
                  color: '#374151',
                  border: 'none',
                  padding: '10px 16px',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '14px'
                }}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Invest Money Modal */}
      {showInvestModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0,0,0,0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{
            background: 'white',
            padding: '20px',
            borderRadius: '12px',
            width: '340px',
            textAlign: 'center'
          }}>
            <h3 style={{marginBottom: '15px'}}>Invest Money</h3>
            <p style={{marginBottom: '15px', color: '#666'}}>Move money to MMF for better returns (1.2% monthly)</p>
            
            <div style={{marginBottom: '15px'}}>
              <label style={{display: 'block', marginBottom: '5px', fontSize: '14px', color: '#374151', textAlign: 'left'}}>
                Investment Amount (KES)
              </label>
              <input
                type="number"
                value={investAmount}
                onChange={(e) => {
                  const value = parseInt(e.target.value);
                  if (!isNaN(value) && value > 0 && value <= 100000) {
                    setInvestAmount(value);
                  }
                }}
                min="100"
                max="100000"
                step="100"
                style={{
                  width: '100%',
                  padding: '8px 12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '16px',
                  textAlign: 'center'
                }}
                placeholder="Enter investment amount"
              />
              <div style={{fontSize: '12px', color: '#6b7280', marginTop: '5px'}}>
                Min: KES 100, Max: KES 100,000
              </div>
            </div>
            
            <div style={{
              background: '#EFF6FF',
              borderRadius: '8px',
              padding: '12px',
              marginBottom: '15px',
              fontSize: '12px',
              color: '#1E40AF',
              lineHeight: '1.4'
            }}>
              <div style={{fontWeight: '600', marginBottom: '4px'}}>Investment Details:</div>
              <div>Expected monthly return: KES {(investAmount * 0.012).toLocaleString()}</div>
              <div>Annual return: ~14.4%</div>
              <div>Auto-sweep: Daily at midnight</div>
            </div>
            
            <div style={{display: 'flex', gap: '10px', justifyContent: 'center'}}>
              <button 
                onClick={handleInvestMoney}
                disabled={loading || investAmount < 100 || investAmount > 100000}
                style={{
                  background: loading || investAmount < 100 || investAmount > 100000 ? '#9ca3af' : '#3B82F6',
                  color: 'white',
                  border: 'none',
                  padding: '10px 16px',
                  borderRadius: '6px',
                  cursor: loading || investAmount < 100 || investAmount > 100000 ? 'not-allowed' : 'pointer',
                  fontSize: '14px'
                }}
              >
                {loading ? 'Investing...' : `Invest KES ${investAmount.toLocaleString()}`}
              </button>
              <button 
                onClick={() => {
                  setShowInvestModal(false);
                  setInvestAmount(1000);
                }}
                style={{
                  background: '#e5e7eb',
                  color: '#374151',
                  border: 'none',
                  padding: '10px 16px',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '14px'
                }}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

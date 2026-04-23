import { useState } from 'react';
import authService from '@/services/authService';

interface LoginScreenProps {
  onLoginSuccess: () => void;
}

export default function LoginScreen({ onLoginSuccess }: LoginScreenProps) {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          phoneNumber: phoneNumber.trim(),
          password: password
        }),
      });

      if (response.ok) {
        const result = await response.json();
        
        if (result.success && result.data) {
          localStorage.setItem('token', result.data.token);
          localStorage.setItem('user', JSON.stringify(result.data.user));
          onLoginSuccess();
          return;
        }
      } else {
        const error = await response.json();
        throw new Error(error.message || 'Login failed');
      }
    } catch (err: any) {
      setError(err.message || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="screen on">
      <div className="scr-body" style={{padding: '20px', display: 'flex', flexDirection: 'column', justifyContent: 'center', minHeight: '100vh'}}>
        <div style={{textAlign: 'center', marginBottom: '40px'}}>
          <svg width="60" height="60" viewBox="0 0 60 60" fill="none" style={{margin: '0 auto 20px'}}>
            <rect width="60" height="60" rx="15" fill="#F47C20"/>
            <path d="M15 27h30a3 3 0 013 3v12a3 3 0 01-3 3H15a3 3 0 01-3-3V30a3 3 0 013-3z" fill="white" opacity=".12"/>
            <path d="M15 27h30a3 3 0 013 3v12a3 3 0 01-3 3H15a3 3 0 01-3-3V30a3 3 0 013-3z" stroke="white" strokeWidth="2" fill="none"/>
            <path d="M22 27v-4a8 8 0 0116 0v4" stroke="white" strokeWidth="2" strokeLinecap="round" fill="none"/>
            <circle cx="37" cy="34" r="3" fill="white"/>
            <path d="M10 31l5-4 4 4" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" opacity=".7"/>
            <path d="M50 31l-5-4-4 4" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" opacity=".7"/>
          </svg>
          <div style={{fontSize: '24px', fontWeight: '600', color: 'var(--t1)', marginBottom: '8px'}}>
            <span style={{color: '#F47C20'}}>JIMUDU</span> <span style={{color: 'white'}}>WALLET</span>
          </div>
          <div style={{fontSize: '14px', color: 'rgba(255,255,255,.6)', letterSpacing: '.7px'}}>
            SAVE. BUILD & MANAGE WEALTH
          </div>
        </div>

        <div className="card" style={{padding: '30px 20px', maxWidth: '400px', margin: '0 auto'}}>
          <h2 style={{fontSize: '20px', fontWeight: '600', color: 'var(--t1)', marginBottom: '8px', textAlign: 'center'}}>
            Welcome Back
          </h2>
          <p style={{fontSize: '14px', color: 'var(--t3)', textAlign: 'center', marginBottom: '25px'}}>
            Login to access your Jimudu Wallet
          </p>

          <form onSubmit={handleLogin}>
            <div style={{marginBottom: '20px'}}>
              <label style={{display: 'block', marginBottom: '8px', fontSize: '14px', color: 'var(--t2)', fontWeight: '500'}}>
                Phone Number
              </label>
              <input
                type="tel"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                placeholder="2547XXXXXXXXX"
                required
                style={{
                  width: '100%',
                  padding: '12px 16px',
                  border: '1px solid #e5e7eb',
                  borderRadius: '8px',
                  fontSize: '16px',
                  outline: 'none',
                  transition: 'border-color 0.2s'
                }}
                onFocus={(e) => e.target.style.borderColor = '#F47C20'}
                onBlur={(e) => e.target.style.borderColor = '#e5e7eb'}
              />
            </div>

            <div style={{marginBottom: '25px'}}>
              <label style={{display: 'block', marginBottom: '8px', fontSize: '14px', color: 'var(--t2)', fontWeight: '500'}}>
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                required
                style={{
                  width: '100%',
                  padding: '12px 16px',
                  border: '1px solid #e5e7eb',
                  borderRadius: '8px',
                  fontSize: '16px',
                  outline: 'none',
                  transition: 'border-color 0.2s'
                }}
                onFocus={(e) => e.target.style.borderColor = '#F47C20'}
                onBlur={(e) => e.target.style.borderColor = '#e5e7eb'}
              />
            </div>

            {error && (
              <div style={{
                background: '#FEE2E2',
                color: '#DC2626',
                padding: '12px',
                borderRadius: '8px',
                fontSize: '14px',
                marginBottom: '20px',
                textAlign: 'center'
              }}>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading || !phoneNumber || !password}
              style={{
                width: '100%',
                background: loading || !phoneNumber || !password ? '#9ca3af' : '#F47C20',
                color: 'white',
                border: 'none',
                padding: '14px 20px',
                borderRadius: '8px',
                fontSize: '16px',
                fontWeight: '600',
                cursor: loading || !phoneNumber || !password ? 'not-allowed' : 'pointer',
                transition: 'background-color 0.2s'
              }}
            >
              {loading ? 'Logging in...' : 'Login'}
            </button>
          </form>
        </div>

        <div style={{textAlign: 'center', marginTop: '30px'}}>
          <div style={{fontSize: '12px', color: 'rgba(255,255,255,.5)', marginBottom: '15px'}}>
            Sample Login Credentials
          </div>
          <div style={{background: 'rgba(255,255,255,.1)', padding: '15px', borderRadius: '8px', fontSize: '11px', color: 'rgba(255,255,255,.8)'}}>
            <div style={{marginBottom: '8px'}}><strong>Phone:</strong> 254711234567</div>
            <div style={{marginBottom: '8px'}}><strong>Password:</strong> password123</div>
            <div style={{marginBottom: '8px'}}><strong>Name:</strong> Amara Ochieng</div>
            <div style={{fontSize: '10px', color: 'rgba(255,255,255,.6)', marginTop: '10px'}}>
              More users available in system
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

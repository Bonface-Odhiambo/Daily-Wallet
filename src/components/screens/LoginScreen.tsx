import { useState } from 'react';
import authService from '@/services/authService';

interface LoginScreenProps {
  onLoginSuccess: () => void;
}

export default function LoginScreen({ onLoginSuccess }: LoginScreenProps) {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [isLogin, setIsLogin] = useState(true);

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

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Validation
    if (password !== confirmPassword) {
      setError('Passwords do not match');
      setLoading(false);
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters');
      setLoading(false);
      return;
    }

    if (!phoneNumber.match(/^2547\d{8}$/)) {
      setError('Please enter a valid Kenyan phone number (2547XXXXXXXXX)');
      setLoading(false);
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          phoneNumber: phoneNumber.trim(),
          password: password,
          fullName: fullName.trim(),
          email: email.trim() || null
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
        throw new Error(error.message || 'Registration failed');
      }
    } catch (err: any) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = isLogin ? handleLogin : handleRegister;

  const toggleMode = () => {
    setIsLogin(!isLogin);
    setError('');
    setPhoneNumber('');
    setPassword('');
    setFullName('');
    setEmail('');
    setConfirmPassword('');
  };

  return (
    <div className="screen on">
      <div className="scr-body" style={{padding: '20px', display: 'flex', flexDirection: 'column', justifyContent: 'center', minHeight: '100vh'}}>
        <div style={{textAlign: 'center', marginBottom: '40px'}}>
          <img 
            src="/JIMUDU APP LOGO.png" 
            alt="Jimudu Wallet Logo" 
            style={{width: '60px', height: '60px', margin: '0 auto 20px', borderRadius: '12px'}}
          />
          <div style={{fontSize: '24px', fontWeight: '600', color: 'var(--t1)', marginBottom: '8px'}}>
            <span style={{color: '#F47C20'}}>JIMUDU</span> <span style={{color: '#10B981'}}>WALLET</span>
          </div>
          <div style={{fontSize: '14px', color: 'rgba(255,255,255,.6)', letterSpacing: '.7px'}}>
            SAVE. BUILD & MANAGE WEALTH
          </div>
        </div>

        <div className="card" style={{padding: '30px 20px', maxWidth: '400px', margin: '0 auto'}}>
          <h2 style={{fontSize: '20px', fontWeight: '600', color: 'var(--t1)', marginBottom: '8px', textAlign: 'center'}}>
            {isLogin ? 'Welcome Back' : 'Create Account'}
          </h2>
          <p style={{fontSize: '14px', color: 'var(--t3)', textAlign: 'center', marginBottom: '25px'}}>
            {isLogin ? 'Login to access your Jimudu Wallet' : 'Join Jimudu Wallet and start managing your finances'}
          </p>

          <form onSubmit={handleSubmit}>
            {!isLogin && (
              <div style={{marginBottom: '20px'}}>
                <label style={{display: 'block', marginBottom: '8px', fontSize: '14px', color: 'var(--t2)', fontWeight: '500'}}>
                  Full Name
                </label>
                <input
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="Enter your full name"
                  required={!isLogin}
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
            )}

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

            {!isLogin && (
              <div style={{marginBottom: '20px'}}>
                <label style={{display: 'block', marginBottom: '8px', fontSize: '14px', color: 'var(--t2)', fontWeight: '500'}}>
                  Email (Optional)
                </label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="Enter your email"
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
            )}

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

            {!isLogin && (
              <div style={{marginBottom: '25px'}}>
                <label style={{display: 'block', marginBottom: '8px', fontSize: '14px', color: 'var(--t2)', fontWeight: '500'}}>
                  Confirm Password
                </label>
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  placeholder="Confirm your password"
                  required={!isLogin}
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
            )}

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
              disabled={loading || !phoneNumber || !password || (!isLogin && (!fullName || password !== confirmPassword))}
              style={{
                width: '100%',
                background: loading || !phoneNumber || !password || (!isLogin && (!fullName || password !== confirmPassword)) ? '#9ca3af' : '#F47C20',
                color: 'white',
                border: 'none',
                padding: '14px 20px',
                borderRadius: '8px',
                fontSize: '16px',
                fontWeight: '600',
                cursor: loading || !phoneNumber || !password || (!isLogin && (!fullName || password !== confirmPassword)) ? 'not-allowed' : 'pointer',
                transition: 'background-color 0.2s'
              }}
            >
              {loading ? (isLogin ? 'Logging in...' : 'Creating account...') : (isLogin ? 'Login' : 'Create Account')}
            </button>
          </form>

          <div style={{textAlign: 'center', marginTop: '20px'}}>
            <button
              type="button"
              onClick={toggleMode}
              style={{
                background: 'none',
                border: 'none',
                color: '#F47C20',
                fontSize: '14px',
                cursor: 'pointer',
                textDecoration: 'underline'
              }}
            >
              {isLogin ? "Don't have an account? Register here" : 'Already have an account? Login here'}
            </button>
          </div>
        </div>

        {isLogin && (
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
        )}
      </div>
    </div>
  );
}

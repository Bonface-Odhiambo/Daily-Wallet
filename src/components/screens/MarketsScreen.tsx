import { useState, useEffect } from 'react';

interface MarketsScreenProps {
  setActiveScreen: (screen: string) => void;
}

export default function MarketsScreen({ setActiveScreen }: MarketsScreenProps) {
  const [currentTime, setCurrentTime] = useState('9:41');
  const [nseData, setNseData] = useState([
    {sym: 'SCOM', name: 'Safaricom PLC', price: 13.85, chg: +0.15, pct: +1.10, up: true},
    {sym: 'EQTY', name: 'Equity Group', price: 38.20, chg: -0.30, pct: -0.78, up: false},
    {sym: 'KCB', name: 'KCB Group', price: 22.50, chg: +0.50, pct: +2.27, up: true},
    {sym: 'COOP', name: 'Co-op Bank', price: 11.90, chg: +0.10, pct: +0.85, up: true},
    {sym: 'ABSA', name: 'Absa Bank Kenya', price: 12.40, chg: -0.20, pct: -1.59, up: false},
    {sym: 'EABL', name: 'E.A. Breweries', price: 148.00, chg: +2.00, pct: +1.37, up: true},
  ]);
  const [nse20Value, setNse20Value] = useState('1847.3');

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

  useEffect(() => {
    const interval = setInterval(() => {
      // Update NSE data
      setNseData(prev => prev.map(s => {
        const delta = (Math.random() - 0.499) * 0.06;
        const newPrice = parseFloat(Math.max(s.price + delta, 0.5).toFixed(2));
        const newChg = parseFloat((s.chg + delta * 0.1).toFixed(2));
        const newPct = parseFloat((newChg / newPrice * 100).toFixed(2));
        return { ...s, price: newPrice, chg: newChg, pct: newPct, up: newChg >= 0 };
      }));

      // Update NSE 20 index
      const newValue = (1847.3 + (Math.random() - 0.48) * 3).toFixed(1);
      const delta = (parseFloat(newValue) - 1847.3).toFixed(1);
      setNse20Value(newValue);
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  const SparklineChart = ({ symbol, isUp }: { symbol: string; isUp: boolean }) => {
    const canvasId = `sp${symbol}`;
    
    useEffect(() => {
      const canvas = document.getElementById(canvasId) as HTMLCanvasElement;
      if (!canvas) return;
      
      const ctx = canvas.getContext('2d');
      if (!ctx) return;

      const points = Array.from({ length: 10 }, (_, i) => 
        20 + (Math.random() - 0.5) * 8 + (isUp ? i * 0.4 : -i * 0.22)
      );

      ctx.clearRect(0, 0, 48, 22);
      ctx.beginPath();
      ctx.moveTo(0, 22 - (points[0] - 12));
      
      points.forEach((p, i) => {
        ctx.lineTo(i * 48 / 9, 22 - (p - 12));
      });
      
      ctx.strokeStyle = isUp ? '#5DBE3C' : '#E24B4A';
      ctx.lineWidth = 1.5;
      ctx.stroke();
    }, [symbol, isUp]);

    return (
      <canvas id={canvasId} className="nsp" width="48" height="22"></canvas>
    );
  };

  // Main chart rendering for SCOM 5-day chart
  useEffect(() => {
    const canvas = document.getElementById('mchart') as HTMLCanvasElement;
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    // Set canvas dimensions explicitly
    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;

    // Generate sample 5-day SCOM data
    const data = [13.50, 13.65, 13.40, 13.75, 13.85];
    const width = canvas.width;
    const height = canvas.height;
    const padding = 20;

    // Clear canvas
    ctx.clearRect(0, 0, width, height);

    // Calculate min/max for scaling
    const min = Math.min(...data) - 0.2;
    const max = Math.max(...data) + 0.2;
    const range = max - min;

    // Draw grid lines
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.1)';
    ctx.lineWidth = 1;
    for (let i = 0; i <= 4; i++) {
      const y = padding + (height - 2 * padding) * (i / 4);
      ctx.beginPath();
      ctx.moveTo(padding, y);
      ctx.lineTo(width - padding, y);
      ctx.stroke();
    }

    // Draw line chart
    ctx.beginPath();
    ctx.strokeStyle = '#5DBE3C';
    ctx.lineWidth = 2;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';

    data.forEach((value, index) => {
      const x = padding + (width - 2 * padding) * (index / (data.length - 1));
      const y = height - padding - ((value - min) / range) * (height - 2 * padding);
      
      if (index === 0) {
        ctx.moveTo(x, y);
      } else {
        ctx.lineTo(x, y);
      }
    });

    ctx.stroke();

    // Draw gradient fill under the line
    const gradient = ctx.createLinearGradient(0, padding, 0, height - padding);
    gradient.addColorStop(0, 'rgba(93, 190, 60, 0.3)');
    gradient.addColorStop(1, 'rgba(93, 190, 60, 0.0)');

    ctx.lineTo(width - padding, height - padding);
    ctx.lineTo(padding, height - padding);
    ctx.closePath();
    ctx.fillStyle = gradient;
    ctx.fill();

    // Draw data points
    data.forEach((value, index) => {
      const x = padding + (width - 2 * padding) * (index / (data.length - 1));
      const y = height - padding - ((value - min) / range) * (height - 2 * padding);
      
      ctx.beginPath();
      ctx.arc(x, y, 4, 0, Math.PI * 2);
      ctx.fillStyle = '#5DBE3C';
      ctx.fill();
      ctx.strokeStyle = '#1C2340';
      ctx.lineWidth = 2;
      ctx.stroke();
    });

  }, []);

  return (
    <div className="screen on">
      <div className="sbar" style={{background: '#1C2340', position: 'relative', height: 'auto', padding: '14px 18px 16px'}}>
        <div style={{display: 'flex', alignItems: 'center', gap: '9px', marginBottom: '12px'}}>
          <button style={{background: 'rgba(255,255,255,.1)', border: 'none', borderRadius: '9px', width: '30px', height: '30px', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer'}}>
            <svg width="13" height="13" viewBox="0 0 13 13" fill="none">
              <path d="M8.5 2L4 6.5l4.5 4.5" stroke="white" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </button>
          <div style={{display: 'flex', alignItems: 'center', gap: '8px'}}>
            <img 
              src="/JIMUDU APP LOGO.png" 
              alt="Jimudu Wallet Logo" 
              style={{width: '38px', height: '38px', borderRadius: '8px'}}
            />
            <div>
              <div style={{fontSize: '13px', fontWeight: '500', letterSpacing: '.4px', lineHeight: '1.1'}}>
                <span style={{color: '#F47C20'}}>JIMUDU</span> <span style={{color: '#10B981'}}>WALLET</span>
              </div>
              <div style={{fontSize: '9px', color: 'rgba(255,255,255,.45)', letterSpacing: '.7px'}}>
                SAVE. BUILD & MANAGE WEALTH
              </div>
            </div>
          </div>
          <div style={{marginLeft: 'auto', background: 'rgba(93,190,60,.15)', border: '0.5px solid rgba(93,190,60,.3)', borderRadius: '10px', padding: '3px 9px'}}>
            <span style={{fontSize: '10px', color: '#5DBE3C', fontWeight: '500'}}>Live</span>
          </div>
        </div>
        <div style={{display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px'}}>
          <div style={{background: 'rgba(255,255,255,.07)', borderRadius: '10px', padding: '11px', border: '0.5px solid rgba(255,255,255,.09)'}}>
            <div style={{fontSize: '10px', color: 'rgba(255,255,255,.45)', marginBottom: '3px'}}>NSE 20 Index</div>
            <div style={{fontSize: '17px', fontWeight: '500', color: '#fff'}}>{nse20Value}</div>
            <div style={{fontSize: '10px', color: '#5DCAA5', marginTop: '2px'}}>+12.4 (+0.68%)</div>
          </div>
          <div style={{background: 'rgba(255,255,255,.07)', borderRadius: '10px', padding: '11px', border: '0.5px solid rgba(255,255,255,.09)'}}>
            <div style={{fontSize: '10px', color: 'rgba(255,255,255,.45)', marginBottom: '3px'}}>NASI</div>
            <div style={{fontSize: '17px', fontWeight: '500', color: '#fff'}}>109.2</div>
            <div style={{fontSize: '10px', color: '#5DCAA5', marginTop: '2px'}}>+0.9 (+0.83%)</div>
          </div>
        </div>
      </div>

      <div className="scr-body">
        <div style={{background: 'var(--dark2)', padding: '12px 18px 14px'}}>
          <div style={{fontSize: '10px', color: 'rgba(255,255,255,.4)', marginBottom: '8px'}}>SCOM &mdash; Safaricom PLC · 5-day</div>
          <div style={{position: 'relative', height: '100px'}}>
            <canvas id="mchart" role="img" aria-label="Safaricom 5-day price chart" style={{width: '100%', height: '100%'}}></canvas>
          </div>
        </div>

        <div className="sec" style={{paddingTop: '12px'}}>
          <div className="sch">
            <span className="sct">NSE equities</span>
            <span style={{fontSize: '10px', color: 'var(--t3)'}}>Tap to trade</span>
          </div>
          <div className="card" id="nse-rows">
            {nseData.map((stock) => (
              <div key={stock.sym} className="nse-row">
                <div className="nsym">{stock.sym}</div>
                <div className="nname">{stock.name}</div>
                <SparklineChart symbol={stock.sym} isUp={stock.up} />
                <div className="npr">KES {stock.price.toFixed(2)}</div>
                <div className={`nch ${stock.up ? 'pos' : 'neg'}`}>
                  {stock.up ? '+' : ''}{stock.pct.toFixed(2)}%
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="sec">
          <div className="sch">
            <span className="sct">MMF growth bucket</span>
            <span style={{background: '#EBF7E6', color: '#1E5C0E', fontSize: '10px', fontWeight: '500', padding: '3px 9px', borderRadius: '10px'}}>
              1.2%/mo
            </span>
          </div>
          <div className="card" style={{padding: '14px'}}>
            <div className="mg">
              <div style={{background: '#EBF7E6', borderRadius: '10px', padding: '10px', textAlign: 'center'}}>
                <div style={{fontSize: '16px', fontWeight: '500', color: '#1E5C0E'}}>KES 12,400</div>
                <div style={{fontSize: '10px', color: '#3E8E3E', marginTop: '2px'}}>Invested</div>
              </div>
              <div style={{background: '#EBF7E6', borderRadius: '10px', padding: '10px', textAlign: 'center'}}>
                <div style={{fontSize: '16px', fontWeight: '500', color: '#1E5C0E'}}>+KES 890</div>
                <div style={{fontSize: '10px', color: '#3E8E3E', marginTop: '2px'}}>Total yield</div>
              </div>
            </div>
            <button className="btn-p" style={{fontSize: '13px', padding: '11px'}}>
              Invest more into MMF
            </button>
          </div>

          <div className="card" style={{padding: '14px'}}>
            <div style={{fontSize: '13px', fontWeight: '500', color: 'var(--t1)', marginBottom: '10px'}}>
              Wealth Building Progress
            </div>
            <div style={{display: 'flex', flexDirection: 'column', gap: '8px'}}>
              <div>
                <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '3px'}}>
                  <span style={{fontSize: '11px', color: 'var(--t2)'}}>Emergency Fund Goal</span>
                  <span style={{fontSize: '11px', color: 'var(--gr2)'}}>73%</span>
                </div>
                <div className="pb">
                  <div className="pf" style={{width: '73%', background: 'var(--gr2)'}}></div>
                </div>
              </div>
              <div>
                <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '3px'}}>
                  <span style={{fontSize: '11px', color: 'var(--t2)'}}>MMF Growth Target</span>
                  <span style={{fontSize: '11px', color: 'var(--or)'}}>42%</span>
                </div>
                <div className="pb">
                  <div className="pf" style={{width: '42%', background: 'var(--or)'}}></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

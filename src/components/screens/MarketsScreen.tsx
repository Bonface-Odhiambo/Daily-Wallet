import { useState, useEffect } from 'react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { TrendingUp, TrendingDown } from 'lucide-react';
import { motion } from 'framer-motion';

interface MarketsScreenProps {
  setActiveScreen: (screen: string) => void;
}

export default function MarketsScreen({ setActiveScreen }: MarketsScreenProps) {
  const [currentTime, setCurrentTime] = useState('9:41');
  const [activeRange, setActiveRange] = useState('5D');
  const [nseData, setNseData] = useState([
    {sym: 'SCOM', name: 'Safaricom PLC', price: 13.85, chg: +0.15, pct: +1.10, up: true},
    {sym: 'EQTY', name: 'Equity Group', price: 38.20, chg: -0.30, pct: -0.78, up: false},
    {sym: 'KCB', name: 'KCB Group', price: 22.50, chg: +0.50, pct: +2.27, up: true},
    {sym: 'COOP', name: 'Co-op Bank', price: 11.90, chg: +0.10, pct: +0.85, up: true},
    {sym: 'ABSA', name: 'Absa Bank Kenya', price: 12.40, chg: -0.20, pct: -1.59, up: false},
    {sym: 'EABL', name: 'E.A. Breweries', price: 148.00, chg: +2.00, pct: +1.37, up: true},
  ]);
  const [nse20Value, setNse20Value] = useState('1847.3');
  
  // Sample chart data for SCOM
  const [chartData, setChartData] = useState([
    { time: 'Mon', price: 13.50 },
    { time: 'Tue', price: 13.65 },
    { time: 'Wed', price: 13.40 },
    { time: 'Thu', price: 13.75 },
    { time: 'Fri', price: 13.85 },
  ]);

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

  // NSE Stock Chart Component with Recharts
  const NSEStockChart = ({ data, symbol, price, change }: { data: any[], symbol: string, price: number, change: number }) => {
    const isPositive = change >= 0;
    const color = isPositive ? '#22c55e' : '#ef4444';

    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        style={{background: '#1C2340', borderRadius: '16px', padding: '16px', color: 'white'}}
      >
        {/* Header */}
        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '16px'}}>
          <div>
            <h3 style={{fontSize: '24px', fontWeight: 'bold', margin: 0}}>{symbol}</h3>
            <p style={{color: 'rgba(255,255,255,0.6)', fontSize: '12px', margin: '4px 0 0'}}>Nairobi Securities Exchange</p>
          </div>
          <div style={{textAlign: 'right'}}>
            <p style={{fontSize: '24px', fontWeight: 'bold', margin: 0}}>KES {price.toFixed(2)}</p>
            <div style={{display: 'flex', alignItems: 'center', gap: '4px', justifyContent: 'flex-end', color: isPositive ? '#22c55e' : '#ef4444'}}>
              {isPositive ? <TrendingUp size={16}/> : <TrendingDown size={16}/>}
              <span>{change >= 0 ? '+' : ''}{change.toFixed(2)}%</span>
            </div>
          </div>
        </div>

        {/* Chart */}
        <ResponsiveContainer width="100%" height={180}>
          <AreaChart data={data}>
            <defs>
              <linearGradient id="colorGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor={color} stopOpacity={0.3}/>
                <stop offset="95%" stopColor={color} stopOpacity={0}/>
              </linearGradient>
            </defs>
            <XAxis dataKey="time" hide />
            <YAxis domain={['auto', 'auto']} hide />
            <Tooltip
              contentStyle={{ background: '#1f2937', border: 'none', borderRadius: '8px' }}
              labelStyle={{ color: '#9ca3af' }}
              formatter={(value) => [`KES ${value}`, 'Price']}
            />
            <Area
              type="monotone"
              dataKey="price"
              stroke={color}
              strokeWidth={2.5}
              fill="url(#colorGradient)"
              dot={false}
              activeDot={{ r: 6, fill: color }}
            />
          </AreaChart>
        </ResponsiveContainer>

        {/* Time range selector */}
        <div style={{display: 'flex', gap: '8px', marginTop: '12px', justifyContent: 'center'}}>
          {['1D', '5D', '1M', '3M', '1Y'].map(range => (
            <button
              key={range}
              style={{
                padding: '4px 12px',
                borderRadius: '20px',
                fontSize: '12px',
                fontWeight: '500',
                transition: 'all 0.2s',
                background: activeRange === range ? '#F47C20' : '#374151',
                color: activeRange === range ? 'white' : '#9ca3af',
                border: 'none',
                cursor: 'pointer'
              }}
              onClick={() => setActiveRange(range)}
            >
              {range}
            </button>
          ))}
        </div>
      </motion.div>
    );
  };

  // Financial Discipline Insight Component
  const DisciplineInsight = ({ spendingData, stockPrice }: { spendingData: { weeklyBudget: number, weeklySpent: number }, stockPrice: number }) => {
    const savedAmount = spendingData.weeklyBudget - spendingData.weeklySpent;
    const sharesCanBuy = Math.floor(savedAmount / stockPrice);

    return (
      <div style={{
        background: 'linear-gradient(to right, #F47C20, #10B981)',
        borderRadius: '16px',
        padding: '16px',
        color: 'white',
        marginTop: '16px'
      }}>
        <p style={{fontSize: '14px', fontWeight: '500', opacity: 0.9, margin: '0 0 8px 0'}}>💡 Discipline Insight</p>
        <p style={{fontSize: '18px', fontWeight: 'bold', margin: '0 0 4px 0'}}>
          You saved KES {savedAmount} this week
        </p>
        <p style={{fontSize: '14px', opacity: 0.9, margin: '0 0 12px 0'}}>
          That's enough to buy {sharesCanBuy} shares of Safaricom at current price.
          <span style={{fontWeight: 'bold'}}> Invest now?</span>
        </p>
        <button style={{
          marginTop: '12px',
          background: 'white',
          color: '#F47C20',
          fontWeight: 'bold',
          padding: '12px 16px',
          borderRadius: '12px',
          fontSize: '14px',
          width: '100%',
          border: 'none',
          cursor: 'pointer'
        }}>
          Move savings → MMF Growth Bucket
        </button>
      </div>
    );
  };

  // Engagement Hooks Components
  const StreakCounter = () => (
    <div style={{display: 'flex', alignItems: 'center', gap: '8px', background: '#FEF3EA', borderRadius: '12px', padding: '12px', marginBottom: '8px'}}>
      <span style={{fontSize: '24px'}}>🔥</span>
      <div>
        <p style={{fontWeight: 'bold', color: '#F47C20', margin: '0 0 2px 0'}}>12-day discipline streak!</p>
        <p style={{fontSize: '12px', color: '#6B7280', margin: 0}}>Stay under budget to keep it going</p>
      </div>
    </div>
  );

  const PriceAlert = () => (
    <div style={{background: '#EBF7E6', borderRadius: '12px', padding: '12px', marginBottom: '8px', borderLeft: '4px solid #10B981'}}>
      <p style={{fontWeight: 'bold', color: '#10B981', margin: '0 0 4px 0'}}>📈 SCOM hit your target price!</p>
      <p style={{fontSize: '12px', color: '#6B7280', margin: '0 0 4px 0'}}>KES 14.00 — Your alert triggered</p>
      <button style={{color: '#10B981', fontWeight: 'bold', fontSize: '14px', marginTop: '4px', background: 'none', border: 'none', cursor: 'pointer', padding: 0}}>Buy now →</button>
    </div>
  );

  const MicroInvestment = () => (
    <div style={{background: '#EFF6FF', borderRadius: '12px', padding: '12px', marginBottom: '8px'}}>
      <p style={{fontSize: '14px', fontWeight: 'bold', margin: '0 0 4px 0'}}>Round-up opportunity</p>
      <p style={{fontSize: '12px', color: '#6B7280', margin: 0}}>
        Your last M-Pesa was KES 847. Round up KES 153 → MMF?
      </p>
    </div>
  );

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
            <motion.span
              animate={{ opacity: [1, 0.5, 1] }}
              transition={{ repeat: Infinity, duration: 2 }}
              style={{fontSize: '10px', color: '#5DBE3C', fontWeight: '500'}}
            >
              ● LIVE
            </motion.span>
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
        <div style={{padding: '12px 18px 14px'}}>
          <NSEStockChart 
            data={chartData} 
            symbol="SCOM" 
            price={nseData[0].price} 
            change={nseData[0].pct} 
          />
        </div>

        <div style={{padding: '0 18px 14px'}}>
          <DisciplineInsight 
            spendingData={{ weeklyBudget: 5000, weeklySpent: 3200 }}
            stockPrice={nseData[0].price}
          />
        </div>

        <div style={{padding: '0 18px 14px'}}>
          <StreakCounter />
          <PriceAlert />
          <MicroInvestment />
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

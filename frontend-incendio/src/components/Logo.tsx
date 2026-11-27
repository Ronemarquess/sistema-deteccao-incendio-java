import { useState } from 'react';
import { Flame } from 'lucide-react';

interface LogoProps {
  variante?: 'grande' | 'pequena';
}

export function Logo({ variante = 'grande' }: LogoProps) {
  const [falhou, setFalhou] = useState(false);
  const grande = variante === 'grande';

  if (falhou) {
    return (
      <div className="flex items-center gap-3 text-white">
        <span className="flex size-11 items-center justify-center rounded-2xl bg-acao">
          <Flame size={24} aria-hidden />
        </span>
        <div>
          <p className="text-2xl font-black tracking-widest">SAFE-ALERT</p>
          {grande && <p className="text-sm text-suave">Detecção precoce de incêndios</p>}
        </div>
      </div>
    );
  }

  return (
    <img
      src="/safe-alert-logo.png"
      alt="SAFE-ALERT — detecção precoce de incêndios"
      className={grande ? 'w-full max-w-sm mx-auto' : 'h-10 w-auto'}
      onError={() => setFalhou(true)}
    />
  );
}

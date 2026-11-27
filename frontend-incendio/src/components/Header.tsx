import { LogOut } from 'lucide-react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Logo } from './Logo';

const abas = [
  { to: '/chamados', rotulo: 'Chamados' },
  { to: '/sensores', rotulo: 'Sensores' },
  { to: '/simulador', rotulo: 'Simulador' },
];

function classesAba({ isActive }: { isActive: boolean }) {
  return [
    'inline-flex min-h-11 items-center justify-center px-4 py-2 rounded-full font-semibold transition-colors',
    isActive ? 'bg-acao text-white font-bold' : 'text-borda hover:bg-linha',
  ].join(' ');
}

export function Header() {
  const { bombeiro, sair } = useAuth();
  const navigate = useNavigate();

  function handleSair() {
    sair();
    navigate('/login', { replace: true });
  }

  return (
    <>
      <header className="sticky top-0 z-30 border-b border-linha bg-painel/95 backdrop-blur">
        <div className="mx-auto flex max-w-7xl flex-wrap items-center gap-3 px-4 py-3 md:flex-nowrap md:px-6">
          <NavLink to="/chamados" className="flex min-h-11 items-center text-white" aria-label="SAFE-ALERT — início">
            <Logo variante="pequena" />
          </NavLink>

          <nav aria-label="Navegação principal" className="order-3 w-full md:order-none md:w-auto md:flex-1">
            <ul className="flex flex-wrap gap-2 md:justify-center">
              {abas.map((aba) => (
                <li key={aba.to}>
                  <NavLink to={aba.to} className={classesAba}>
                    {aba.rotulo}
                  </NavLink>
                </li>
              ))}
            </ul>
          </nav>

          <div className="ml-auto flex items-center gap-3">
            <div className="text-right text-sm leading-tight">
              <p className="font-semibold text-white">{bombeiro?.nome ?? 'Bombeiro'}</p>
              <p className="text-xs text-suave">Matrícula {bombeiro?.matricula ?? '—'}</p>
            </div>
            <button
              type="button"
              onClick={handleSair}
              className="flex min-h-11 min-w-11 items-center justify-center gap-2 rounded-full border border-borda px-4 text-borda hover:bg-linha"
            >
              <LogOut size={18} aria-hidden />
              <span className="hidden sm:inline">Sair</span>
            </button>
          </div>
        </div>
      </header>

      <nav
        aria-label="Navegação principal (celular)"
        className="fixed bottom-0 left-0 right-0 z-30 border-t border-linha bg-painel md:hidden"
      >
        <ul className="grid grid-cols-3">
          {abas.map((aba) => (
            <li key={aba.to}>
              <NavLink to={aba.to} className={({ isActive }) => `${classesAba({ isActive })} block rounded-none text-center`}>
                {aba.rotulo}
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>
    </>
  );
}

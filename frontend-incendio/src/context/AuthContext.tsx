import { createContext, useContext, useMemo, useState, type ReactNode } from 'react';
import { api } from '../services/api';
import type { Bombeiro } from '../types';

const CHAVE_SESSAO = 'safealert.bombeiro';

interface AuthContextValue {
  bombeiro: Bombeiro | null;
  entrar: (email: string, senha: string) => Promise<void>;
  sair: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function lerSessao(): Bombeiro | null {
  try {
    const bruto = sessionStorage.getItem(CHAVE_SESSAO);
    return bruto ? (JSON.parse(bruto) as Bombeiro) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [bombeiro, setBombeiro] = useState<Bombeiro | null>(lerSessao);

  const value = useMemo<AuthContextValue>(() => ({
    bombeiro,
    entrar: async (email, senha) => {
      const { data } = await api.post<Bombeiro>('/bombeiros/login', { email, senha });
      sessionStorage.setItem(CHAVE_SESSAO, JSON.stringify(data));
      setBombeiro(data);
    },
    sair: () => {
      sessionStorage.removeItem(CHAVE_SESSAO);
      setBombeiro(null);
    },
  }), [bombeiro]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const contexto = useContext(AuthContext);
  if (!contexto) throw new Error('useAuth deve ser usado dentro de AuthProvider');
  return contexto;
}

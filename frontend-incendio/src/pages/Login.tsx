import { motion } from 'framer-motion';
import { ArrowRight, LockKeyhole, Mail } from 'lucide-react';
import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { Logo } from '../components/Logo';
import { useAuth } from '../context/AuthContext';
import { mensagemErro } from '../services/api';
import { unlockAudio } from '../utils/alarm';

export function Login() {
  const { entrar, bombeiro } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [concordo, setConcordo] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  async function handleSubmit(evento: FormEvent) {
    evento.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      await entrar(email, senha);
      unlockAudio();
      navigate('/chamados', { replace: true });
    } catch (err) {
      setErro(mensagemErro(err));
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-10 bg-fundo px-4 py-10 md:flex-row md:gap-20 md:px-12">
      <div className="w-full max-w-sm text-center md:text-left">
        <Logo variante="grande" />
      </div>

      <motion.section
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-md rounded-3xl border border-linha bg-painel p-7 shadow-2xl md:p-9"
        aria-labelledby="titulo-login"
      >
        <h1 id="titulo-login" className="text-center text-3xl font-black tracking-widest text-white">
          LOGIN
        </h1>
        <p className="mt-2 text-center text-sm text-suave">
          Acesso restrito às equipes do corpo de bombeiros.
        </p>

        <form onSubmit={handleSubmit} className="mt-8 space-y-5">
          <div>
            <label htmlFor="email" className="mb-2 block text-sm font-bold tracking-wide text-borda">
              EMAIL
            </label>
            <div className="flex items-center gap-2 rounded-xl border border-borda bg-white px-3">
              <Mail size={18} className="text-fundo" aria-hidden />
              <input
                id="email"
                type="email"
                autoComplete="username"
                required
                value={email}
                onChange={(evento) => setEmail(evento.target.value)}
                placeholder="nome@bombeiros.se.gov.br"
                className="min-h-12 w-full bg-transparent text-[15px] text-fundo outline-none placeholder:text-slate-500"
              />
            </div>
          </div>

          <div>
            <label htmlFor="senha" className="mb-2 block text-sm font-bold tracking-wide text-borda">
              SENHA
            </label>
            <div className="flex items-center gap-2 rounded-xl border border-borda bg-white px-3">
              <LockKeyhole size={18} className="text-fundo" aria-hidden />
              <input
                id="senha"
                type="password"
                autoComplete="current-password"
                required
                value={senha}
                onChange={(evento) => setSenha(evento.target.value)}
                placeholder="••••••"
                className="min-h-12 w-full bg-transparent text-[15px] text-fundo outline-none placeholder:text-slate-500"
              />
            </div>
          </div>

          <label className="flex cursor-pointer items-start gap-3 text-sm text-white">
            <input
              type="checkbox"
              checked={concordo}
              onChange={(evento) => setConcordo(evento.target.checked)}
              className="mt-0.5 size-5 accent-[#ed5533]"
            />
            <span>Li e concordo com a política de privacidade.</span>
          </label>

          {erro && (
            <p role="alert" className="rounded-xl bg-acao/20 p-3 text-sm font-semibold text-white">
              {erro}
            </p>
          )}

          <button
            type="submit"
            disabled={!concordo || enviando}
            className="flex min-h-12 w-full items-center justify-center gap-2 rounded-full bg-acao px-6 text-base font-bold text-white transition-opacity hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-40"
          >
            {enviando ? 'AVANÇANDO…' : 'AVANÇAR'}
            <ArrowRight size={18} aria-hidden />
          </button>
        </form>

        {bombeiro && (
          <p className="mt-4 text-center text-xs text-suave">Sessão ativa: {bombeiro.nome}</p>
        )}
      </motion.section>
    </div>
  );
}

import { AnimatePresence, motion } from 'framer-motion';
import { BellRing, Search } from 'lucide-react';
import { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ResumoCard } from '../components/ResumoCard';
import { StatusChip } from '../components/StatusChip';
import { usePolling } from '../hooks/usePolling';
import { api } from '../services/api';
import type { ChamadoItem, ChamadoResumo, StatusChamado } from '../types';
import { formatarTemperatura } from '../utils/format';
import { playAlarm } from '../utils/alarm';

const filtros: { rotulo: string; valor: StatusChamado | 'TODOS' }[] = [
  { rotulo: 'Todos', valor: 'TODOS' },
  { rotulo: 'Novo', valor: 'NOVO' },
  { rotulo: 'Em atendimento', valor: 'EM_ATENDIMENTO' },
  { rotulo: 'Encerrado', valor: 'ENCERRADO' },
  { rotulo: 'Falso alarme', valor: 'FALSO_ALARME' },
];

export function Chamados() {
  const navigate = useNavigate();
  const [filtro, setFiltro] = useState<StatusChamado | 'TODOS'>('TODOS');
  const [busca, setBusca] = useState('');
  const [toast, setToast] = useState<string | null>(null);
  const conhecidos = useRef<Set<number> | null>(null);

  const resumo = usePolling<ChamadoResumo>(async () => (await api.get('/chamados/resumo')).data);
  const lista = usePolling<ChamadoItem[]>(async () => (await api.get('/chamados')).data);

  useEffect(() => {
    if (!lista.data) return;
    const atuais = new Set(lista.data.map((chamado) => chamado.id));
    const anteriores = conhecidos.current;
    conhecidos.current = atuais;
    if (anteriores === null) return;

    const ineditos = lista.data.filter(
      (chamado) => chamado.status === 'NOVO' && !anteriores.has(chamado.id),
    );
    if (ineditos.length > 0) {
      setToast(`Novo chamado: ${ineditos.map((chamado) => chamado.numero).join(', ')}`);
      playAlarm();
    }
  }, [lista.data]);

  useEffect(() => {
    if (!toast) return;
    const id = window.setTimeout(() => setToast(null), 9000);
    return () => window.clearTimeout(id);
  }, [toast]);

  const chamados = useMemo(() => lista.data ?? [], [lista.data]);
  const filtrados = chamados.filter((chamado) => {
    const passaStatus = filtro === 'TODOS' || chamado.status === filtro;
    const passaBusca = busca.trim() === ''
      || chamado.local.toLowerCase().includes(busca.trim().toLowerCase());
    return passaStatus && passaBusca;
  });

  const erro = lista.error ?? resumo.error;
  const carregando = lista.loading && lista.data === null;

  return (
    <section aria-labelledby="titulo-chamados">
      <div className="mb-6">
        <h1 id="titulo-chamados" className="text-2xl font-black md:text-3xl">Painel de chamados</h1>
        <p className="mt-1 text-sm text-suave">Atualização automática a cada 5 segundos.</p>
      </div>

      {erro && (
        <div role="alert" className="mb-6 rounded-xl border border-acao bg-acao/15 p-4 text-sm font-semibold">
          {erro}
        </div>
      )}

      <div className="grid gap-4 sm:grid-cols-3">
        <ResumoCard titulo="Em aberto" valor={resumo.data?.emAberto ?? 0} detalhe="Chamados NOVO" tom="acao" />
        <ResumoCard titulo="Em atendimento" valor={resumo.data?.emAtendimento ?? 0} detalhe="Equipes despachadas" tom="borda" />
        <ResumoCard titulo="Encerrados hoje" valor={resumo.data?.encerradosHoje ?? 0} detalhe="Ocorrências finalizadas" tom="claro" />
      </div>

      <div className="mt-8 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div className="flex flex-wrap gap-2" role="group" aria-label="Filtrar por status">
          {filtros.map((opcao) => (
            <button
              key={opcao.valor}
              type="button"
              onClick={() => setFiltro(opcao.valor)}
              aria-pressed={filtro === opcao.valor}
              className={`min-h-11 rounded-full px-4 text-sm font-semibold ${
                filtro === opcao.valor
                  ? 'bg-acao text-white font-bold'
                  : 'border border-linha text-suave hover:bg-linha'
              }`}
            >
              {opcao.rotulo}
            </button>
          ))}
        </div>

        <label className="flex min-h-12 w-full items-center gap-2 rounded-xl border border-borda bg-painel px-3 md:w-72">
          <Search size={18} className="text-borda" aria-hidden />
          <span className="sr-only">Buscar por local</span>
          <input
            type="search"
            value={busca}
            onChange={(evento) => setBusca(evento.target.value)}
            placeholder="Buscar local"
            className="w-full bg-transparent text-sm text-white outline-none placeholder:text-suave"
          />
        </label>
      </div>

      <div className="mt-6 overflow-x-hidden rounded-2xl border border-linha bg-painel">
        {carregando ? (
          <p className="p-8 text-center text-suave">Carregando chamados…</p>
        ) : filtrados.length === 0 ? (
          <p className="p-8 text-center text-suave">Nenhum chamado encontrado para os filtros atuais.</p>
        ) : (
          <>
            <div className="hidden overflow-x-auto md:block">
              <table className="w-full text-left text-sm">
                <thead className="bg-fundo text-xs uppercase tracking-wide text-borda">
                  <tr>
                    <th scope="col" className="px-4 py-3">Nº</th>
                    <th scope="col" className="px-4 py-3">Local</th>
                    <th scope="col" className="px-4 py-3">Sensores</th>
                    <th scope="col" className="px-4 py-3">Temp. máx.</th>
                    <th scope="col" className="px-4 py-3">Status</th>
                    <th scope="col" className="px-4 py-3 text-right">Ação</th>
                  </tr>
                </thead>
                <tbody>
                  {filtrados.map((chamado) => (
                    <tr
                      key={chamado.id}
                      className={`border-t border-linha ${chamado.status === 'NOVO' ? 'borda-pulsante' : ''}`}
                    >
                      <td className="px-4 py-3 font-bold">{chamado.numero}</td>
                      <td className="px-4 py-3">{chamado.local}</td>
                      <td className="px-4 py-3">{chamado.quantidadeSensores}</td>
                      <td className="px-4 py-3">{formatarTemperatura(chamado.temperaturaMaxima)}</td>
                      <td className="px-4 py-3"><StatusChip status={chamado.status} /></td>
                      <td className="px-4 py-3 text-right">
                        <button
                          type="button"
                          onClick={() => navigate(`/chamados/${chamado.id}`)}
                          className="min-h-11 rounded-full border border-borda px-4 text-sm font-semibold text-borda hover:bg-linha"
                        >
                          Abrir
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <ul className="divide-y divide-linha md:hidden">
              {filtrados.map((chamado) => (
                <li key={chamado.id} className={`p-4 ${chamado.status === 'NOVO' ? 'borda-pulsante' : ''}`}>
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <p className="font-bold">{chamado.numero} · {chamado.local}</p>
                      <p className="mt-1 text-xs text-suave">
                        {chamado.quantidadeSensores} sensores · {formatarTemperatura(chamado.temperaturaMaxima)}
                      </p>
                    </div>
                    <StatusChip status={chamado.status} />
                  </div>
                  <button
                    type="button"
                    onClick={() => navigate(`/chamados/${chamado.id}`)}
                    className="mt-3 min-h-11 w-full rounded-full bg-acao font-bold text-white"
                  >
                    Abrir chamado
                  </button>
                </li>
              ))}
            </ul>
          </>
        )}
      </div>

      <AnimatePresence>
        {toast && (
          <motion.div
            role="status"
            aria-live="polite"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 20 }}
            className="fixed bottom-24 left-4 right-4 z-40 mx-auto flex max-w-md items-center gap-3 rounded-2xl border-2 border-acao bg-painel p-4 shadow-2xl md:bottom-8"
          >
            <BellRing className="shrink-0 text-acao" aria-hidden />
            <span className="text-sm font-semibold">{toast}</span>
          </motion.div>
        )}
      </AnimatePresence>
    </section>
  );
}

import { ArrowLeft, ExternalLink, Navigation, RadioTower } from 'lucide-react';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Mapa } from '../components/Mapa';
import { StatusChip } from '../components/StatusChip';
import { usePolling } from '../hooks/usePolling';
import { api, mensagemErro } from '../services/api';
import type { Bombeiro, ChamadoDetalhe as Detalhe } from '../types';
import { formatarData, formatarTemperatura, rotuloFumaca } from '../utils/format';

export function ChamadoDetalhe() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [erroAcao, setErroAcao] = useState<string | null>(null);
  const [bombeiroId, setBombeiroId] = useState<number | null>(null);

  const detalhe = usePolling<Detalhe>(async () => (await api.get(`/chamados/${id}`)).data);
  const bombeiros = usePolling<Bombeiro[]>(async () => (await api.get('/bombeiros')).data, 30000);

  if (detalhe.loading && !detalhe.data) {
    return <p className="p-8 text-center text-suave">Carregando chamado…</p>;
  }
  if (detalhe.error && !detalhe.data) {
    return <div role="alert" className="rounded-xl border border-acao bg-acao/15 p-4">{detalhe.error}</div>;
  }
  const chamado = detalhe.data;
  if (!chamado) return null;
  const chamadoId = chamado.id;

  const selecionado = bombeiroId ?? bombeiros.data?.[0]?.id ?? null;
  const rota = chamado.latitude != null && chamado.longitude != null
    ? `https://www.google.com/maps/dir/?api=1&destination=${chamado.latitude},${chamado.longitude}`
    : null;

  async function acao(caminho: string, corpo?: object) {
    setErroAcao(null);
    try {
      await api.post(`/chamados/${chamadoId}/${caminho}`, corpo);
      await detalhe.refetch();
    } catch (err) {
      setErroAcao(mensagemErro(err));
    }
  }

  function despachar() {
    if (selecionado == null) {
      setErroAcao('Cadastre um bombeiro antes de despachar.');
      return;
    }
    void acao('despachar', { bombeiroId: selecionado });
  }

  function falsoAlarme() {
    if (window.confirm('Marcar este chamado como falso alarme?')) void acao('falso-alarme');
  }

  const podeDespachar = chamado.status === 'NOVO';
  const podeFalsoAlarme = chamado.status === 'NOVO' || chamado.status === 'EM_ATENDIMENTO';
  const podeEncerrar = chamado.status === 'EM_ATENDIMENTO';
  const motivo = (permitido: boolean, esperado: string) =>
    permitido ? undefined : `Somente chamados ${esperado} permitem esta ação; status atual: ${chamado.status}`;

  return (
    <section aria-labelledby="titulo-detalhe" className="pb-4">
      <div className="mb-5 flex flex-wrap items-center gap-3">
        <button
          type="button"
          onClick={() => navigate('/chamados')}
          className="flex min-h-11 items-center gap-2 rounded-full border border-borda px-4 text-sm text-borda hover:bg-linha"
        >
          <ArrowLeft size={18} aria-hidden /> Voltar
        </button>
        <h1 id="titulo-detalhe" className="text-2xl font-black">Chamado {chamado.numero}</h1>
        <StatusChip status={chamado.status} />
        <p className="w-full text-sm text-suave">Recebido: {formatarData(chamado.horarioExato)}</p>
      </div>

      {(detalhe.error || erroAcao) && (
        <p role="alert" className="mb-5 rounded-xl border border-acao bg-acao/15 p-3 text-sm">
          {erroAcao ?? detalhe.error}
        </p>
      )}

      <div className="grid gap-6 lg:grid-cols-2">
        <div className="rounded-2xl border border-linha bg-painel p-4">
          <Mapa latitude={chamado.latitude} longitude={chamado.longitude} local={chamado.local} />
          {rota && (
            <a
              href={rota}
              target="_blank"
              rel="noreferrer"
              className="mt-4 flex min-h-11 items-center justify-center gap-2 rounded-full bg-borda px-4 text-sm font-bold text-painel"
            >
              <Navigation size={17} aria-hidden /> Abrir rota
            </a>
          )}
        </div>

        <div className="rounded-2xl border border-linha bg-painel p-5">
          <h2 className="mb-4 flex items-center gap-2 text-lg font-bold">
            <RadioTower size={20} className="text-acao" aria-hidden /> DADOS DO ALERTA
          </h2>
          <dl className="space-y-3 text-sm">
            <div><dt className="text-suave">Local</dt><dd className="font-semibold">{chamado.local}</dd></div>
            <div>
              <dt className="text-suave">Coordenadas GPS</dt>
              <dd className="font-semibold">
                {chamado.latitude != null && chamado.longitude != null
                  ? `${chamado.latitude.toFixed(4)}, ${chamado.longitude.toFixed(4)}`
                  : '—'}
              </dd>
            </div>
            <div><dt className="text-suave">Horário exato</dt><dd className="font-semibold">{formatarData(chamado.horarioExato)}</dd></div>
            <div>
              <dt className="text-suave">Elevação de temperatura</dt>
              <dd className="font-semibold">
                +{formatarTemperatura(chamado.elevacaoTemperatura)} ({formatarTemperatura(chamado.temperaturaMaxima)})
              </dd>
            </div>
            <div><dt className="text-suave">Sensores ativos</dt><dd className="font-semibold">{chamado.sensores.filter((s) => s.ativo).length} de {chamado.sensores.length}</dd></div>
            <div><dt className="text-suave">Bombeiro responsável</dt><dd className="font-semibold">{chamado.bombeiroResponsavel ?? 'Não designado'}</dd></div>
          </dl>
        </div>
      </div>

      <div className="mt-6 overflow-hidden rounded-2xl border border-linha bg-painel">
        <h2 className="border-b border-linha bg-fundo px-4 py-3 text-sm font-bold uppercase tracking-wide text-borda">
          Sensores deste chamado
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full min-w-[560px] text-left text-sm">
            <thead className="text-xs uppercase text-suave">
              <tr>
                <th scope="col" className="px-4 py-3">ID</th>
                <th scope="col" className="px-4 py-3">Localização</th>
                <th scope="col" className="px-4 py-3">Temperatura</th>
                <th scope="col" className="px-4 py-3">Ativo</th>
                <th scope="col" className="px-4 py-3">MQ-2 (fumaça)</th>
              </tr>
            </thead>
            <tbody>
              {chamado.sensores.map((sensor) => (
                <tr key={sensor.id} className="border-t border-linha">
                  <td className="px-4 py-3">#{sensor.id}</td>
                  <td className="px-4 py-3">{sensor.localizacao}</td>
                  <td className="px-4 py-3">{formatarTemperatura(sensor.temperatura)}</td>
                  <td className="px-4 py-3">{sensor.ativo ? 'Sim' : 'Não'}</td>
                  <td className="px-4 py-3">{rotuloFumaca(sensor.nivelFumacaRotulo)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="mt-6 rounded-2xl border border-linha bg-painel p-5">
        <h2 className="mb-3 text-sm font-bold uppercase tracking-wide text-borda">Notificações enviadas</h2>
        {chamado.notificacoes.length === 0 ? (
          <p className="text-sm text-suave">Nenhuma notificação registrada.</p>
        ) : (
          <ul className="space-y-2 text-sm">
            {chamado.notificacoes.map((notificacao) => (
              <li key={notificacao.id} className="rounded-xl bg-fundo p-3">
                <span className="font-bold text-acao">{notificacao.canal}</span> · {formatarData(notificacao.enviadaEm)}
                <p>{notificacao.mensagem}</p>
                <p className="text-xs text-suave">Destinatário: {notificacao.destinatario}</p>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="mt-6 hidden flex-wrap gap-3 md:flex">
        <label className="flex min-h-12 items-center gap-2 rounded-full border border-borda px-4 text-sm">
          <span>Responsável:</span>
          <select
            value={selecionado ?? ''}
            onChange={(evento) => setBombeiroId(Number(evento.target.value))}
            className="bg-painel text-sm text-white outline-none"
          >
            {bombeiros.data?.map((bombeiro) => (
              <option key={bombeiro.id} value={bombeiro.id}>{bombeiro.nome}</option>
            ))}
          </select>
        </label>
        <button type="button" onClick={despachar} disabled={!podeDespachar} title={motivo(podeDespachar, 'NOVO')}
          className="min-h-12 rounded-full bg-acao px-5 font-bold text-white disabled:cursor-not-allowed disabled:opacity-40">
          Despachar equipe
        </button>
        <button type="button" onClick={() => void acao('em-atendimento', { bombeiroId: selecionado })}
          disabled={!podeDespachar} title={motivo(podeDespachar, 'NOVO')}
          className="min-h-12 rounded-full border border-borda px-5 font-semibold text-borda disabled:cursor-not-allowed disabled:opacity-40">
          Marcar em atend.
        </button>
        <button type="button" onClick={falsoAlarme} disabled={!podeFalsoAlarme}
          title={motivo(podeFalsoAlarme, 'NOVO ou EM_ATENDIMENTO')}
          className="min-h-12 rounded-full border border-linha px-5 font-semibold text-suave disabled:cursor-not-allowed disabled:opacity-40">
          Falso alarme
        </button>
        <button type="button" onClick={() => void acao('encerrar')} disabled={!podeEncerrar}
          title={motivo(podeEncerrar, 'EM_ATENDIMENTO')}
          className="min-h-12 rounded-full border border-borda px-5 font-semibold text-borda disabled:cursor-not-allowed disabled:opacity-40">
          Encerrar
        </button>
      </div>

      <div className="fixed bottom-16 left-0 right-0 z-20 flex gap-2 overflow-x-auto border-t border-linha bg-painel p-3 md:hidden">
        {rota && (
          <a href={rota} target="_blank" rel="noreferrer"
            className="flex min-h-12 shrink-0 items-center gap-2 rounded-full bg-acao px-4 font-bold text-white">
            <ExternalLink size={17} aria-hidden /> Iniciar rota
          </a>
        )}
        <button type="button" onClick={despachar} disabled={!podeDespachar} title={motivo(podeDespachar, 'NOVO')}
          className="min-h-12 shrink-0 rounded-full bg-acao px-4 font-bold text-white disabled:opacity-40">
          Despachar
        </button>
        <button type="button" onClick={() => void acao('em-atendimento', { bombeiroId: selecionado })}
          disabled={!podeDespachar} title={motivo(podeDespachar, 'NOVO')}
          className="min-h-12 shrink-0 rounded-full border border-borda px-4 text-borda disabled:opacity-40">
          Atend.
        </button>
        <button type="button" onClick={falsoAlarme} disabled={!podeFalsoAlarme}
          title={motivo(podeFalsoAlarme, 'NOVO ou EM_ATENDIMENTO')}
          className="min-h-12 shrink-0 rounded-full border border-linha px-4 text-suave disabled:opacity-40">
          Falso alarme
        </button>
        <button type="button" onClick={() => void acao('encerrar')} disabled={!podeEncerrar}
          title={motivo(podeEncerrar, 'EM_ATENDIMENTO')}
          className="min-h-12 shrink-0 rounded-full border border-borda px-4 text-borda disabled:opacity-40">
          Encerrar
        </button>
      </div>
    </section>
  );
}

import { FlaskConical, RotateCcw, Send, Waves } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { usePolling } from '../hooks/usePolling';
import { api, mensagemErro } from '../services/api';
import type { LeituraResultado, Limites, NivelRisco, Sensor } from '../types';
import { formatarData } from '../utils/format';

interface ResultadoEnvio {
  sensorId: number;
  ponto: string;
  ok: boolean;
  mensagem: string;
  chamadoId?: number | null;
}

const presets = [
  { rotulo: 'Normal', temperatura: 26, fumaca: 5 },
  { rotulo: 'Atenção', temperatura: 45, fumaca: 35 },
  { rotulo: 'Princípio de incêndio', temperatura: 60, fumaca: 55 },
  { rotulo: 'Incêndio', temperatura: 85, fumaca: 90 },
];

const limitesPadrao: Limites = {
  temperaturaAtencao: 40,
  temperaturaAlerta: 57,
  temperaturaCritica: 80,
  fumacaAtencao: 30,
  fumacaAlerta: 50,
  fumacaCritica: 85,
};

function classificar(temperatura: number, fumaca: number, limites: Limites): NivelRisco {
  if ((temperatura >= limites.temperaturaAlerta && fumaca >= limites.fumacaAlerta)
    || temperatura >= limites.temperaturaCritica
    || fumaca >= limites.fumacaCritica) return 'ALERTA';
  if (temperatura >= limites.temperaturaAtencao || fumaca >= limites.fumacaAtencao) return 'ATENCAO';
  return 'NORMAL';
}

const coresRisco: Record<NivelRisco, string> = {
  NORMAL: 'bg-borda text-painel',
  ATENCAO: 'bg-creme text-painel',
  ALERTA: 'bg-acao text-white font-bold',
};

export function Simulador() {
  const navigate = useNavigate();
  const sensores = usePolling<Sensor[]>(async () => (await api.get('/sensores')).data);
  const limites = usePolling<Limites>(async () => (await api.get('/configuracao/limites')).data, 60000);
  const [nomeLocal, setNomeLocal] = useState('');
  const [marcados, setMarcados] = useState<number[]>([]);
  const [temperatura, setTemperatura] = useState(26);
  const [fumaca, setFumaca] = useState(5);
  const [umidade, setUmidade] = useState('');
  const [resultados, setResultados] = useState<ResultadoEnvio[]>([]);
  const [mensagem, setMensagem] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);
  const [autoAtivo, setAutoAtivo] = useState(false);
  const [intervalo, setIntervalo] = useState(5);

  const locais = useMemo(() => {
    const agrupados = new Map<string, Sensor[]>();
    (sensores.data ?? []).forEach((sensor) => {
      const lista = agrupados.get(sensor.nomeLocal) ?? [];
      lista.push(sensor);
      agrupados.set(sensor.nomeLocal, lista);
    });
    return [...agrupados.entries()].map(([nome, itens]) => ({ nome, itens }));
  }, [sensores.data]);

  useEffect(() => {
    if (!nomeLocal && locais.length > 0) setNomeLocal(locais[0].nome);
  }, [locais, nomeLocal]);

  const sensoresDoLocal = locais.find((local) => local.nome === nomeLocal)?.itens ?? [];

  useEffect(() => {
    setMarcados(sensoresDoLocal.filter((sensor) => sensor.ativo).map((sensor) => sensor.id));
    // Só reinicia a seleção quando o local (ou o total de sensores) muda.
  }, [nomeLocal, sensores.data?.length]);

  const risco = classificar(temperatura, fumaca, limites.data ?? limitesPadrao);

  async function enviar() {
    if (marcados.length === 0) {
      setMensagem('Marque ao menos um sensor para enviar a leitura.');
      return;
    }
    setEnviando(true);
    setMensagem(null);
    const novos: ResultadoEnvio[] = [];
    for (const id of marcados) {
      const sensor = sensoresDoLocal.find((item) => item.id === id);
      try {
        const { data } = await api.post<LeituraResultado>('/leituras', {
          sensorId: id,
          temperatura,
          nivelFumaca: fumaca,
          umidade: umidade === '' ? null : Number(umidade),
        });
        novos.push({
          sensorId: id,
          ponto: sensor?.localizacao ?? `Sensor ${id}`,
          ok: true,
          mensagem: `${data.nivelRisco} · leitura registrada`,
          chamadoId: data.chamadoId,
        });
      } catch (err) {
        novos.push({
          sensorId: id,
          ponto: sensor?.localizacao ?? `Sensor ${id}`,
          ok: false,
          mensagem: mensagemErro(err),
        });
      }
    }
    setResultados((anteriores) => [...novos, ...anteriores].slice(0, 20));
    setEnviando(false);
  }

  useEffect(() => {
    if (!autoAtivo) return;
    const id = window.setInterval(() => void enviar(), intervalo * 1000);
    return () => window.clearInterval(id);
  }, [autoAtivo, intervalo, temperatura, fumaca, umidade, marcados.join(',')]);

  async function resetar() {
    if (!window.confirm('Apagar chamados, notificações e leituras e recriar o cenário inicial?')) return;
    try {
      await api.post('/simulacao/reset');
      setResultados([]);
      setMensagem('Demonstração reiniciada.');
      await sensores.refetch();
      navigate('/chamados');
    } catch (err) {
      setMensagem(mensagemErro(err));
    }
  }

  return (
    <section aria-labelledby="titulo-simulador">
      <div className="mb-6 flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 id="titulo-simulador" className="text-2xl font-black md:text-3xl">Simulador de ambiente</h1>
          <p className="mt-2 max-w-2xl text-sm text-suave">
            Sensores simulados. Digite ou escolha os valores para reproduzir o ambiente.
          </p>
        </div>
        <button type="button" onClick={() => void resetar()}
          className="flex min-h-12 items-center gap-2 rounded-full border border-borda px-4 text-borda hover:bg-linha">
          <RotateCcw size={17} aria-hidden /> Resetar demonstração
        </button>
      </div>

      {(mensagem || sensores.error) && (
        <p role="status" aria-live="polite" className="mb-5 rounded-xl bg-creme p-3 text-sm font-semibold text-painel">
          {mensagem ?? sensores.error}
        </p>
      )}

      <div className="grid gap-6 lg:grid-cols-2">
        <div className="space-y-6 rounded-2xl border border-linha bg-painel p-5">
          <div>
            <label htmlFor="local" className="mb-2 block text-sm font-semibold text-borda">Local</label>
            <select id="local" value={nomeLocal} onChange={(evento) => setNomeLocal(evento.target.value)}
              className="min-h-12 w-full rounded-xl border border-borda bg-white px-3 text-fundo">
              {locais.map((local) => <option key={local.nome} value={local.nome}>{local.nome}</option>)}
            </select>
          </div>

          <fieldset>
            <legend className="mb-2 text-sm font-semibold text-borda">Sensores que enviam a leitura</legend>
            <div className="space-y-2">
              {sensoresDoLocal.map((sensor) => (
                <label key={sensor.id} className="flex min-h-11 cursor-pointer items-center gap-3 rounded-xl bg-fundo px-3 text-sm">
                  <input type="checkbox" className="size-5 accent-[#ed5533]"
                    checked={marcados.includes(sensor.id)}
                    onChange={(evento) => setMarcados((anteriores) => evento.target.checked
                      ? [...anteriores, sensor.id]
                      : anteriores.filter((id) => id !== sensor.id))} />
                  <span>#{sensor.id} · {sensor.localizacao}</span>
                  {!sensor.ativo && <span className="ml-auto text-xs text-suave">inativo</span>}
                </label>
              ))}
            </div>
          </fieldset>

          <div>
            <label htmlFor="temperatura" className="mb-2 block text-sm font-semibold text-borda">
              Temperatura (°C)
            </label>
            <div className="flex items-center gap-4">
              <input id="temperatura" type="number" min={-10} max={120} step={0.5} value={temperatura}
                onChange={(evento) => setTemperatura(Number(evento.target.value))}
                className="min-h-12 w-28 rounded-xl border border-borda bg-white px-3 text-fundo" />
              <input type="range" min={-10} max={120} step={0.5} value={temperatura}
                aria-label="Ajustar temperatura"
                onChange={(evento) => setTemperatura(Number(evento.target.value))}
                className="h-11 flex-1 accent-[#ed5533]" />
            </div>
          </div>

          <div>
            <label htmlFor="fumaca" className="mb-2 block text-sm font-semibold text-borda">Fumaça (0–100)</label>
            <div className="flex items-center gap-4">
              <input id="fumaca" type="number" min={0} max={100} step={0.5} value={fumaca}
                onChange={(evento) => setFumaca(Number(evento.target.value))}
                className="min-h-12 w-28 rounded-xl border border-borda bg-white px-3 text-fundo" />
              <input type="range" min={0} max={100} step={0.5} value={fumaca}
                aria-label="Ajustar nível de fumaça"
                onChange={(evento) => setFumaca(Number(evento.target.value))}
                className="h-11 flex-1 accent-[#ed5533]" />
            </div>
          </div>

          <div>
            <label htmlFor="umidade" className="mb-2 block text-sm font-semibold text-borda">Umidade (%) — opcional</label>
            <input id="umidade" type="number" min={0} max={100} step={1} value={umidade}
              onChange={(evento) => setUmidade(evento.target.value)} placeholder="Ex.: 55"
              className="min-h-12 w-full rounded-xl border border-borda bg-white px-3 text-fundo" />
          </div>

          <div>
            <p className="mb-2 text-sm font-semibold text-borda">Presets</p>
            <div className="flex flex-wrap gap-2">
              {presets.map((preset) => (
                <button key={preset.rotulo} type="button"
                  onClick={() => { setTemperatura(preset.temperatura); setFumaca(preset.fumaca); }}
                  className="min-h-11 rounded-full border border-linha px-4 text-sm text-white hover:bg-linha">
                  {preset.rotulo}
                </button>
              ))}
            </div>
          </div>
        </div>

        <div className="space-y-6">
          <div className="rounded-2xl border border-linha bg-painel p-5">
            <h2 className="mb-3 flex items-center gap-2 text-lg font-bold">
              <Waves size={20} className="text-borda" aria-hidden /> Prévia da classificação
            </h2>
            <p className={`inline-flex min-h-11 items-center rounded-full px-5 text-lg ${coresRisco[risco]}`}>
              {risco}
            </p>
            <p className="mt-3 text-xs text-suave">
              Classificação preliminar no navegador; o backend é a fonte de verdade.
            </p>
          </div>

          <div className="rounded-2xl border border-linha bg-painel p-5">
            <button type="button" onClick={() => void enviar()} disabled={enviando}
              className="flex min-h-12 w-full items-center justify-center gap-2 rounded-full bg-acao px-5 font-bold text-white disabled:opacity-50">
              <Send size={18} aria-hidden /> {enviando ? 'ENVIANDO…' : 'Enviar leitura'}
            </button>

            <div className="mt-5 flex flex-wrap items-center gap-3 border-t border-linha pt-4">
              <label className="flex min-h-11 cursor-pointer items-center gap-2 text-sm">
                <input type="checkbox" className="size-5 accent-[#ed5533]"
                  checked={autoAtivo} onChange={(evento) => setAutoAtivo(evento.target.checked)} />
                Enviar automaticamente
              </label>
              <label htmlFor="intervalo" className="text-sm text-suave">A cada</label>
              <select id="intervalo" value={intervalo} onChange={(evento) => setIntervalo(Number(evento.target.value))}
                className="min-h-11 rounded-xl border border-borda bg-white px-3 text-fundo">
                <option value={3}>3 s</option>
                <option value={5}>5 s</option>
                <option value={10}>10 s</option>
              </select>
            </div>
          </div>

          <div className="rounded-2xl border border-linha bg-painel p-5">
            <h2 className="mb-3 flex items-center gap-2 text-lg font-bold">
              <FlaskConical size={20} className="text-acao" aria-hidden /> Resultados e histórico
            </h2>
            {resultados.length === 0 ? (
              <p className="text-sm text-suave">Nenhuma leitura enviada nesta sessão.</p>
            ) : (
              <ul className="max-h-80 space-y-2 overflow-y-auto text-sm">
                {resultados.map((resultado, indice) => (
                  <li key={`${resultado.sensorId}-${indice}`}
                    className={`rounded-xl p-3 ${resultado.ok ? 'bg-fundo' : 'bg-acao/15'}`}>
                    <p className="font-semibold">#{resultado.sensorId} · {resultado.ponto}</p>
                    <p className={resultado.ok ? 'text-borda' : 'text-white'}>{resultado.mensagem}</p>
                    {resultado.chamadoId != null && (
                      <button type="button" onClick={() => navigate(`/chamados/${resultado.chamadoId}`)}
                        className="mt-1 min-h-11 font-bold text-acao underline">
                        Ver chamado #{String(resultado.chamadoId).padStart(3, '0')}
                      </button>
                    )}
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      </div>

      <p className="mt-6 text-xs text-suave">
        Ambiente simulado para fins acadêmicos. Nenhum alerta é real. Última consulta: {formatarData(new Date().toISOString())}.
      </p>
    </section>
  );
}

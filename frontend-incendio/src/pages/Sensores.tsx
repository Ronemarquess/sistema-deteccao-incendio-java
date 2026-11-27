import { Pencil, Plus } from 'lucide-react';
import { useEffect, useMemo, useState, type FormEvent } from 'react';
import { Modal } from '../components/Modal';
import { ResumoCard } from '../components/ResumoCard';
import { useAuth } from '../context/AuthContext';
import { usePolling } from '../hooks/usePolling';
import { api, mensagemErro } from '../services/api';
import type { Sensor, SensorResumo } from '../types';
import { formatarTemperatura, rotuloFumaca } from '../utils/format';

interface Formulario {
  localizacao: string;
  nomeLocal: string;
  latitude: string;
  longitude: string;
}

const vazio: Formulario = { localizacao: '', nomeLocal: '', latitude: '', longitude: '' };

export function Sensores() {
  const { bombeiro } = useAuth();
  const [modalAberto, setModalAberto] = useState(false);
  const [editando, setEditando] = useState<number | null>(null);
  const [formulario, setFormulario] = useState<Formulario>(vazio);
  const [erro, setErro] = useState<string | null>(null);
  const [otimistas, setOtimistas] = useState<Record<number, boolean>>({});

  const lista = usePolling<Sensor[]>(async () => (await api.get('/sensores')).data);
  const resumo = usePolling<SensorResumo>(async () => (await api.get('/sensores/resumo')).data);

  useEffect(() => {
    if (!erro) return;
    const id = window.setTimeout(() => setErro(null), 8000);
    return () => window.clearTimeout(id);
  }, [erro]);

  const sensores = useMemo(
    () => (lista.data ?? []).map((sensor) => ({
      ...sensor,
      ativo: otimistas[sensor.id] ?? sensor.ativo,
    })),
    [lista.data, otimistas],
  );

  function abrirNovo() {
    setEditando(null);
    setFormulario(vazio);
    setErro(null);
    setModalAberto(true);
  }

  function abrirEdicao(sensor: Sensor) {
    setEditando(sensor.id);
    setFormulario({
      localizacao: sensor.localizacao,
      nomeLocal: sensor.nomeLocal,
      latitude: String(sensor.latitude),
      longitude: String(sensor.longitude),
    });
    setErro(null);
    setModalAberto(true);
  }

  async function salvar(evento: FormEvent) {
    evento.preventDefault();
    setErro(null);
    const corpo = {
      localizacao: formulario.localizacao.trim(),
      nomeLocal: formulario.nomeLocal.trim(),
      latitude: Number(formulario.latitude),
      longitude: Number(formulario.longitude),
    };
    try {
      if (editando == null) await api.post('/sensores', corpo);
      else await api.put(`/sensores/${editando}`, corpo);
      setModalAberto(false);
      await Promise.all([lista.refetch(), resumo.refetch()]);
    } catch (err) {
      setErro(mensagemErro(err));
    }
  }

  async function alternarAtivo(sensor: Sensor) {
    const novoValor = !sensor.ativo;
    setOtimistas((anteriores) => ({ ...anteriores, [sensor.id]: novoValor }));
    try {
      await api.patch(`/sensores/${sensor.id}/ativo`, { ativo: novoValor });
      await Promise.all([lista.refetch(), resumo.refetch()]);
    } catch (err) {
      setErro(`Não foi possível atualizar o sensor: ${mensagemErro(err)}`);
      await lista.refetch();
    } finally {
      setOtimistas(({ [sensor.id]: _ignorado, ...restantes }) => restantes);
    }
  }

  return (
    <section aria-labelledby="titulo-sensores">
      <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 id="titulo-sensores" className="text-2xl font-black md:text-3xl">Sensores</h1>
          <p className="mt-1 text-sm text-suave">Nós ESP32 com sensores MQ-2 e DHT22.</p>
        </div>
        <button type="button" onClick={abrirNovo}
          className="flex min-h-12 items-center gap-2 rounded-full bg-acao px-5 font-bold text-white">
          <Plus size={18} aria-hidden /> Novo sensor
        </button>
      </div>

      {(erro || lista.error) && (
        <p role="alert" className="mb-5 rounded-xl border border-acao bg-acao/15 p-3 text-sm font-semibold">
          {erro ?? lista.error}
        </p>
      )}

      <div className="grid gap-4 sm:grid-cols-3">
        <ResumoCard titulo="Total" valor={resumo.data?.total ?? sensores.length} detalhe="Sensores cadastrados" tom="claro" />
        <ResumoCard titulo="Ativos" valor={resumo.data?.ativos ?? 0} detalhe="Enviando leituras" tom="borda" />
        <ResumoCard titulo="Inativos" valor={resumo.data?.inativos ?? 0} detalhe="Sem envio de alertas" tom="acao" />
      </div>

      <div className="mt-6 overflow-hidden rounded-2xl border border-linha bg-painel">
        {lista.loading && !lista.data ? (
          <p className="p-8 text-center text-suave">Carregando sensores…</p>
        ) : sensores.length === 0 ? (
          <p className="p-8 text-center text-suave">Nenhum sensor cadastrado.</p>
        ) : (
          <>
            <div className="hidden overflow-x-auto md:block">
              <table className="w-full text-left text-sm">
                <thead className="bg-fundo text-xs uppercase text-borda">
                  <tr>
                    <th scope="col" className="px-4 py-3">ID</th>
                    <th scope="col" className="px-4 py-3">Local · ponto</th>
                    <th scope="col" className="px-4 py-3">Temperatura</th>
                    <th scope="col" className="px-4 py-3">Fumaça</th>
                    <th scope="col" className="px-4 py-3">Ativo</th>
                    <th scope="col" className="px-4 py-3 text-right">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {sensores.map((sensor) => (
                    <tr key={sensor.id} className={`border-t border-linha ${sensor.ativo ? '' : 'text-suave'}`}>
                      <td className="px-4 py-3">#{sensor.id}</td>
                      <td className="px-4 py-3">{sensor.nomeLocal} · {sensor.localizacao}</td>
                      <td className="px-4 py-3">{formatarTemperatura(sensor.temperatura)}</td>
                      <td className="px-4 py-3">{rotuloFumaca(sensor.nivelFumacaRotulo)}</td>
                      <td className="px-4 py-3">
                        <button type="button" role="switch" aria-checked={sensor.ativo}
                          aria-label={`Sensor ${sensor.id} ativo`}
                          onClick={() => void alternarAtivo(sensor)}
                          className={`min-h-11 min-w-12 rounded-full px-3 font-bold ${sensor.ativo ? 'bg-borda text-painel' : 'bg-linha text-suave'}`}>
                          {sensor.ativo ? 'Sim' : 'Não'}
                        </button>
                      </td>
                      <td className="px-4 py-3 text-right">
                        <button type="button" onClick={() => abrirEdicao(sensor)}
                          className="flex min-h-11 items-center gap-2 rounded-full border border-borda px-4 text-borda hover:bg-linha">
                          <Pencil size={16} aria-hidden /> Editar
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <ul className="divide-y divide-linha md:hidden">
              {sensores.map((sensor) => (
                <li key={sensor.id} className={`p-4 ${sensor.ativo ? '' : 'bg-linha/20 text-suave'}`}>
                  <p className="font-bold">#{sensor.id} · {sensor.localizacao}</p>
                  <p className="text-sm">{sensor.nomeLocal}</p>
                  <p className="mt-1 text-xs">
                    {formatarTemperatura(sensor.temperatura)} · fumaça {rotuloFumaca(sensor.nivelFumacaRotulo)}
                  </p>
                  <div className="mt-3 flex gap-2">
                    <button type="button" role="switch" aria-checked={sensor.ativo}
                      onClick={() => void alternarAtivo(sensor)}
                      className={`min-h-11 flex-1 rounded-full font-bold ${sensor.ativo ? 'bg-borda text-painel' : 'bg-linha text-suave'}`}>
                      {sensor.ativo ? 'Ativo' : 'Inativo'}
                    </button>
                    <button type="button" onClick={() => abrirEdicao(sensor)}
                      className="min-h-11 rounded-full border border-borda px-4 text-borda">
                      Editar
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          </>
        )}
      </div>

      {modalAberto && (
        <Modal titulo={editando == null ? 'Novo sensor' : `Editar sensor #${editando}`} onFechar={() => setModalAberto(false)}>
          <form onSubmit={salvar} className="space-y-4">
            {([
              ['localizacao', 'Localização do ponto*', 'Ex.: Térreo · sala 2'],
              ['nomeLocal', 'Nome do local*', 'Ex.: Casarão histórico · Aracaju'],
              ['latitude', 'Latitude*', '-10.9111'],
              ['longitude', 'Longitude*', '-37.0717'],
            ] as const).map(([campo, rotulo, placeholder]) => (
              <div key={campo}>
                <label htmlFor={campo} className="mb-1 block text-sm font-semibold text-borda">{rotulo}</label>
                <input
                  id={campo}
                  type={campo === 'latitude' || campo === 'longitude' ? 'number' : 'text'}
                  step="any"
                  required
                  value={formulario[campo]}
                  onChange={(evento) => setFormulario((anterior) => ({ ...anterior, [campo]: evento.target.value }))}
                  placeholder={placeholder}
                  className="min-h-12 w-full rounded-xl border border-borda bg-white px-3 text-fundo outline-none"
                />
              </div>
            ))}
            {erro && <p role="alert" className="text-sm font-semibold text-acao">{erro}</p>}
            <div className="flex gap-3 pt-2">
              <button type="button" onClick={() => setModalAberto(false)}
                className="min-h-12 flex-1 rounded-full border border-borda text-borda">Cancelar</button>
              <button type="submit" className="min-h-12 flex-1 rounded-full bg-acao font-bold text-white">Salvar</button>
            </div>
          </form>
        </Modal>
      )}

      <p className="mt-4 text-xs text-suave">Usuário: {bombeiro?.nome}</p>
    </section>
  );
}

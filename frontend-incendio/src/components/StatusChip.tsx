import type { StatusChamado } from '../types';

const estilos: Record<StatusChamado, string> = {
  NOVO: 'bg-acao text-white font-bold',
  EM_ATENDIMENTO: 'bg-borda text-painel font-bold',
  ENCERRADO: 'border border-borda text-borda',
  FALSO_ALARME: 'bg-linha text-suave',
};

const rotulos: Record<StatusChamado, string> = {
  NOVO: 'NOVO',
  EM_ATENDIMENTO: 'EM ATEND.',
  ENCERRADO: 'ENCERRADO',
  FALSO_ALARME: 'FALSO ALARME',
};

export function StatusChip({ status }: { status: StatusChamado }) {
  return (
    <span className={`inline-flex min-h-8 items-center rounded-full px-3 text-xs tracking-wide ${estilos[status]}`}>
      {rotulos[status]}
    </span>
  );
}

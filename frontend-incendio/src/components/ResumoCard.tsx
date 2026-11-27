interface ResumoCardProps {
  titulo: string;
  valor: number;
  detalhe: string;
  tom: 'acao' | 'borda' | 'claro';
}

const estilos = {
  acao: 'bg-acao text-white',
  borda: 'bg-borda text-painel',
  claro: 'bg-white text-painel',
};

export function ResumoCard({ titulo, valor, detalhe, tom }: ResumoCardProps) {
  return (
    <article className={`rounded-2xl p-5 shadow-lg ${estilos[tom]}`}>
      <p className="text-sm font-semibold uppercase tracking-wider">{titulo}</p>
      <p className="mt-1 text-4xl font-black">{valor}</p>
      <p className="mt-1 text-xs opacity-80">{detalhe}</p>
    </article>
  );
}

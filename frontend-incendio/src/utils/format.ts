export function formatarData(iso: string | null | undefined): string {
  if (!iso) return '—';
  const data = new Date(iso);
  if (Number.isNaN(data.getTime())) return '—';
  return data.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
}

export function formatarTemperatura(valor: number | null | undefined): string {
  return valor == null ? '—' : `${valor.toFixed(1).replace('.', ',')} °C`;
}

export function rotuloFumaca(rotulo: string | null | undefined): string {
  if (rotulo === 'ALTA') return 'Alta';
  if (rotulo === 'MEDIA') return 'Média';
  if (rotulo === 'BAIXA') return 'Baixa';
  return '—';
}

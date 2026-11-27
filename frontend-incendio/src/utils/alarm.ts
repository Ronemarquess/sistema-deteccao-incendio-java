let contexto: AudioContext | null = null;

function obterContexto(): AudioContext | null {
  if (typeof window === 'undefined') return null;
  if (!contexto) {
    const Construtor = window.AudioContext
      ?? (window as unknown as { webkitAudioContext?: typeof AudioContext }).webkitAudioContext;
    if (!Construtor) return null;
    contexto = new Construtor();
  }
  return contexto;
}

/** Chamada no clique de login para liberar áudio em navegadores que exigem interação. */
export function unlockAudio(): void {
  const ctx = obterContexto();
  if (ctx?.state === 'suspended') {
    void ctx.resume();
  }
}

function toque(ctx: AudioContext, inicio: number, frequencia: number, duracao: number): void {
  const oscilador = ctx.createOscillator();
  const ganho = ctx.createGain();
  oscilador.type = 'square';
  oscilador.frequency.value = frequencia;
  ganho.gain.setValueAtTime(0.0001, inicio);
  ganho.gain.exponentialRampToValueAtTime(0.12, inicio + 0.02);
  ganho.gain.exponentialRampToValueAtTime(0.0001, inicio + duracao);
  oscilador.connect(ganho).connect(ctx.destination);
  oscilador.start(inicio);
  oscilador.stop(inicio + duracao + 0.02);
}

/** Alarme sonoro curto para chamados NOVO inéditos. */
export function playAlarm(): void {
  const ctx = obterContexto();
  if (!ctx) return;
  if (ctx.state === 'suspended') void ctx.resume();
  const agora = ctx.currentTime + 0.02;
  toque(ctx, agora, 880, 0.16);
  toque(ctx, agora + 0.22, 1175, 0.16);
  toque(ctx, agora + 0.44, 880, 0.16);
}

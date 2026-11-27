import { X } from 'lucide-react';
import type { ReactNode } from 'react';

interface ModalProps {
  titulo: string;
  children: ReactNode;
  onFechar: () => void;
}

export function Modal({ titulo, children, onFechar }: ModalProps) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4" role="presentation" onClick={onFechar}>
      <div
        role="dialog"
        aria-modal="true"
        aria-label={titulo}
        className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-2xl border border-linha bg-painel p-6 shadow-2xl"
        onClick={(evento) => evento.stopPropagation()}
      >
        <div className="mb-5 flex items-center justify-between gap-4">
          <h2 className="text-xl font-bold text-white">{titulo}</h2>
          <button
            type="button"
            onClick={onFechar}
            aria-label="Fechar"
            className="flex size-11 items-center justify-center rounded-full border border-borda text-borda hover:bg-linha"
          >
            <X size={20} aria-hidden />
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}

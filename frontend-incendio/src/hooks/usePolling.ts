import { useCallback, useEffect, useRef, useState } from 'react';

interface Estado<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
}

export function usePolling<T>(
  carregar: () => Promise<T>,
  intervaloMs = 5000,
  habilitado = true,
): Estado<T> {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const carregarRef = useRef(carregar);
  carregarRef.current = carregar;

  const refetch = useCallback(async () => {
    try {
      const resultado = await carregarRef.current();
      setData(resultado);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar dados.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!habilitado) return;
    void refetch();
    const id = window.setInterval(() => void refetch(), intervaloMs);
    return () => window.clearInterval(id);
  }, [habilitado, intervaloMs, refetch]);

  return { data, loading, error, refetch };
}

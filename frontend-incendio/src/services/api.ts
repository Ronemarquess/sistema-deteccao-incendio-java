import axios, { AxiosError } from 'axios';

const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const api = axios.create({ baseURL, timeout: 10000 });

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<{ mensagem?: string }>) => {
    if (!error.response) {
      return Promise.reject(new Error('Servidor indisponível. Verifique se o backend está executando.'));
    }
    const mensagem = error.response.data?.mensagem;
    return Promise.reject(new Error(mensagem || `Erro ${error.response.status} ao consultar o servidor.`));
  },
);

export function mensagemErro(error: unknown): string {
  return error instanceof Error ? error.message : 'Ocorreu um erro inesperado.';
}

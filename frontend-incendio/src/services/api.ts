import axios from 'axios'
import type {
  BombeiroRequest,
  BombeiroResponse,
  LeituraSensor,
  LoginRequest,
  Sensor,
} from '../types'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api',
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
  timeout: 5000,
})

export async function getSensores(signal?: AbortSignal): Promise<Sensor[]> {
  const { data } = await api.get<Sensor[]>('/sensores', { signal })
  return data
}

export async function getLeituras(signal?: AbortSignal): Promise<LeituraSensor[]> {
  const { data } = await api.get<LeituraSensor[]>('/leituras', { signal })
  return data
}

export async function cadastrarBombeiro(request: BombeiroRequest): Promise<BombeiroResponse> {
  const { data } = await api.post<BombeiroResponse>('/bombeiros', request)
  return data
}

export async function loginBombeiro(request: LoginRequest): Promise<BombeiroResponse> {
  const { data } = await api.post<BombeiroResponse>('/bombeiros/login', request)
  return data
}

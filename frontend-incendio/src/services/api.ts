import axios from 'axios'
import type {
  BombeiroRequest,
  BombeiroResponse,
  LeituraSensor,
  LoginRequest,
  Sensor,
} from '../types'
import {
  cadastrarBombeiroDemo,
  isDemoMode,
  getLeiturasDemo,
  getSensoresDemo,
  loginBombeiroDemo,
  resetDemoMode,
} from './demoStore'

const configuredApiUrl = import.meta.env.VITE_API_URL?.trim()
const localApiUrl = 'http://localhost:8080/api'
const currentHost = typeof window === 'undefined' ? '' : window.location.hostname
const isLocalOrOfficialHost = currentHost === 'localhost'
  || currentHost === '127.0.0.1'
  || currentHost === 'frontend-incendio.vercel.app'
const defaultApiUrl = typeof window === 'undefined' || isLocalOrOfficialHost
  ? localApiUrl
  : `${window.location.origin}/api`

export const api = axios.create({
  baseURL: configuredApiUrl || defaultApiUrl,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
  timeout: 5000,
})

function isNetworkFailure(error: unknown): boolean {
  if (axios.isCancel(error)) return false

  if (axios.isAxiosError(error)) {
    return !error.response
      && ['ERR_NETWORK', 'ECONNABORTED', 'ETIMEDOUT'].includes(error.code ?? '')
  }

  return error instanceof TypeError && /failed to fetch|networkerror|network request failed/i.test(error.message)
}

async function requestWithDemoFallback<T>(
  request: () => Promise<T>,
  fallback: () => Promise<T>,
): Promise<T> {
  try {
    return await request()
  } catch (error) {
    if (!isNetworkFailure(error)) throw error
    return fallback()
  }
}

export async function getSensores(signal?: AbortSignal): Promise<Sensor[]> {
  return requestWithDemoFallback(
    async () => (await api.get<Sensor[]>('/sensores', { signal })).data,
    getSensoresDemo,
  )
}

export async function getLeituras(signal?: AbortSignal): Promise<LeituraSensor[]> {
  return requestWithDemoFallback(
    async () => (await api.get<LeituraSensor[]>('/leituras', { signal })).data,
    getLeiturasDemo,
  )
}

export async function cadastrarBombeiro(request: BombeiroRequest): Promise<BombeiroResponse> {
  return requestWithDemoFallback(
    async () => (await api.post<BombeiroResponse>('/bombeiros', request)).data,
    () => cadastrarBombeiroDemo(request),
  )
}

export async function loginBombeiro(request: LoginRequest): Promise<BombeiroResponse> {
  return requestWithDemoFallback(
    async () => (await api.post<BombeiroResponse>('/bombeiros/login', request)).data,
    () => loginBombeiroDemo(request),
  )
}

export { isDemoMode }

export function beginApiRefresh(): void {
  resetDemoMode()
}

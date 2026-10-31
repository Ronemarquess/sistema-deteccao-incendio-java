import axios from 'axios'
import type { LeituraSensor, Sensor } from '../types'

export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
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

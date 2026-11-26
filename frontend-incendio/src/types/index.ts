export interface Sensor {
  id: number
  tipo: string
  localizacao: string
}

export interface LeituraSensor {
  id: number
  valor: number
  instanteMedicao: string
  sensor: Sensor
}

export interface BombeiroRequest {
  nome: string
  email: string
  senha: string
  matricula: string
  patente: string
  batalhao: string
  telefone: string
}

export interface LoginRequest {
  email: string
  senha: string
}

export interface BombeiroResponse {
  id: number
  nome: string
  email: string
  ativo: boolean
  matricula: string
  patente: string
  batalhao: string
  telefone: string
}

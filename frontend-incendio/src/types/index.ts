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

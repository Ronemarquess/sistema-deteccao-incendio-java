export type StatusChamado = 'NOVO' | 'EM_ATENDIMENTO' | 'ENCERRADO' | 'FALSO_ALARME';
export type NivelRisco = 'NORMAL' | 'ATENCAO' | 'ALERTA';
export type NivelFumaca = 'BAIXA' | 'MEDIA' | 'ALTA';

export interface Bombeiro {
  id: number;
  nome: string;
  email: string;
  matricula: string;
  patente: string;
  batalhao: string;
  telefone: string;
}

export interface Sensor {
  id: number;
  localizacao: string;
  nomeLocal: string;
  latitude: number;
  longitude: number;
  ativo: boolean;
  temperatura: number | null;
  nivelFumaca: number | null;
  nivelFumacaRotulo: NivelFumaca | null;
  instanteMedicao: string | null;
}

export interface SensorResumo {
  total: number;
  ativos: number;
  inativos: number;
}

export interface ChamadoResumo {
  emAberto: number;
  emAtendimento: number;
  encerradosHoje: number;
}

export interface ChamadoItem {
  id: number;
  numero: string;
  local: string;
  quantidadeSensores: number;
  temperaturaMaxima: number | null;
  status: StatusChamado;
  horarioExato: string;
}

export interface SensorDoChamado {
  id: number;
  localizacao: string;
  temperatura: number | null;
  nivelFumaca: number | null;
  nivelFumacaRotulo: NivelFumaca | null;
  ativo: boolean;
}

export interface Notificacao {
  id: number;
  canal: 'PUSH' | 'WHATSAPP' | 'SMS';
  enviadaEm: string;
  mensagem: string;
  destinatario: string;
}

export interface ChamadoDetalhe {
  id: number;
  numero: string;
  status: StatusChamado;
  local: string;
  latitude: number | null;
  longitude: number | null;
  horarioExato: string;
  elevacaoTemperatura: number | null;
  temperaturaMaxima: number | null;
  bombeiroResponsavel: string | null;
  sensores: SensorDoChamado[];
  notificacoes: Notificacao[];
}

export interface Leitura {
  id: number;
  sensorId: number;
  temperatura: number;
  nivelFumaca: number;
  umidade: number | null;
  instanteMedicao: string;
  nivelRisco: NivelRisco;
}

export interface LeituraResultado {
  leitura: Leitura;
  nivelRisco: NivelRisco;
  chamadoId: number | null;
  chamadoCriado: boolean;
}

export interface Limites {
  temperaturaAtencao: number;
  temperaturaAlerta: number;
  temperaturaCritica: number;
  fumacaAtencao: number;
  fumacaAlerta: number;
  fumacaCritica: number;
}

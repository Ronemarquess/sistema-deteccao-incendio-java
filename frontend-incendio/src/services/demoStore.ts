import type {
  BombeiroRequest,
  BombeiroResponse,
  LeituraSensor,
  LoginRequest,
  Sensor,
} from '../types'

const storageKey = 'vigiafire.demo.v2'

interface DemoBombeiro extends BombeiroResponse {
  senhaSalt: string
  senhaHash: string
}

interface DemoStore {
  bombeiros: DemoBombeiro[]
  sensores: Sensor[]
  leituras: LeituraSensor[]
}

let demoMode = false

async function hashPassword(password: string, salt?: Uint8Array): Promise<{ salt: string; hash: string }> {
  if (!globalThis.crypto?.subtle) {
    throw new Error('Armazenamento seguro de senha indisponível neste navegador.')
  }

  const saltBytes = salt ?? globalThis.crypto.getRandomValues(new Uint8Array(16))
  const key = await globalThis.crypto.subtle.importKey(
    'raw',
    new TextEncoder().encode(password),
    'PBKDF2',
    false,
    ['deriveBits'],
  )
  const derivedBits = await globalThis.crypto.subtle.deriveBits(
    { name: 'PBKDF2', hash: 'SHA-256', salt: saltBytes, iterations: 120_000 },
    key,
    256,
  )

  return {
    salt: Array.from(saltBytes, (byte) => byte.toString(16).padStart(2, '0')).join(''),
    hash: Array.from(new Uint8Array(derivedBits), (byte) => byte.toString(16).padStart(2, '0')).join(''),
  }
}

async function createInitialStore(): Promise<DemoStore> {
  const now = Date.now()
  const seedPassword = await hashPassword('123456')
  const sensors: Sensor[] = [
    { id: 1, tipo: 'Temperatura', localizacao: '1 BBM - Lagarto - Setor Norte' },
    { id: 2, tipo: 'Fumaça', localizacao: '1 BBM - Lagarto - Setor Sul' },
    { id: 3, tipo: 'Umidade', localizacao: '1 BBM - Lagarto - Almoxarifado' },
  ]

  return {
    bombeiros: [{
      id: 1,
      nome: 'Rone Marques',
      email: 'rone@bombeiros.se.gov.br',
      ativo: true,
      matricula: 'BM-2026-01',
      patente: 'Tenente',
      batalhao: '1 BBM - Lagarto',
      telefone: '79999999999',
      senhaSalt: seedPassword.salt,
      senhaHash: seedPassword.hash,
    }],
    sensores,
    leituras: [
      { id: 1, valor: 26.4, instanteMedicao: new Date(now - 3 * 60_000).toISOString(), sensor: sensors[0] },
      { id: 2, valor: 0.02, instanteMedicao: new Date(now - 2 * 60_000).toISOString(), sensor: sensors[1] },
      { id: 3, valor: 58, instanteMedicao: new Date(now - 60_000).toISOString(), sensor: sensors[2] },
    ],
  }
}

function isDemoStore(value: unknown): value is DemoStore {
  if (typeof value !== 'object' || value === null) return false
  const store = value as Partial<DemoStore>
  return Array.isArray(store.bombeiros)
    && Array.isArray(store.sensores)
    && Array.isArray(store.leituras)
}

async function loadStore(): Promise<DemoStore> {
  const serialized = localStorage.getItem(storageKey)
  if (serialized === null) {
    const initialStore = await createInitialStore()
    localStorage.setItem(storageKey, JSON.stringify(initialStore))
    return initialStore
  }

  let parsed: unknown
  try {
    parsed = JSON.parse(serialized)
  } catch (error) {
    throw new Error('Os dados locais de demonstração estão corrompidos.', { cause: error })
  }

  if (!isDemoStore(parsed)) {
    throw new Error('Os dados locais de demonstração têm um formato inválido.')
  }
  return parsed
}

async function saveStore(store: DemoStore): Promise<void> {
  localStorage.setItem(storageKey, JSON.stringify(store))
}

function toResponse(bombeiro: DemoBombeiro): BombeiroResponse {
  return {
    id: bombeiro.id,
    nome: bombeiro.nome,
    email: bombeiro.email,
    ativo: bombeiro.ativo,
    matricula: bombeiro.matricula,
    patente: bombeiro.patente,
    batalhao: bombeiro.batalhao,
    telefone: bombeiro.telefone,
  }
}

export function isDemoMode(): boolean {
  return demoMode
}

export function resetDemoMode(): void {
  demoMode = false
}

export async function getSensoresDemo(): Promise<Sensor[]> {
  demoMode = true
  return (await loadStore()).sensores
}

export async function getLeiturasDemo(): Promise<LeituraSensor[]> {
  demoMode = true
  return (await loadStore()).leituras
}

export async function cadastrarBombeiroDemo(request: BombeiroRequest): Promise<BombeiroResponse> {
  demoMode = true
  const normalizedEmail = request.email.trim().toLowerCase()
  const normalizedMatricula = request.matricula.trim()
  if (Object.values(request).some((value) => !value.trim())) {
    throw new Error('Preencha todos os campos para cadastrar o bombeiro.')
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(normalizedEmail)) {
    throw new Error('Informe um e-mail válido.')
  }
  if (request.senha.length < 6) {
    throw new Error('A senha deve ter pelo menos 6 caracteres.')
  }

  const store = await loadStore()
  if (store.bombeiros.some((bombeiro) => bombeiro.email.toLowerCase() === normalizedEmail)) {
    throw new Error('E-mail já cadastrado.')
  }
  if (store.bombeiros.some((bombeiro) => bombeiro.matricula === normalizedMatricula)) {
    throw new Error('Matrícula já cadastrada.')
  }

  const password = await hashPassword(request.senha)
  const bombeiro: DemoBombeiro = {
    id: Math.max(0, ...store.bombeiros.map((item) => item.id)) + 1,
    nome: request.nome.trim(),
    email: normalizedEmail,
    ativo: true,
    matricula: normalizedMatricula,
    patente: request.patente.trim(),
    batalhao: request.batalhao.trim(),
    telefone: request.telefone.trim(),
    senhaSalt: password.salt,
    senhaHash: password.hash,
  }
  store.bombeiros.push(bombeiro)
  await saveStore(store)
  return toResponse(bombeiro)
}

export async function loginBombeiroDemo(request: LoginRequest): Promise<BombeiroResponse> {
  demoMode = true
  const store = await loadStore()
  const normalizedEmail = request.email.trim().toLowerCase()
  const bombeiro = store.bombeiros.find((item) => item.email.toLowerCase() === normalizedEmail)
  if (!bombeiro) {
    throw new Error('E-mail ou senha inválidos no modo de demonstração.')
  }
  const storedSalt = Uint8Array.from(
    bombeiro.senhaSalt.match(/.{2}/g) ?? [],
    (byte) => Number.parseInt(byte, 16),
  )
  const suppliedPassword = await hashPassword(request.senha, storedSalt)
  if (bombeiro.senhaHash !== suppliedPassword.hash) {
    throw new Error('E-mail ou senha inválidos no modo de demonstração.')
  }
  if (!bombeiro.ativo) {
    throw new Error('Usuário desativado.')
  }
  return toResponse(bombeiro)
}

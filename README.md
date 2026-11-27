# SAFE-ALERT — Detecção precoce de incêndios

Trabalho acadêmico da **Estácio** (professor **Luiz Santos**), por **Rone Marques e Nicolas**.

> Ambiente simulado para fins acadêmicos. Os dados e alertas não são reais e o sistema não substitui equipamentos ou serviços certificados de segurança.

O SAFE-ALERT integra um backend Spring Boot com persistência em SQLite, uma API REST e um painel React responsivo. Sensores simulados de temperatura e fumaça enviam leituras; o backend classifica o risco, abre chamados, despacha bombeiros e registra notificações simuladas.

## Tecnologias

- **Backend:** Java 17, Spring Boot 4.1.2 (`spring-boot-starter-webmvc`), Spring Data JPA, Jakarta Validation, Hibernate 7, SQLite JDBC, Lombok, springdoc, BCrypt (`spring-security-crypto`), Maven Wrapper.
- **Frontend:** React 19, TypeScript, Vite 7, Tailwind CSS 4, React Router 7, Axios, Leaflet/React Leaflet, Framer Motion, Lucide.
- **Persistência:** arquivo `safe-alert.db` na raiz do projeto (ignorado pelo Git). Testes usam SQLite em memória.

## Pré-requisitos

- JDK 17
- Node.js 20.19+ (testado com 20.20.2) e npm 10+
- Navegador com Web Audio para o alerta sonoro

## Como executar

### 1. Backend

Na raiz do repositório:

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080/api`. O schema é criado/atualizado automaticamente e o seed é aplicado apenas quando ainda não há sensores no banco.

### 2. Frontend

Em outro terminal:

```bash
cd frontend-incendio
npm install
cp .env.example .env   # opcional; ajuste VITE_API_URL se necessário
npm run dev
```

O Vite normalmente usa `http://localhost:5173`. Se essa porta estiver ocupada, ele escolhe outra (por exemplo, `5174`) — essas origens já estão liberadas no CORS do backend.

### 3. Acessar

Abra o endereço informado pelo Vite e entre com:

- **E-mail:** `rone@bombeiros.se.gov.br`
- **Senha:** `123456`

A logo do protótipo está incluída em `frontend-incendio/public/safe-alert-logo.png` e aparece no login, no cabeçalho e como ícone da aba do navegador. Se o arquivo for removido, a interface volta ao fallback textual.

## Roteiro de demonstração (acceptance)

1. Faça login com a credencial do seed; repita com senha errada para ver o erro inline.
2. No painel, confirme **Em aberto: 0** no cenário inicial.
3. Vá em **Simulador**, selecione o local "Casarão histórico · Aracaju", escolha o preset **Incêndio** e envie. Um chamado `NOVO` é criado com os sensores daquele local.
4. Abra o chamado: mapa OpenStreetMap, coordenadas GPS, horário exato, elevação de temperatura, sensores acionados e rótulo de fumaça.
5. **Despachar equipe** muda o status para `EM_ATENDIMENTO`, designa Rone e grava uma notificação `PUSH` (visível no detalhe e no log do backend). **Encerrar** só é permitido nesse estado.
6. Tente **Falso alarme** em um chamado já encerrado: a ação fica desabilitada na interface e a API responde 409.
7. Em **Sensores**, desative o sensor "Box 7" e tente uma leitura nele: a API responde 409. Crie um novo sensor pelo modal.
8. Envie leituras de **Atenção** (ex.: 45 °C / 35) e **Normal** (26 °C / 5): nenhum chamado é criado.
9. Reinicie o backend: sensores, chamados e leituras permanecem no arquivo SQLite.
10. Reduza a janela para 390 px: sem rolagem horizontal e com as ações do chamado acessíveis na barra inferior.
11. Use **Resetar demonstração** em `/api/simulacao/reset` (habilitado por configuração) para apagar chamados/notificações/leituras e recriar as leituras normais do seed.

## Endpoints principais

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/bombeiros/login` | Login com BCrypt |
| `GET/POST/PUT/PATCH` | `/api/bombeiros` | Cadastro, consulta, atualização e ativação lógica |
| `GET/POST/PUT` | `/api/sensores` | Lista, criação e edição |
| `GET` | `/api/sensores/resumo` | Totais de sensores |
| `PATCH` | `/api/sensores/{id}/ativo` | Ativa/desativa sensor |
| `GET/POST` | `/api/leituras` | Histórico e registro de leitura |
| `GET` | `/api/chamados?status=&busca=` | Lista filtrável |
| `GET` | `/api/chamados/resumo` | Em aberto, em atendimento, encerrados hoje |
| `GET` | `/api/chamados/{id}` | Detalhe com sensores e notificações |
| `POST` | `/api/chamados/{id}/despachar` | Despacha equipe e cria notificação |
| `POST` | `/api/chamados/{id}/em-atendimento` | Marca em atendimento |
| `POST` | `/api/chamados/{id}/falso-alarme` | Encerra como falso alarme |
| `POST` | `/api/chamados/{id}/encerrar` | Encerra chamado |
| `GET` | `/api/configuracao/limites` | Limites configuráveis |
| `POST` | `/api/simulacao/reset` | Reinicia o cenário demonstrativo |

Erros seguem o corpo `{ timestamp, status, erro, mensagem, campos }`: 400 (validação), 401 (credencial inválida), 404 (não encontrado), 409 (regra de negócio).

## Configuração

Arquivo `src/main/resources/application.properties`:

- `safealert.cors.origens` — origens permitidas (5173 e 5174 por padrão).
- `safealert.simulacao.habilitada` — controla `POST /api/simulacao/reset`.
- `safealert.limites.*` — limites de temperatura e fumaça para Atenção/Alerta/Crítico.
- `safealert.datasource.url` — caminho do arquivo SQLite.

## Testes e build

```bash
./mvnw clean test          # backend
cd frontend-incendio && npm run build   # frontend
```

Os testes cobrem limites de risco, transições de chamado e fluxos de integração (criação/agrupamento de chamados, sensor inativo, notificação no despacho e bloqueio de transição inválida).

## Estrutura

```text
src/main/java/br/com/estacio/incendio/
├── IncendioApplication.java
├── DataLoader.java
├── config/        # CORS, limites e BCrypt
├── controller/    # API REST e handler de erros
├── dto/           # request/response
├── exception/     # erros de domínio
├── model/         # entidades e enums
├── repository/    # Spring Data JPA
└── service/       # regras e transações
frontend-incendio/src/
├── components/    # Layout, Header, Logo, Mapa, Modal, cards
├── context/       # AuthContext (sessionStorage)
├── hooks/         # usePolling
├── pages/         # Login, Chamados, ChamadoDetalhe, Sensores, Simulador
├── services/api.ts
├── types/
└── utils/         # alarme sonoro e formatação
docs/              # DECISOES, TAREFAS, diagrama-de-classes
```

Consulte [`docs/diagrama-de-classes.md`](docs/diagrama-de-classes.md) e [`docs/DECISOES.md`](docs/DECISOES.md).

## Limitações do escopo

Fora do escopo deste trabalho, portanto **não implementados**: Docker/MQTT reais, integração real com Twilio/WhatsApp, WebSocket/SSE (o painel usa polling de 5 s), Spring Security/JWT (a sessão é apenas local via `sessionStorage`) e internacionalização. Arquivos Docker presentes no repositório são legado fora do escopo.

Não há sensores físicos, firmware ESP32 nem serviços certificados. O alarme e as notificações são simulados e não devem ser usados para proteger vidas ou propriedades.

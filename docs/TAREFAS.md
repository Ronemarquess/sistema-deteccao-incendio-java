# Tarefas e evolução do projeto

Checklist atualizado após a implementação. Um item só é marcado como concluído quando foi **implementado e verificado**. Quando a verificação foi parcial, isso está registrado explicitamente.

## Etapa 1 — build, execução e persistência

- [x] Build Maven com Java 17 e Spring Boot 4.1.2 executável via `./mvnw`.
- [x] Configuração corrigida do datasource SQLite e do dialeto Hibernate (confirmado em inicialização real).
- [x] SQLite em arquivo `safe-alert.db`, pool de 1 conexão e `PRAGMA foreign_keys=ON`.
- [x] Banco de desenvolvimento ignorado pelo Git (incluindo journal/WAL/SHM).
- [x] Persistência após reinício confirmada no baseline; com o modelo final, o banco foi recriado e o seed validado.
- [x] Legado Docker documentado como fora do escopo, sem remoção dos arquivos.

## Etapa 2 — domínio, regras e seed

- [x] Pacotes migrados para `br.com.estacio.incendio.*`.
- [x] Entidades `Usuario`, `Bombeiro`, `Localizacao`, `Sensor`, `LeituraSensor`, `Chamado` e `Notificacao`.
- [x] Enums `StatusChamado`, `NivelRisco`, `NivelFumaca` e `CanalNotificacao`.
- [x] Regras de Atenção/Alerta/Crítico em `AvaliadorRisco`, com limites configuráveis.
- [x] Chamado agrega sensores do mesmo local, calcula elevação e atualiza temperaturas máximas.
- [x] Transições de estado encapsuladas: despacho, atendimento, falso alarme e encerramento.
- [x] Notificação `PUSH` criada no despacho e registrada no log do backend.
- [x] Login com BCrypt e senha do seed (`123456`) nunca armazenada em texto puro.
- [x] Seed idempotente com bombeiro, 7 sensores, leituras normais e 2 chamados históricos.
- [x] Testes unitários do avaliador (4) e das transições (5).
- [x] Testes de integração dos fluxos de leitura/chamado (6).

## Etapa 3 — API REST

- [x] Controllers para bombeiros, sensores, leituras, chamados, configuração e simulação.
- [x] DTOs de entrada/saída separados das entidades.
- [x] Bean Validation e `RestControllerAdvice` com erros 400/401/404/409/500 padronizados.
- [x] CORS lido de `safealert.cors.origens` (inclui 5173 e 5174).
- [x] Transações `readOnly` nas consultas.
- [x] `POST /api/simulacao/reset` condicionado a `safealert.simulacao.habilitada`.
- [x] Fluxo validado por `curl`: login (certo e errado), leituras, chamados, despacho, encerramento, 409, 404, 400 e reset.

## Etapa 4/5 — frontend

- [x] Projeto Vite + React 19 + TypeScript + Tailwind 4 + React Router + Leaflet + Axios criado do zero.
- [x] Login com erro inline em português; sessão em `sessionStorage`.
- [x] Painel com cards, filtros, busca e polling de 5 s; `EM ABERTO: 0` verificado no cenário inicial.
- [x] Alerta sonoro implementado no recebimento de chamado `NOVO` (áudio não auditado por microfone; apenas o disparo do código foi exercitado).
- [x] Detalhe com mapa OpenStreetMap, GPS, horário, elevação, sensores, notificações e ações.
- [x] Despachar e encerrar exercitados pela interface; notificação conferida no detalhe.
- [x] Sensores com criação/edição, ativação lógica e estados de carregamento/erro (CRUD validado pela API; clique no modal de criação não automatizado).
- [x] Simulador com presets, prévia, envio por sensor, envio automático, histórico e reset (preset "Incêndio" exercitado pela interface).
- [x] Layout responsivo em 390, 768 e 1280 px sem rolagem horizontal nas quatro rotas principais.
- [x] Ações fixas e "Iniciar rota" visíveis em 390 px.
- [x] Alvos de toque com pelo menos 44 px após ajuste nos links do cabeçalho e nos controles do Leaflet.
- [ ] Validação com leitor de tela e em dispositivo físico real.

## Etapa 6 — entrega

- [x] README com pré-requisitos, comandos, credencial, endpoints, roteiro e limitações.
- [x] `docs/DECISOES.md` com uma decisão por ambiguidade encontrada.
- [x] `docs/diagrama-de-classes.md` fiel ao código implementado.
- [x] `./mvnw clean test` e `npm run build` executados com sucesso.
- [x] Evidências de aceite registradas no README e relatadas ao usuário.
- [x] Commit final da Etapa 6 na branch atual, sem push.

## Fora do escopo / não implementado

- [ ] Docker e MQTT reais (arquivos Docker existentes são legado).
- [ ] Integração real com Twilio/WhatsApp.
- [ ] WebSocket/SSE — o frontend usa polling de 5 s.
- [ ] Spring Security/JWT — a autenticação é simulada com sessão local.
- [ ] Internacionalização (i18n).
- [ ] Sensores físicos, firmware ESP32 e uso com fogo real.
- [ ] Envio para produção ou publicação do repositório remoto.

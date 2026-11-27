# Decisões registradas

Uma linha por decisão tomada diante de ambiguidades do prompt.

- 2026-11-27: O prompt é a fonte da verdade do produto: o backend será migrado para `br.com.estacio.incendio.*` e o frontend será criado do zero, pois não havia frontend nem pacotes `br.com.estacio.incendio` no repositório.
- 2026-11-27: Mantive o Spring Boot 4.1.2 já presente no `pom.xml` (não fiz downgrade para 3.5.6) e o `spring-boot-starter-webmvc`; o dialeto SQLite do Hibernate 7 foi confirmado por teste de inicialização.
- 2026-11-27: Preservei os arquivos modificados/untracked preexistentes como referência e corrigi apenas o necessário para o baseline compilar.
- 2026-11-27: `Dockerfile`, `docker-compose.yml` e `selfalert-docker.db` serão documentados como legado fora do escopo, sem remoção.
- 2026-11-27: Os assets `safe-alert-logo.png` e `safe-alert-icon.png` serão referenciados por nome; se não forem copiados, haverá fallback textual no frontend. Em 2026-11-27, `frontend-incendio/img/prototipo_logo.png` foi copiado para `public/safe-alert-logo.png` e passou a ser a logo do login, do cabeçalho e da aba do navegador.
- 2026-11-27: Chamados históricos fechados são criados em `NOVO`, recebem sensores e só então recebem o status final, para respeitar a regra de que chamados fechados não aceitam novos sensores.
- 2026-11-27: Em chamados já existentes, a elevação de temperatura usa baseline fixo de 25 °C; em chamados novos, usa a média das últimas 5 leituras NORMAL do sensor (ou 25 °C se não houver).
- 2026-11-27: Testes automatizados usam SQLite em memória compartilhada, separado do arquivo `safe-alert.db` de desenvolvimento.
- 2026-11-27: Credencial inválida retorna 401 (corpo de erro padronizado) para permitir mensagem inline no login; as demais regras de negócio retornam 409.
- 2026-11-27: O chamado histórico `ENCERRADO` do seed usa `encerradoEm` fixo no início do dia para que "Encerrados hoje" seja 1 mesmo quando a aplicação inicia perto da meia-noite.
- 2026-11-27: Como não havia frontend no repositório, o projeto Vite foi criado do zero; assets são lidos de `frontend-incendio/public/` com fallback textual. A logo do protótipo foi posteriormente disponibilizada em `img/` e incorporada ao projeto.
- 2026-11-27: O envio automático do simulador (3/5/10 s) é um intervalo no navegador; não há WebSocket/SSE nem agendamento no backend.
- 2026-11-27: Incluí as portas 5173 e 5174 em `safealert.cors.origens` porque a porta padrão do Vite já estava ocupada por outro projeto neste ambiente.
- 2026-11-27: A checagem visual foi feita com Chromium headless conectado por CDP (o `puppeteer.launch` padrão não iniciava neste ambiente); os artefatos ficaram em `/tmp/opencode/visual`, fora do repositório.
- 2026-11-27: Ajustei os links de navegação para `inline-flex` e os controles do Leaflet por CSS para garantir alvos de toque de pelo menos 44 px, conforme o critério de acessibilidade do painel.

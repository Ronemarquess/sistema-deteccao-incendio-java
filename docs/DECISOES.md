# Decisões registradas

Uma linha por decisão tomada diante de ambiguidades do prompt.

- 2026-11-27: O prompt é a fonte da verdade do produto: o backend será migrado para `br.com.estacio.incendio.*` e o frontend será criado do zero, pois não havia frontend nem pacotes `br.com.estacio.incendio` no repositório.
- 2026-11-27: Mantive o Spring Boot 4.1.2 já presente no `pom.xml` (não fiz downgrade para 3.5.6) e o `spring-boot-starter-webmvc`; o dialeto SQLite do Hibernate 7 foi confirmado por teste de inicialização.
- 2026-11-27: Preservei os arquivos modificados/untracked preexistentes como referência e corrigi apenas o necessário para o baseline compilar.
- 2026-11-27: `Dockerfile`, `docker-compose.yml` e `selfalert-docker.db` serão documentados como legado fora do escopo, sem remoção.
- 2026-11-27: Os assets `safe-alert-logo.png` e `safe-alert-icon.png` serão referenciados por nome; se não forem copiados, haverá fallback textual no frontend.
- 2026-11-27: Chamados históricos fechados são criados em `NOVO`, recebem sensores e só então recebem o status final, para respeitar a regra de que chamados fechados não aceitam novos sensores.
- 2026-11-27: Em chamados já existentes, a elevação de temperatura usa baseline fixo de 25 °C; em chamados novos, usa a média das últimas 5 leituras NORMAL do sensor (ou 25 °C se não houver).
- 2026-11-27: Testes automatizados usam SQLite em memória compartilhada, separado do arquivo `safe-alert.db` de desenvolvimento.

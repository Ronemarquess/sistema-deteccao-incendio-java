# Sistema de Detecção Precoce de Incêndios IoT

🔗 https://frontend-incendio.vercel.app/

Trabalho acadêmico da **Estácio**, desenvolvido para a disciplina do professor **Luiz Santos** por **Rone Marques e Nicolas**.

O projeto propõe um sistema de monitoramento de riscos de incêndio com sensores IoT, um backend Java e um painel web. O backend inclui a API de bombeiros e endpoints de consulta para o painel; a integração com sensores físicos e o processamento de alertas permanecem no backlog.

## Estado atual

### O que já funciona

- **Backend:** aplicação Spring Boot 3.5.6 com Java 17+, executável pelo Maven Wrapper incluído.
- **Modelos Java:** `Sensor`, `LeituraSensor` e `Bombeiro` têm mapeamento JPA. A API de bombeiros oferece cadastro, login com senhas BCrypt, consulta, atualização e desativação lógica.
- **Banco de dados:** H2 em memória com carga inicial de um bombeiro, sensores e leituras para apresentação. Os dados não são persistidos após o encerramento da aplicação.
- **Painel web:** interface React + TypeScript com visualização de sensores, leituras de demonstração, indicadores e estados de ocorrência.
- **Demonstração local:** permite simular uma ocorrência e a sua extinção na interface, com efeitos visuais e alerta sonoro no navegador. A simulação não aciona equipes reais.
- **Monitoramento visual:** inclui uma câmera ilustrativa e uma confirmação Sim/Não antes de ativar o alerta demonstrativo. Não há webcam conectada nem detecção real por IA.
- **Idiomas:** o painel oferece Português, Inglês, Espanhol, Francês e Alemão.
- **Consulta da API:** o frontend consulta `/api/sensores` e `/api/leituras` em `http://localhost:8080` a cada 15 segundos. A base pode ser configurada por `VITE_API_URL` no ambiente do frontend. Em falhas de rede, o frontend usa um modo de demonstração local com login, cadastro, sensores e leituras persistidos no navegador.

### O que ainda não está implementado

- Endpoints para receber leituras de sensores e avaliar condições de risco.
- Regras de negócio para avaliar leituras, gerar alertas e definir prioridades.
- Testes automatizados.
- Integração com sensores físicos, firmware ESP32 e comunicação IoT.
- Autorização baseada em sessão/token e persistência durável.
- Atualizações em tempo real por WebSockets, STOMP ou SSE.

O painel é um protótipo acadêmico. Seus dados de demonstração e simulações não representam alertas reais, e o sistema não substitui equipamentos ou serviços certificados de segurança.

## Próximos passos

1. Confirmar com a equipe e a orientação os requisitos, os sensores e o fluxo de comunicação IoT.
2. Implementar a API e as regras de aplicação para registrar leituras e consultar sensores e alertas.
3. Criar testes automatizados para as validações, regras de alerta, persistência e fluxos principais.
4. Definir e implementar a comunicação com o hardware, incluindo firmware e tratamento de falhas de rede.
5. Avaliar uma solução de persistência durável e os requisitos de segurança da aplicação.
6. Atualizar diagramas, evidências e roteiro de demonstração conforme as funcionalidades forem implementadas.

## Estrutura do repositório

```text
.
├── docs/
│   └── TAREFAS.md
├── frontend-incendio/
│   ├── public/
│   └── src/
│       ├── assets/
│       ├── components/
│       ├── services/
│       └── types/
├── pom.xml
└── src/
    └── main/
        └── java/br/com/estacio/incendio/
            ├── IncendioApplication.java
            ├── config/
            ├── controller/
            ├── dto/
            ├── model/
            ├── repository/
            └── service/
```

## Tecnologias

- **Backend:** Java 17, Spring Boot, Spring Web, Spring Data JPA, Jakarta Validation e H2.
- **Frontend:** React, TypeScript, Vite, Axios, i18next, Tailwind CSS e Framer Motion.
- **Build do backend:** Maven.

## Como executar localmente

### Backend

Com um JDK 17 ou superior instalado, execute na raiz do repositório pelo PowerShell:

```shell
.\mvnw.cmd spring-boot:run
```

O backend inicia localmente com H2 em memória. A API de bombeiros fica disponível em `http://localhost:8080/api/bombeiros`; as consultas de sensores e leituras usadas pelo painel ficam em `/api/sensores` e `/api/leituras`.

O banco vazio é preenchido com o bombeiro `rone@bombeiros.se.gov.br` / `123456`, além de três sensores e três leituras. A senha do seed é armazenada com BCrypt. O endpoint de login valida credenciais, mas ainda não emite token nem protege os demais endpoints.

Para compilar o back-end:

```shell
.\mvnw.cmd clean compile
```

### Frontend

Requer Node.js 20.19+ ou 22.12+ e npm. Em outro terminal:

```shell
cd frontend-incendio
npm install
npm run dev
```

Depois de instalar as dependências uma vez, também é possível iniciar o front-end com duplo clique em `iniciar-frontend.bat`, na raiz do projeto.

O Vite informa o endereço local do painel, normalmente `http://localhost:5173`.

Para mais detalhes sobre o frontend, consulte [`frontend-incendio/README.md`](frontend-incendio/README.md).

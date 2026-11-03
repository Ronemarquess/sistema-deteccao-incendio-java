# Sistema de Detecção Precoce de Incêndios IoT

🔗 https://frontend-incendio.vercel.app/

Trabalho acadêmico da **Estácio**, desenvolvido para a disciplina do professor **Luiz Santos** por **Rone Marques e Nicolas**.

O projeto propõe um sistema de monitoramento de riscos de incêndio com sensores IoT, um backend Java e um painel web. O repositório contém uma base inicial do backend e um protótipo funcional de interface; a integração completa entre essas partes ainda está no backlog.

## Estado atual

### O que já funciona

- **Backend:** aplicação Spring Boot 3.5.6 com Java 17, inicializável por Maven.
- **Modelos Java:** `Sensor` e `LeituraSensor` têm mapeamento JPA e validações básicas. `Usuario` e `Bombeiro` demonstram abstração, herança e sobrescrita de método.
- **Banco de dados:** dependência do H2 configurada para uso em memória. Os dados não são persistidos após o encerramento da aplicação.
- **Painel web:** interface React + TypeScript com visualização de sensores, leituras de demonstração, indicadores e estados de ocorrência.
- **Demonstração local:** permite simular uma ocorrência e a sua extinção na interface, com efeitos visuais e alerta sonoro no navegador. A simulação não aciona equipes reais.
- **Idiomas:** o painel oferece Português, Inglês, Espanhol, Francês e Alemão.
- **Consulta da API:** o frontend tenta consultar `/api/sensores` e `/api/leituras` em `http://localhost:8080` a cada 15 segundos. Como esses endpoints ainda não existem no backend, a interface informa a indisponibilidade da API e mantém dados locais de demonstração.

### O que ainda não está implementado

- Endpoints REST, serviços e repositórios para receber e consultar dados.
- Regras de negócio para avaliar leituras, gerar alertas e definir prioridades.
- Testes automatizados.
- Integração com sensores físicos, firmware ESP32 e comunicação IoT.
- Persistência durável, autenticação e autorização.
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
            └── model/
                ├── Bombeiro.java
                ├── LeituraSensor.java
                ├── Sensor.java
                └── Usuario.java
```

## Tecnologias

- **Backend:** Java 17, Spring Boot, Spring Web, Spring Data JPA, Jakarta Validation e H2.
- **Frontend:** React, TypeScript, Vite, Axios, i18next, Tailwind CSS e Framer Motion.
- **Build do backend:** Maven.

## Como executar localmente

### Backend

Com Java 17 e Maven instalados, execute na raiz do repositório:

```shell
mvn spring-boot:run
```

O backend inicia localmente com H2 em memória. Os endpoints usados pelo painel ainda precisam ser implementados.

### Frontend

Requer Node.js 20.19+ ou 22.12+ e npm. Em outro terminal:

```shell
cd frontend-incendio
npm install
npm run dev
```

O Vite informa o endereço local do painel, normalmente `http://localhost:5173`.

Para mais detalhes sobre o frontend, consulte [`frontend-incendio/README.md`](frontend-incendio/README.md).

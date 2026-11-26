# VigiaFire — centro de comando

Interface React + TypeScript do Sistema de Detecção Precoce de Incêndios IoT.

## Requisitos

- Node.js 20.19+ ou 22.12+ (compatível com Vite 8)
- npm

## Desenvolvimento

Na pasta `frontend-incendio`, instale as dependências e inicie o Vite:

```shell
npm install
npm run dev
```

O Vite informa o endereço local, normalmente `http://localhost:5173/`. A versão publicada está em `https://frontend-incendio.vercel.app/`.

## Verificação

```shell
npm run build
npm run lint
```

## Integração e modo de demonstração

O cliente Axios prioriza `VITE_API_URL` (base incluindo `/api`). Sem essa variável, `http://localhost:5173/` e `https://frontend-incendio.vercel.app/` consultam `http://localhost:8080/api`. Os endpoints `GET /sensores` e `GET /leituras` são tipados conforme os contratos Java; `POST /bombeiros` e `POST /bombeiros/login` também estão disponíveis por `cadastrarBombeiro` e `loginBombeiro` em `src/services/api.ts`. Para apontar o frontend a uma API publicada, configure `VITE_API_URL` nas variáveis de ambiente do Vercel; há um exemplo em `.env.example`. O backend permite CORS para `http://localhost:5173` e `https://frontend-incendio.vercel.app`. Se uma requisição falhar por erro de rede, consultas e autenticação/cadastro usam armazenamento de demonstração persistido no navegador, inicializado com o seed do backend (`rone@bombeiros.se.gov.br` / `123456`). O modo demo não mascara respostas HTTP de erro. Os botões de simulação alteram somente o estado do navegador: não enviam comandos ao backend nem acionam equipes reais.

O seletor oferece Português, Inglês, Espanhol, Francês e Alemão. As traduções adicionais podem ser incluídas nos recursos de `src/i18n.ts`.

Na área de monitoramento, a câmera é apenas uma cena ilustrativa, sem webcam, vídeo ao vivo ou inferência de IA. A análise demonstrativa abre uma confirmação: **Sim** ativa o alerta visual; **Não** descarta a ocorrência simulada. Nenhuma opção aciona equipes reais ou hardware.

## Estilos e movimento

Tailwind CSS v4 está integrado pelo plugin `@tailwindcss/vite`. A interface também utiliza CSS próprio para identidade visual e responsividade, e Framer Motion para os efeitos de chama e extinção.

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

O Vite informa o endereço local, normalmente `http://localhost:5173`.

## Verificação

```shell
npm run build
npm run lint
```

## Integração e modo de demonstração

O cliente Axios usa `http://localhost:8080/api` como base e consulta `/sensores` e `/leituras`. O backend ainda não implementa esses endpoints; quando a API não responde, a tela informa a indisponibilidade e mantém dados locais de demonstração. Os botões de simulação alteram somente o estado do navegador: não enviam comandos ao backend nem acionam equipes reais.

O seletor oferece Português, Inglês, Espanhol, Francês e Alemão. As traduções adicionais podem ser incluídas nos recursos de `src/i18n.ts`.

## Estilos e movimento

Tailwind CSS v4 está integrado pelo plugin `@tailwindcss/vite`. A interface também utiliza CSS próprio para identidade visual e responsividade, e Framer Motion para os efeitos de chama e extinção.

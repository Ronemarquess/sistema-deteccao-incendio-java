import { Outlet } from 'react-router-dom';
import { Header } from './Header';

export function Layout() {
  return (
    <div className="flex min-h-screen flex-col bg-fundo text-white">
      <Header />
      <main className="mx-auto w-full max-w-7xl flex-1 px-4 pb-28 pt-6 md:px-6 md:pb-10">
        <Outlet />
      </main>
      <footer className="border-t border-linha px-4 py-4 pb-24 text-center text-xs text-suave md:pb-4">
        Ambiente simulado para fins acadêmicos. Os dados e alertas não são reais e o sistema não substitui
        equipamentos ou serviços certificados de segurança.
      </footer>
    </div>
  );
}

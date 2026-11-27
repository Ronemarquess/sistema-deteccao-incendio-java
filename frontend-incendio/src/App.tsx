import type { ReactNode } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { Layout } from './components/Layout';
import { useAuth } from './context/AuthContext';
import { ChamadoDetalhe } from './pages/ChamadoDetalhe';
import { Chamados } from './pages/Chamados';
import { Login } from './pages/Login';
import { Sensores } from './pages/Sensores';
import { Simulador } from './pages/Simulador';

function RotaProtegida({ children }: { children: ReactNode }) {
  const { bombeiro } = useAuth();
  return bombeiro ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route element={<RotaProtegida><Layout /></RotaProtegida>}>
        <Route path="/chamados" element={<Chamados />} />
        <Route path="/chamados/:id" element={<ChamadoDetalhe />} />
        <Route path="/sensores" element={<Sensores />} />
        <Route path="/simulador" element={<Simulador />} />
      </Route>
      <Route path="*" element={<Navigate to="/chamados" replace />} />
    </Routes>
  );
}

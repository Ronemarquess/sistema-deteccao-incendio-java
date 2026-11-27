import { MapContainer, Marker, Popup, TileLayer } from 'react-leaflet';
import * as L from 'leaflet';
import { MapPin } from 'lucide-react';

const icone = L.divIcon({
  className: 'safe-alert-marker',
  html: '<div style="width:32px;height:32px;border-radius:50% 50% 50% 0;transform:rotate(-45deg);background:#ed5533;border:3px solid #f1f7da;display:flex;align-items:center;justify-content:center;box-shadow:0 2px 8px rgba(0,0,0,.4)"><span style="transform:rotate(45deg);color:#fff;font-size:16px">🔥</span></div>',
  iconSize: [32, 32],
  iconAnchor: [16, 32],
  popupAnchor: [0, -28],
});

interface MapaProps {
  latitude: number | null;
  longitude: number | null;
  local: string;
  altura?: string;
}

export function Mapa({ latitude, longitude, local, altura = 'h-[220px] md:h-[360px]' }: MapaProps) {
  if (latitude == null || longitude == null) {
    return (
      <div className={`flex ${altura} items-center justify-center rounded-xl bg-linha text-suave`}>
        <MapPin className="mr-2" size={18} aria-hidden /> Coordenadas indisponíveis
      </div>
    );
  }

  return (
    <MapContainer
      center={[latitude, longitude]}
      zoom={16}
      scrollWheelZoom={false}
      className={`w-full ${altura} rounded-xl`}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Marker position={[latitude, longitude]} icon={icone}>
        <Popup>{local}</Popup>
      </Marker>
    </MapContainer>
  );
}

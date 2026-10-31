import { useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { LazyMotion, MotionConfig, domAnimation, m, useScroll, useTransform } from 'framer-motion'
import { AppIcon, type IconName } from './components/icons'
import { getLeituras, getSensores } from './services/api'
import type { LeituraSensor, Sensor } from './types'
import './App.css'

const initialSensors: Sensor[] = [
  { id: 1, tipo: 'Temperatura', localizacao: 'Setor Norte · Torre 01' },
  { id: 2, tipo: 'Fumaça', localizacao: 'Setor Norte · Torre 02' },
  { id: 3, tipo: 'Umidade', localizacao: 'Setor Sul · Torre 04' },
]

const initialReadings: LeituraSensor[] = [
  {
    id: 3,
    valor: 26.4,
    instanteMedicao: new Date(Date.now() - 3 * 60_000).toISOString(),
    sensor: initialSensors[0],
  },
  {
    id: 2,
    valor: 24.8,
    instanteMedicao: new Date(Date.now() - 8 * 60_000).toISOString(),
    sensor: initialSensors[0],
  },
  {
    id: 1,
    valor: 23.1,
    instanteMedicao: new Date(Date.now() - 13 * 60_000).toISOString(),
    sensor: initialSensors[0],
  },
]

const languages = [
  { code: 'pt', label: '🇧🇷 PT' },
  { code: 'en', label: '🇺🇸 EN' },
  { code: 'es', label: '🇪🇸 ES' },
  { code: 'fr', label: '🇫🇷 FR' },
  { code: 'de', label: '🇩🇪 DE' },
]

function playAlarm() {
  const AudioContextConstructor = window.AudioContext
  if (!AudioContextConstructor) return

  const context = new AudioContextConstructor()
  void context.resume().then(() => {
    ;[0, 0.28, 0.56].forEach((delay) => {
      const oscillator = context.createOscillator()
      const volume = context.createGain()
      oscillator.type = 'sawtooth'
      oscillator.frequency.value = 740
      volume.gain.setValueAtTime(0.0001, context.currentTime + delay)
      volume.gain.exponentialRampToValueAtTime(0.14, context.currentTime + delay + 0.025)
      volume.gain.exponentialRampToValueAtTime(0.0001, context.currentTime + delay + 0.2)
      oscillator.connect(volume)
      volume.connect(context.destination)
      oscillator.start(context.currentTime + delay)
      oscillator.stop(context.currentTime + delay + 0.22)
    })
    window.setTimeout(() => void context.close(), 1100)
  })
}

function formatTime(value: string) {
  return new Intl.DateTimeFormat(undefined, {
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

function App() {
  const { t, i18n } = useTranslation()
  const [temperature, setTemperature] = useState(26.4)
  const [isFireActive, setIsFireActive] = useState(false)
  const [isExtinguishing, setIsExtinguishing] = useState(false)
  const [sensors, setSensors] = useState(initialSensors)
  const [readings, setReadings] = useState(initialReadings)
  const [apiError, setApiError] = useState<string | null>(null)
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null)
  const extinguishTimer = useRef<number | undefined>(undefined)
  const { scrollYProgress } = useScroll()
  const flameOpacity = useTransform(scrollYProgress, [0, 0.45], [1, 0.4])
  const flameScale = useTransform(scrollYProgress, [0, 0.45], [1, 0.72])

  useEffect(() => {
    document.documentElement.lang = i18n.language === 'pt' ? 'pt-BR' : i18n.language
  }, [i18n.language])

  useEffect(() => {
    const controller = new AbortController()

    const refreshData = async () => {
      try {
        const [sensorData, readingData] = await Promise.all([
          getSensores(controller.signal),
          getLeituras(controller.signal),
        ])
        setSensors(sensorData)
        setReadings(readingData)
        setApiError(null)
        setLastUpdated(new Date())
      } catch (error) {
        if (controller.signal.aborted) return
        setLastUpdated(new Date())
        setApiError(
          error instanceof Error ? error.message : t('api.connectionError'),
        )
      }
    }

    void refreshData()
    const intervalId = window.setInterval(() => void refreshData(), 15_000)
    return () => {
      controller.abort()
      window.clearInterval(intervalId)
      window.clearTimeout(extinguishTimer.current)
    }
  }, [t])

  const simulateFire = () => {
    const newTemperature = Math.floor(Math.random() * 11) + 85
    const newReading: LeituraSensor = {
      id: Date.now(),
      valor: newTemperature,
      instanteMedicao: new Date().toISOString(),
      sensor: sensors[0] ?? initialSensors[0],
    }

    setTemperature(newTemperature)
    setReadings((current) => [newReading, ...current].slice(0, 5))
    setIsFireActive(true)
    setIsExtinguishing(false)
    playAlarm()
  }

  const extinguishFire = () => {
    const safeTemperature = Number((22 + Math.random() * 5).toFixed(1))
    setTemperature(safeTemperature)
    setReadings((current) =>
      [
        {
          id: Date.now(),
          valor: safeTemperature,
          instanteMedicao: new Date().toISOString(),
          sensor: sensors[0] ?? initialSensors[0],
        },
        ...current,
      ].slice(0, 5),
    )
    setIsFireActive(false)
    setIsExtinguishing(true)
    window.clearTimeout(extinguishTimer.current)
    extinguishTimer.current = window.setTimeout(() => setIsExtinguishing(false), 2200)
  }

  const sensorValue = (sensor: Sensor) => {
    if (sensor.tipo.toLowerCase().includes('fuma')) {
      return isFireActive ? t('sensor.detected') : t('sensor.normal')
    }
    if (sensor.tipo.toLowerCase().includes('umid')) {
      return isFireActive ? '31%' : '58%'
    }
    return `${temperature.toFixed(1)}°C`
  }

  return (
    <MotionConfig reducedMotion="user">
      <LazyMotion features={domAnimation}>
      <div className={`command-center${isFireActive ? ' is-alarming' : ''}`}>
        <header className="topbar">
          <a className="brand" href="#inicio" aria-label={t('header.home')}>
            <span className="brand-mark"><AppIcon name="shield" /></span>
            <span className="brand-copy">
              <strong>VIGIA<span>FIRE</span></strong>
              <small>{t('header.system')}</small>
            </span>
          </a>

          <div className="topbar-center">
            <span className="connection-dot" />
            <span>{t('header.commandCenter')}</span>
            <span className="topbar-divider" />
            <span className="muted">{t('header.liveMonitoring')}</span>
          </div>

          <div className="topbar-actions">
            <span className="shift-label"><span className="status-dot" />{t('header.onDuty')}</span>
            <label className="language-select">
              <span className="sr-only">{t('header.language')}</span>
              <select
                value={i18n.resolvedLanguage?.split('-')[0] ?? 'pt'}
                onChange={(event) => void i18n.changeLanguage(event.target.value)}
              >
                {languages.map((language) => (
                  <option value={language.code} key={language.code}>{language.label}</option>
                ))}
              </select>
              <AppIcon name="chevron-down" size={13} />
            </label>
            <button className="icon-button notification-button" type="button" aria-label={t('header.notifications')}>
              <AppIcon name="bell" size={17} />
              {isFireActive && <span className="notification-dot" />}
            </button>
            <div className="avatar" aria-label={t('header.operator')}>CM</div>
          </div>
        </header>

        <main id="inicio" className="page-shell">
          <section className="page-heading">
            <div>
              <div className="eyebrow"><span className="eyebrow-line" />{t('heading.kicker')}</div>
              <h1>{t('heading.title')} <span>{t('heading.titleAccent')}</span></h1>
              <p>{t('heading.subtitle')}</p>
            </div>
            <div className="heading-meta">
              <div className="update-time"><AppIcon name="clock" size={14} /><span>{t('heading.updated')} {lastUpdated?.toLocaleTimeString(i18n.language, { hour: '2-digit', minute: '2-digit' }) ?? '--:--'}</span></div>
              <div className="location-pill"><AppIcon name="map-pin" size={14} />{t('heading.location')}</div>
            </div>
          </section>

          {apiError && (
            <div className="api-notice" role="status">
              <AppIcon name="cloud-off" size={17} />
              <div><strong>{t('api.offline')}</strong><span>{t('api.offlineDetail')} — {apiError}</span></div>
              <span className="demo-tag">{t('api.demoMode')}</span>
            </div>
          )}

          <section className={`incident-banner${isFireActive ? ' incident-banner-active' : ''}`} aria-live="polite">
            <div className="incident-icon"><AppIcon name={isFireActive ? 'siren' : 'shield-check'} size={20} /></div>
            <div className="incident-copy">
              <strong>{isFireActive ? t('incident.activeTitle') : t('incident.safeTitle')}</strong>
              <span>{isFireActive ? t('incident.activeDescription') : t('incident.safeDescription')}</span>
            </div>
            <div className={`incident-status${isFireActive ? ' status-danger' : ''}`}>
              <span className="status-dot" />{isFireActive ? t('incident.activeBadge') : t('incident.safeBadge')}
            </div>
            <span className="incident-reference">{isFireActive ? 'INC-2026-0842' : t('incident.noIncidents')}</span>
          </section>

          <section className="dashboard-grid">
            <article className={`fire-panel glass-panel${isFireActive ? ' fire-panel-active' : ''}${isExtinguishing ? ' fire-panel-extinguishing' : ''}`}>
              <div className="panel-heading">
                <div>
                  <div className="panel-overline">{t('firePanel.overline')}</div>
                  <h2>{t('firePanel.title')}</h2>
                </div>
                <span className={`live-badge${isFireActive ? ' live-badge-danger' : ''}`}><span />{isFireActive ? t('firePanel.alert') : t('firePanel.monitoring')}</span>
              </div>

              <div className="fire-scene">
                <div className="scene-grid" />
                <div className="scene-rings"><i /><i /><i /></div>
                <m.div className="flame-aura" style={{ opacity: flameOpacity, scale: flameScale }} />
                <m.div className="flame-symbol" style={{ opacity: flameOpacity, scale: flameScale }}>
                  <AppIcon name="flame" size={104} strokeWidth={1.1} />
                  <span>{isFireActive ? '!' : '✓'}</span>
                </m.div>
                {isExtinguishing && (
                  <m.div className="water-effect" initial={{ opacity: 0, scale: 0.2 }} animate={{ opacity: [0, 1, 0], scale: [0.2, 1.5, 2.2] }} transition={{ duration: 1.8 }}>
                    <AppIcon name="waves" size={116} strokeWidth={0.9} />
                  </m.div>
                )}
                <div className="scene-coordinate">23°32&apos; S &nbsp; 46°38&apos; W</div>
                <div className="scene-label"><AppIcon name="map-pin" size={12} />{t('firePanel.zone')}</div>
                {isFireActive && <div className="scene-alert-pulse"><span />{t('firePanel.fireDetected')}</div>}
              </div>

              <div className="temperature-reading">
                <div className="temperature-icon"><AppIcon name="thermometer" size={19} /></div>
                <div>
                  <div className="reading-label">{t('firePanel.currentTemperature')}</div>
                  <div className={`reading-value${isFireActive ? ' reading-danger' : ''}`}>{temperature.toFixed(1)}<span>°C</span></div>
                </div>
                <div className={`reading-trend${isFireActive ? ' trend-danger' : ''}`}>
                  <AppIcon name={isFireActive ? 'arrow-up-right' : 'arrow-down-right'} size={15} />
                  <span>{isFireActive ? `+${(temperature - 26.4).toFixed(1)}°` : t('firePanel.safeRange')}</span>
                </div>
              </div>

              <div className="temperature-meter" aria-label={t('firePanel.temperatureScale')}>
                <div className={`temperature-meter-fill${isFireActive ? ' meter-danger' : ''}`} style={{ width: `${Math.min(temperature, 100)}%` }} />
                <span className="meter-threshold" />
              </div>
              <div className="meter-labels"><span>0°C</span><span>{t('firePanel.warningThreshold')}</span><span>100°C</span></div>

              <div className="response-actions">
                <button className="action-button action-simulate" type="button" onClick={simulateFire}>
                  <AppIcon name="flame" size={16} />{t('firePanel.simulate')}
                </button>
                <button className="action-button action-dispatch" type="button" onClick={extinguishFire} disabled={!isFireActive}>
                  <AppIcon name={isFireActive ? 'waves' : 'check'} size={16} />
                  {isFireActive ? t('firePanel.extinguish') : t('firePanel.controlled')}
                </button>
              </div>
              <p className="simulation-note">{t('firePanel.demoNote')}</p>
            </article>

            <div className="side-column">
              <article className="sensors-panel glass-panel">
                <div className="panel-heading compact-heading">
                  <div><div className="panel-overline">{t('sensors.overline')}</div><h2>{t('sensors.title')}</h2></div>
                  <span className="sensor-count"><AppIcon name="radio" size={13} />{sensors.length} {t('sensors.active')}</span>
                </div>
                <div className="sensor-list">
                  {sensors.map((sensor, index) => {
                    const isTemperatureSensor = sensor.tipo.toLowerCase().includes('temperatura')
                    const isSmokeSensor = sensor.tipo.toLowerCase().includes('fuma')
                    const iconName: IconName = isTemperatureSensor ? 'thermometer' : isSmokeSensor ? 'wind' : 'activity'
                    return (
                      <div className="sensor-row" key={sensor.id}>
                        <div className={`sensor-icon sensor-icon-${index % 3}`}><AppIcon name={iconName} size={16} /></div>
                        <div className="sensor-info"><strong>{sensor.tipo}</strong><span>{sensor.localizacao}</span></div>
                        <div className="sensor-reading"><strong className={isFireActive && isTemperatureSensor ? 'text-danger' : ''}>{sensorValue(sensor)}</strong><span><i />{t('sensors.online')}</span></div>
                      </div>
                    )
                  })}
                  {sensors.length === 0 && <p className="empty-state">{t('sensors.empty')}</p>}
                </div>
                <div className="sensor-footer"><span><span className="tiny-dot" />{t('sensors.allOnline')}</span><span>{t('sensors.syncedNow')}</span></div>
              </article>

              <article className="readings-panel glass-panel">
                <div className="panel-heading compact-heading">
                  <div><div className="panel-overline">{t('readings.overline')}</div><h2>{t('readings.title')}</h2></div>
                  <span className="readings-live"><span />{t('readings.live')}</span>
                </div>
                <div className="readings-list">
                  {readings.slice(0, 3).map((reading, index) => (
                    <div className="reading-row" key={reading.id}>
                      <span className={`reading-marker${index === 0 && isFireActive ? ' marker-danger' : ''}`} />
                      <div className="reading-detail"><strong>{reading.sensor?.tipo ?? t('readings.sensor')}</strong><span>{reading.sensor?.localizacao ?? t('readings.unknownLocation')}</span></div>
                      <strong className={`reading-row-value${index === 0 && isFireActive ? ' text-danger' : ''}`}>{Number(reading.valor).toFixed(1)}°</strong>
                      <time>{formatTime(reading.instanteMedicao)}</time>
                    </div>
                  ))}
                  {readings.length === 0 && <p className="empty-state">{t('readings.empty')}</p>}
                </div>
              </article>
            </div>
          </section>

          <section className="metrics-grid" aria-label={t('metrics.title')}>
            <article className="metric-card glass-panel">
              <span className="metric-icon metric-icon-red"><AppIcon name="flame" size={17} /></span>
              <div className="metric-label">{t('metrics.incidents')}</div><strong>{isFireActive ? '01' : '00'}<small> {t('metrics.today')}</small></strong>
              <span className="metric-foot">{isFireActive ? t('metrics.simulated') : t('metrics.noOccurrences')}</span>
            </article>
            <article className="metric-card glass-panel">
              <span className="metric-icon metric-icon-green"><AppIcon name="shield-check" size={17} /></span>
              <div className="metric-label">{t('metrics.protectedArea')}</div><strong>2.480<small> ha</small></strong>
              <span className="metric-foot">{t('metrics.coveredArea')}</span>
            </article>
            <article className="metric-card glass-panel">
              <span className="metric-icon metric-icon-amber"><AppIcon name="radio" size={17} /></span>
              <div className="metric-label">{t('metrics.connectedSensors')}</div><strong>{sensors.length}<small> / 24</small></strong>
              <span className="metric-foot">{apiError ? t('metrics.demoData') : t('metrics.networkOk')}</span>
            </article>
            <article className="metric-card glass-panel">
              <span className="metric-icon metric-icon-blue"><AppIcon name="activity" size={17} /></span>
              <div className="metric-label">{t('metrics.responseTime')}</div><strong>04<small> min</small></strong>
              <span className="metric-foot">{t('metrics.averageResponse')}</span>
            </article>
          </section>

          <footer className="page-footer">
            <span><AppIcon name="shield" size={13} />VIGIAFIRE <span className="footer-divider">/</span> {t('footer.system')}</span>
            <span>{t('footer.disclaimer')}</span>
            <span><span className="footer-live-dot" />{t('footer.heartbeat')}</span>
          </footer>
        </main>
      </div>
      </LazyMotion>
    </MotionConfig>
  )
}

export default App

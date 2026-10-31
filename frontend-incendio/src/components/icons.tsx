import type { ReactNode, SVGProps } from 'react'

export type IconProps = Omit<SVGProps<SVGSVGElement>, 'width' | 'height'> & {
  size?: number
  strokeWidth?: number
}

export type IconName =
  | 'activity'
  | 'arrow-down-right'
  | 'arrow-up-right'
  | 'bell'
  | 'check'
  | 'chevron-down'
  | 'clock'
  | 'cloud-off'
  | 'flame'
  | 'map-pin'
  | 'radio'
  | 'shield'
  | 'shield-check'
  | 'siren'
  | 'thermometer'
  | 'waves'
  | 'wind'

const glyphs: Record<IconName, ReactNode> = {
  activity: <><path d="M22 12h-4l-3 9L9 3l-3 9H2" /></>,
  'arrow-down-right': <><path d="M7 7 17 17" /><path d="M17 7v10H7" /></>,
  'arrow-up-right': <><path d="M7 17 17 7" /><path d="M7 7h10v10" /></>,
  bell: <><path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9" /><path d="M10 21h4" /></>,
  check: <><path d="m5 12 4 4L19 6" /></>,
  'chevron-down': <><path d="m6 9 6 6 6-6" /></>,
  clock: <><circle cx="12" cy="12" r="10" /><path d="M12 6v6l4 2" /></>,
  'cloud-off': <><path d="m2 2 20 20" /><path d="M5.2 5.2A7 7 0 0 0 5 19h12" /><path d="M10.6 3.2A7 7 0 0 1 19 10a5 5 0 0 1 1.8 9" /></>,
  flame: <><path d="M8.5 14.5A3.5 3.5 0 0 0 12 18a3.5 3.5 0 0 0 3.5-3.5c0-2-1.5-3-2.5-4.5-.5 2-2 2.5-2 2.5 0-4-3-5-3-9-3 3-5 6-5 10a9 9 0 0 0 18 0c0-3-1.5-6-4-8 .5 4-1 6-1 6" /></>,
  'map-pin': <><path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z" /><circle cx="12" cy="10" r="2.5" /></>,
  radio: <><path d="M4.9 19.1a10 10 0 0 1 0-14.2" /><path d="M8.5 15.5a5 5 0 0 1 0-7" /><circle cx="12" cy="12" r="2" /><path d="M15.5 8.5a5 5 0 0 1 0 7" /><path d="M19.1 4.9a10 10 0 0 1 0 14.2" /></>,
  shield: <><path d="M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11Z" /></>,
  'shield-check': <><path d="M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11Z" /><path d="m9 12 2 2 4-4" /></>,
  siren: <><path d="M18 8a6 6 0 0 0-12 0v5H4v4h16v-4h-2Z" /><path d="M10 21h4" /><path d="M12 2V1M4.2 4.2 3.5 3.5m17 0-.7.7" /></>,
  thermometer: <><path d="M14 14.8V5a3 3 0 0 0-6 0v9.8a5 5 0 1 0 6 0Z" /><path d="M11 11v7" /></>,
  waves: <><path d="M2 6c2.5 0 2.5 2 5 2s2.5-2 5-2 2.5 2 5 2 2.5-2 5-2" /><path d="M2 12c2.5 0 2.5 2 5 2s2.5-2 5-2 2.5 2 5 2 2.5-2 5-2" /><path d="M2 18c2.5 0 2.5 2 5 2s2.5-2 5-2 2.5 2 5 2 2.5-2 5-2" /></>,
  wind: <><path d="M3 8h12a3 3 0 1 0-3-3" /><path d="M2 12h17a3 3 0 1 1-3 3" /><path d="M4 16h6a3 3 0 1 1-3 3" /></>,
}

export function AppIcon({ name, size = 18, strokeWidth = 1.8, ...props }: IconProps & { name: IconName }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={strokeWidth}
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
      {...props}
    >
      {glyphs[name]}
    </svg>
  )
}

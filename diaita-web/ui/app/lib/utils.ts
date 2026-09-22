import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatOptionLabel(value: string) {
  const label = value
    .trim()
    .replace(/(\d)_+(\d)/g, '$1–$2')
    .replace(/[_-]+/g, ' ')

  return label ? label.charAt(0).toUpperCase() + label.slice(1) : ''
}

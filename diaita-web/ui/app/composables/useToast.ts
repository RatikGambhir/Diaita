import { ref } from 'vue'

export interface Toast {
  id: number
  title: string
  description?: string
  color?: 'primary' | 'success' | 'error' | 'warning' | 'info'
  timeout?: number
}

const toasts = ref<Toast[]>([])
let toastId = 0

export function useToast() {
  const add = (toast: Omit<Toast, 'id'>) => {
    const id = ++toastId
    const newToast: Toast = {
      id,
      ...toast,
      timeout: toast.timeout ?? 5000
    }
    toasts.value.push(newToast)

    // Auto remove after timeout
    if (newToast.timeout > 0) {
      setTimeout(() => {
        remove(id)
      }, newToast.timeout)
    }

    // Also log to console for debugging
    console.log(`[Toast] ${toast.title}: ${toast.description || ''}`)

    return id
  }

  const remove = (id: number) => {
    const index = toasts.value.findIndex(t => t.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }

  const clear = () => {
    toasts.value = []
  }

  return {
    toasts,
    add,
    remove,
    clear
  }
}

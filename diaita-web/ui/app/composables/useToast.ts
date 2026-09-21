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
    const timeout = toast.timeout ?? 5000
    const newToast: Toast = {
      id,
      ...toast,
      timeout
    }
    toasts.value.push(newToast)

    // Auto remove after timeout
    if (timeout > 0) {
      setTimeout(() => {
        remove(id)
      }, timeout)
    }

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

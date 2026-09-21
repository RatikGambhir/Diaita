import { useUserStore } from "~/stores/useUserStore"


const PUBLIC_ROUTES = ['/landing', '/login', '/register']

export default defineNuxtRouteMiddleware(async (to) => {
  if (import.meta.server) {
    return
  }

  const isPublicRoute = PUBLIC_ROUTES.some(route => to.path.startsWith(route))

  const userStore = useUserStore()
  const authenticated = userStore.isAuthenticated

  // Redirect unauthenticated users away from protected routes
  if (!isPublicRoute && !authenticated) {
    if (to.fullPath !== '/landing') {
      return navigateTo({
        path: '/landing',
      })
    }
  }

  if (authenticated && ['/login', '/register'].includes(to.path)) {
    return navigateTo('/')
  }

})

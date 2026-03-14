import { useUserStore } from "~/stores/useUserStore"


const PUBLIC_ROUTES = ['/landing', '/login', '/register', '/verify-email']

export default defineNuxtRouteMiddleware(async (to) => {
  if (import.meta.server) {
    return
  }

  const isPublicRoute = PUBLIC_ROUTES.some(route => to.path.startsWith(route))

  const userStore = useUserStore()
  const user = userStore.getUser

  // Redirect unauthenticated users away from protected routes
  if (!isPublicRoute && !user) {
    if (to.fullPath !== '/landing') {
      return navigateTo({
        path: '/landing',
      })
    }
  }

  // Redirect authenticated users away from auth pages (except landing)
  // if (isPublicRoute && session && to.path !== '/landing') {
  //   return navigateTo('/')
  // }

})

import { useUserStore } from "~/stores/useUserStore"


const PUBLIC_ROUTES = ['/landing', '/login', '/register', '/verify-email']

export default defineNuxtRouteMiddleware(async (to) => {
  const isPublicRoute = PUBLIC_ROUTES.some(route => to.path.startsWith(route))

  const session = useUserStore().getSession
console.log("SESSION", session)
  // Redirect unauthenticated users away from protected routes
  if (!isPublicRoute && !session) {
if (to.fullPath !== '/landing') {
  return navigateTo({
    path: '/landing',
  })
}
  }

  // Redirect authenticated users away from auth pages (except landing)
  if (isPublicRoute && session && to.path !== '/landing') {
    return navigateTo('/')
  }

})

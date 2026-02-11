import { supabase } from '~/utils'

const PUBLIC_ROUTES = ['/landing', '/login', '/register', '/verify-email']

export default defineNuxtRouteMiddleware(async (to) => {
  const isPublicRoute = PUBLIC_ROUTES.some(route => to.path.startsWith(route))

  const { data: { session } } = await supabase.auth.getSession()
console.log("SESSION", session)
  // Redirect unauthenticated users away from protected routes
  if (!isPublicRoute && !session) {
  //  const intendedPath = to.fullPath !== '/' ? to.fullPath : undefined
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

  // Check profile completion for authenticated users on protected routes
  if (!isPublicRoute && session) {
    console.log("HAS SESSION AND NON PUBLIC ROUTE")
    const userStore = useUserStore()

    // Sync session into store if not already present
    if (!userStore.session || Object.keys(userStore.session).length === 0) {
      userStore.addUserSession(session.user, session)
    }


  }
})

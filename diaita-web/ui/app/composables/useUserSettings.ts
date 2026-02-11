export interface UserSettings {
  profile: {
    name: string
    email: string
    bio?: string
  }
  preferences: {
    theme: 'light' | 'dark' | 'system'
    language: string
    timezone: string
  }
  workout: {
    units: 'metric' | 'imperial'
    defaultView: 'daily' | 'weekly' | 'monthly'
    restTimerSound: boolean
    autoStartRest: boolean
  }
  notifications: {
    email: boolean
    push: boolean
    workoutReminders: boolean
    weeklyReport: boolean
    achievementAlerts: boolean
  }
  privacy: {
    profileVisibility: 'public' | 'friends' | 'private'
    showWorkouts: boolean
    showStats: boolean
  }
}

const defaultSettings: UserSettings = {
  profile: {
    name: 'John Doe',
    email: 'john.doe@example.com',
    bio: 'Fitness enthusiast and workout tracker'
  },
  preferences: {
    theme: 'system',
    language: 'en',
    timezone: 'UTC'
  },
  workout: {
    units: 'metric',
    defaultView: 'weekly',
    restTimerSound: true,
    autoStartRest: true
  },
  notifications: {
    email: true,
    push: true,
    workoutReminders: true,
    weeklyReport: true,
    achievementAlerts: true
  },
  privacy: {
    profileVisibility: 'friends',
    showWorkouts: true,
    showStats: true
  }
}

export const useUserSettings = () => {
  // Use useCookie for SSR-safe persistence
  const settingsCookie = useCookie<UserSettings>('user-settings', {
    default: () => defaultSettings,
    maxAge: 60 * 60 * 24 * 365, // 1 year
    sameSite: 'lax'
  })

  const settings = ref<UserSettings>(settingsCookie.value || defaultSettings)

  // Watch for changes and persist to cookie
  watch(settings, (newSettings) => {
    settingsCookie.value = newSettings
  }, { deep: true })

  const updateProfile = (profile: Partial<UserSettings['profile']>) => {
    settings.value.profile = { ...settings.value.profile, ...profile }
  }

  const updatePreferences = (preferences: Partial<UserSettings['preferences']>) => {
    settings.value.preferences = { ...settings.value.preferences, ...preferences }
  }

  const updateWorkout = (workout: Partial<UserSettings['workout']>) => {
    settings.value.workout = { ...settings.value.workout, ...workout }
  }

  const updateNotifications = (notifications: Partial<UserSettings['notifications']>) => {
    settings.value.notifications = { ...settings.value.notifications, ...notifications }
  }

  const updatePrivacy = (privacy: Partial<UserSettings['privacy']>) => {
    settings.value.privacy = { ...settings.value.privacy, ...privacy }
  }

  const resetSettings = () => {
    settings.value = { ...defaultSettings }
  }

  return {
    settings,
    updateProfile,
    updatePreferences,
    updateWorkout,
    updateNotifications,
    updatePrivacy,
    resetSettings
  }
}


export type Stat = {
  title: string
  icon: string
  value: number | string
  variation: number
  formatter?: (value: number) => string
}

export type Period = 'daily' | 'weekly' | 'monthly'

export type Range = {
  start: Date
  end: Date
}

export enum ActivityLevel {
  SEDENTARY = 'sedentary',
  LIGHTLY_ACTIVE = 'lightly_active',
  MODERATELY_ACTIVE = 'moderately_active',
  VERY_ACTIVE = 'very_active',
  EXTREMELY_ACTIVE = 'extremely_active'
}

export enum PrimaryGoal {
  FAT_LOSS = 'fat_loss',
  MUSCLE_GAIN = 'muscle_gain',
  RECOMPOSITION = 'recomposition',
  STRENGTH = 'strength',
  ENDURANCE = 'endurance',
  GENERAL_HEALTH = 'general_health'
}

export type LoginCredentials = {
  email: string
  password: string
}

export type CreateProfileData = {
  email: string
  password: string
  name?: string
}

export type UserProfile = {
  userId: string
  age: number
  sex: string
  gender: string
  height: number
  weight: number
  ethnicity: string
  activityLevel: ActivityLevel
  primaryGoal: PrimaryGoal
}

export type AuthResponse = {
  token: string
  user: UserProfile
}

export type ProfileSetupFormData = {
  height: number
  weight: number
  gender: boolean
  activity_level: ActivityLevel
  goal: PrimaryGoal
}
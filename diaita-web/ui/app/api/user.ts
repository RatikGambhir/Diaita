import axios from 'axios';
import type { LoginCredentials, CreateProfileData, UserProfile, AuthResponse } from '~/types';

const API_BASE_URL = process.env.NUXT_PUBLIC_API_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

export const userApi = {
  login: async (credentials: LoginCredentials): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  },

  getAccountDetails: async (): Promise<UserProfile> => {
    const response = await apiClient.get<UserProfile>('/user/profile');
    return response.data;
  },

  createProfile: async (data: CreateProfileData): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/register', data);
    return response.data;
  },

  logout: async (): Promise<void> => {
    await apiClient.post('/auth/logout');
  },

  updateProfile: async (data: Partial<UserProfile>): Promise<UserProfile> => {
    const response = await apiClient.put<UserProfile>('/user/profile', data);
    return response.data;
  },

  createUserProfile: async (data: RegisterUserProfileRequest): Promise<{ status: string }> => {
    const response = await apiClient.post<{ status: string }>('/user/profile', data);
    return response.data;
  },
};

export const setAuthToken = (token: string | null) => {
  if (token) {
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete apiClient.defaults.headers.common['Authorization'];
  }
};

type BasicDemographicsRequest = {
  age: number
  sex: string | null
  gender: string | null
  height: number
  weight: number
  bodyFatPercentage: number | null
  leanMass: number | null
  biologicalConsiderations: string | null
  menstrualCycleInfo: string | null
}

type ActivityLifestyleRequest = {
  activityLevel: string
  dailyStepCount: number | null
  jobType: string | null
  commuteTime: string | null
  sleepDuration: number | null
  sleepQuality: string | null
  stressLevel: string | null
  recoveryCapacity: string | null
}

type GoalsPrioritiesRequest = {
  primaryGoal: string
  secondaryGoals: string[] | null
  timeframe: string | null
  targetWeight: number | null
  performanceMetric: string | null
  aestheticGoals: string | null
  healthGoals: string[] | null
}

type TrainingBackgroundRequest = {
  trainingAge: string | null
  trainingHistory: string | null
  currentWorkoutRoutine: string | null
  exercisePreferences: string[] | null
  exerciseDislikes: string[] | null
  equipmentAccess: string | null
  timePerSession: number | null
  daysPerWeek: number | null
}

type MedicalHistoryRequest = {
  injuries: string[] | null
  chronicConditions: string[] | null
  painPatterns: string | null
  mobilityRestrictions: string[] | null
  medications: string[] | null
  doctorRestrictions: string | null
}

type NutritionHistoryRequest = {
  currentDietPattern: string | null
  calorieTrackingExperience: boolean | null
  macronutrientPreferences: string | null
  foodAllergies: string[] | null
  dietaryRestrictions: string[] | null
  culturalFoodPreferences: string | null
  cookingSkillLevel: string | null
  foodBudget: string | null
  eatingSchedule: string | null
  snackingHabits: string | null
  alcoholIntake: string | null
  supplementUse: string[] | null
}

type BehavioralFactorsRequest = {
  motivationLevel: string | null
  consistencyHistory: string | null
  accountabilityPreference: string | null
  pastSuccessFailurePatterns: string | null
  relationshipWithFood: string | null
  disorderedEatingHistory: string | null
  stressEatingTendencies: string | null
  supportSystem: string | null
}

type MetricsTrackingRequest = {
  preferredProgressMetrics: string[] | null
  trackingTools: string[] | null
  checkinFrequency: string | null
}

export type RegisterUserProfileRequest = {
  id: string
  userId: string
  basicDemographics: BasicDemographicsRequest
  activityLifestyle: ActivityLifestyleRequest
  goals: GoalsPrioritiesRequest
  trainingBackground: TrainingBackgroundRequest | null
  medicalHistory: MedicalHistoryRequest | null
  nutritionHistory: NutritionHistoryRequest | null
  behavioralFactors: BehavioralFactorsRequest | null
  metricsTracking: MetricsTrackingRequest | null
  notes: string | null
}

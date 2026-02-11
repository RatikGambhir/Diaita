import type { UserProfile, ProfileSetupFormData } from '~/types'

export const useUserProfile = () => {
  const userStore = useUserStore()

  /**
   * Fetch user profile (mock implementation using localStorage)
   * In production, this would call Supabase
   */
  const fetchProfile = async (): Promise<UserProfile | null> => {
    try {
      // Mock: Get from localStorage
      const profileData = localStorage.getItem('userProfile')
      if (profileData) {
        const profile = JSON.parse(profileData) as UserProfile
        userStore.setProfile(profile)
        return profile
      }
      return null
    } catch (error) {
      console.error('Error fetching profile:', error)
      return null
    }
  }

  /**
   * Create new profile (mock implementation using localStorage)
   * In production, this would insert into Supabase
   */
  const createProfile = async (data: ProfileSetupFormData): Promise<UserProfile | null> => {
    try {
      // Mock: Generate ID and timestamps
      const profile: UserProfile = {
        id: crypto.randomUUID(),
        ...data,
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString()
      }

      // Mock: Save to localStorage
      localStorage.setItem('userProfile', JSON.stringify(profile))
      userStore.setProfile(profile)

      return profile
    } catch (error) {
      console.error('Error creating profile:', error)
      return null
    }
  }

  /**
   * Update existing profile (mock implementation using localStorage)
   * In production, this would update Supabase
   */
  const updateProfile = async (data: ProfileSetupFormData): Promise<UserProfile | null> => {
    try {
      const existingProfileData = localStorage.getItem('userProfile')

      if (!existingProfileData) {
        // If no profile exists, create one
        return await createProfile(data)
      }

      const existingProfile = JSON.parse(existingProfileData) as UserProfile

      const updatedProfile: UserProfile = {
        ...existingProfile,
        ...data,
        updated_at: new Date().toISOString()
      }

      // Mock: Save to localStorage
      localStorage.setItem('userProfile', JSON.stringify(updatedProfile))
      userStore.setProfile(updatedProfile)

      return updatedProfile
    } catch (error) {
      console.error('Error updating profile:', error)
      return null
    }
  }

  /**
   * Check if user has completed profile
   */
  const hasCompletedProfile = computed(() => {
    return userStore.getProfile !== null
  })

  return {
    fetchProfile,
    createProfile,
    updateProfile,
    hasCompletedProfile
  }
}

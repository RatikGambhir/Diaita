export const ACTIVITY_LEVEL_OPTIONS = [
  { value: "sedentary", label: "Sedentary (little or no exercise)" },
  { value: "lightly_active", label: "Lightly active (1–3 days/week)" },
  { value: "moderately_active", label: "Moderately active (3–5 days/week)" },
  { value: "very_active", label: "Very active (6–7 days/week)" },
  { value: "extremely_active", label: "Extremely active (athlete/physical job)" },
] as const

export const PRIMARY_GOAL_OPTIONS = [
  { value: "lose_weight", label: "Lose weight" },
  { value: "build_muscle", label: "Build muscle" },
  { value: "improve_endurance", label: "Improve endurance" },
  { value: "maintain_weight", label: "Maintain weight" },
  { value: "improve_health", label: "Improve overall health" },
  { value: "increase_strength", label: "Increase strength" },
  { value: "improve_flexibility", label: "Improve flexibility" },
  { value: "body_recomposition", label: "Body recomposition" },
] as const

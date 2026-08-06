/** Nutrition value formatting shared by the API mappers and the nutrition page. */

/** Rounds a macro/calorie value to one decimal place. */
export function roundNutritionValue(value: number): number {
  return Math.round(value * 10) / 10;
}

/**
 * Renders a macro/calorie value for display.
 *
 * The original callers branched on `Number.isInteger` but returned the same
 * template string from both arms, so this is that behaviour without the branch.
 */
export function formatNutritionValue(value: number): string {
  return `${roundNutritionValue(value)}`;
}

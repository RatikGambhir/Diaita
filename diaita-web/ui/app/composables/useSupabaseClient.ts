import type { SupabaseClient } from "@supabase/supabase-js";
import { createClient } from "@supabase/supabase-js";

let client: SupabaseClient | null = null;

/**
 * Returns the shared Supabase browser client, created on first use.
 *
 * Credentials come from Nuxt's public runtime config (`NUXT_PUBLIC_SUPABASE_URL`
 * / `NUXT_PUBLIC_SUPABASE_KEY`) so they can be set per deployment instead of
 * being baked in at build time.
 */
export function useSupabaseClient(): SupabaseClient {
  if (client) {
    return client;
  }

  const { supabaseUrl, supabaseKey } = useRuntimeConfig().public;

  if (!supabaseUrl || !supabaseKey) {
    throw new Error(
      "Supabase is not configured. Set NUXT_PUBLIC_SUPABASE_URL and NUXT_PUBLIC_SUPABASE_KEY.",
    );
  }

  client = createClient(supabaseUrl, supabaseKey, {
    auth: {
      persistSession: true,
      autoRefreshToken: true,
      detectSessionInUrl: true,
    },
  });

  return client;
}

import { defineNuxtConfig } from "nuxt/config";
import tailwindcss from "@tailwindcss/vite";

export default defineNuxtConfig({
  srcDir: "app",
  devtools: { enabled: true },

  modules: [
    "@nuxt/eslint",
    "@nuxt/image",
    "@nuxt/scripts",
    "@nuxt/test-utils",
    "@pinia/nuxt",
  ],

  // Values are overridden at runtime by NUXT_PUBLIC_* environment variables.
  runtimeConfig: {
    public: {
      apiBaseUrl: "http://localhost:8080",
      supabaseUrl: "",
      supabaseKey: "",
    },
  },

  pinia: {
    storesDirs: ["./stores/**"],
  },

  css: ["~/assets/css/main.css"],

  vite: {
    plugins: [tailwindcss()],
  },

  // `pathPrefix: false` keeps component names flat (WorkoutCard, not WorkoutsWorkoutCard)
  // regardless of which subdirectory they live in.
  components: [
    {
      path: "~/components",
      extensions: [".vue"],
      pathPrefix: false,
    },
  ],

  compatibilityDate: "2025-07-16",
});

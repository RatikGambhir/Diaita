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

  pinia: {
    storesDirs: ["./stores/**", "./custom-folder/stores/**"],
  },

  css: ["~/assets/css/main.css"],

  vite: {
    plugins: [tailwindcss()],
  },

  components: {
    dirs: [
      {
        path: '~/components',
        extensions: ['.vue'],
        pathPrefix: false,
      },
      {
        path: '~/components/ui',
        extensions: ['.vue'],
        pathPrefix: false,
      },
    ],
  },

  compatibilityDate: "2025-07-16",
});

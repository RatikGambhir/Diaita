import { FetchError } from "ofetch";
import {
  createError,
  defineEventHandler,
  getMethod,
  getQuery,
  getRouterParam,
  readBody,
} from "h3";

const API_BASE_URL = process.env.NUXT_PUBLIC_API_URL || "http://localhost:8080";

export default defineEventHandler(async (event) => {
  const path = getRouterParam(event, "path");

  if (!path) {
    throw createError({
      statusCode: 400,
      statusMessage: "Missing nutrition path",
    });
  }

  const method = getMethod(event);
  const serviceUrl = new URL(`/nutrition/${path}`, API_BASE_URL);

  try {
    return await $fetch(serviceUrl.toString(), {
      method,
      query: method === "GET" ? getQuery(event) : undefined,
      body:
        method === "GET" || method === "HEAD"
          ? undefined
          : await readBody(event),
    });
  } catch (error) {
    if (error instanceof FetchError) {
      const statusCode = error.response?.status ?? 500;
      const data = error.data;
      const statusMessage =
        typeof data === "string"
          ? data
          : error.response?.statusText || "Nutrition request failed";

      throw createError({
        statusCode,
        statusMessage,
        data,
      });
    }

    throw createError({
      statusCode: 500,
      statusMessage: "Nutrition proxy failed",
    });
  }
});

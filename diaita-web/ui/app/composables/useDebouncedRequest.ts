import type { Ref, WatchSource } from "vue";
import { onBeforeUnmount, ref, watch } from "vue";

type DebouncedRequestOptions<TArgs extends unknown[], TResult> = {
  /** Reactive inputs; their current values are passed to `shouldRun` and `request`. */
  sources: { [K in keyof TArgs]: WatchSource<TArgs[K]> };
  /** Returns false to skip the request and reset state instead. */
  shouldRun: (...args: TArgs) => boolean;
  request: (...args: TArgs) => Promise<TResult>;
  /** Value assigned to `data` before a skipped or failed request. */
  emptyValue: TResult;
  errorMessage: string;
  debounceMs: number;
  /** Extra state to clear whenever a request is skipped. */
  onReset?: () => void;
  /** Called after a request resolves successfully. */
  onSuccess?: () => void;
  /** Called after a request rejects. */
  onError?: () => void;
};

/**
 * Runs a debounced async request whenever `sources` change, keeping only the
 * newest in-flight response.
 *
 * Every request gets an incrementing id; a response is applied only while its id
 * is still the latest, so a slow earlier request can never overwrite a newer
 * one. Pending timers are cancelled on unmount.
 */
export function useDebouncedRequest<TArgs extends unknown[], TResult>(
  options: DebouncedRequestOptions<TArgs, TResult>,
) {
  const data = ref(options.emptyValue) as Ref<TResult>;
  const isLoading = ref(false);
  const error = ref<string | null>(null);

  let debounceTimeout: ReturnType<typeof setTimeout> | null = null;
  let latestRequestId = 0;

  const cancelPending = () => {
    if (debounceTimeout) {
      clearTimeout(debounceTimeout);
      debounceTimeout = null;
    }
  };

  const reset = () => {
    latestRequestId++;
    cancelPending();
    data.value = options.emptyValue;
    isLoading.value = false;
    error.value = null;
    options.onReset?.();
  };

  const execute = async (...args: TArgs) => {
    const requestId = ++latestRequestId;
    isLoading.value = true;
    error.value = null;

    try {
      const result = await options.request(...args);

      if (requestId !== latestRequestId) {
        return;
      }

      data.value = result;
      options.onSuccess?.();
    } catch {
      if (requestId !== latestRequestId) {
        return;
      }

      data.value = options.emptyValue;
      error.value = options.errorMessage;
      options.onError?.();
    } finally {
      if (requestId === latestRequestId) {
        isLoading.value = false;
      }
    }
  };

  watch(
    options.sources,
    (args, _previous, onCleanup) => {
      cancelPending();

      if (!options.shouldRun(...(args as TArgs))) {
        reset();
        return;
      }

      debounceTimeout = setTimeout(() => {
        debounceTimeout = null;
        void execute(...(args as TArgs));
      }, options.debounceMs);

      onCleanup(cancelPending);
    },
    { immediate: true },
  );

  onBeforeUnmount(() => {
    latestRequestId++;
    cancelPending();
  });

  return { data, isLoading, error, reset };
}

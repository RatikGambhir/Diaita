package com.diaita.lib.factories

/** Loads prompt templates from `src/main/resources/prompts` and fills in `{variable}` placeholders. */
object PromptFactory {

    private const val PROMPT_RESOURCE_ROOT = "/prompts"

    private val promptResources = mapOf(
        "registerUserMetadata" to "$PROMPT_RESOURCE_ROOT/register_user_metadata_prompt.md"
    )

    private val prompts: Map<String, String?> by lazy {
        promptResources.mapValues { (_, path) -> loadPrompt(path) }
    }

    fun getPrompt(name: String): String? = prompts[name]?.takeIf { it.isNotBlank() }

    fun getPromptWithVariables(name: String, variables: Map<String, Any>): String {
        val prompt = getPrompt(name)
            ?: throw IllegalArgumentException("Prompt '$name' not found")

        return variables.entries.fold(prompt) { filled, (key, value) ->
            filled.replace("{$key}", value.toString())
        }
    }

    private fun loadPrompt(path: String): String? =
        runCatching { PromptFactory::class.java.getResource(path)?.readText() }.getOrNull()
}

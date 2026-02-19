package com.diaita.lib.factories

class PromptFactory {
    companion object {
        private val promptMock: Map<String, String?> by lazy {
            mapOf(
                "registerUserMetadata" to loadPrompt("/prompts/register_user_metadata_prompt.md"),
            )
        }
        fun getPrompt(name: String): String? {
            return promptMock[name]?.takeIf { it.isNotBlank() }
        }

        fun getPromptWithVariables(name: String, variables: Map<String, Any>): String {
            val prompt = getPrompt(name) ?: throw IllegalArgumentException("Prompt '$name' not found")
            return variables.entries.fold(prompt) { acc, (key, value) ->
                acc.replace("{$key}", value.toString())
            }
        }

        private fun loadPrompt(path: String): String? {
            try {
                return object{}.javaClass.getResource(
                    path
                )?.readText()
            } catch (e: Exception) {
                return null
            }
        }
    }
    private val prompts: Map<String, String?> by lazy {
        mapOf(
            "registerUserMetadata" to loadPrompt("/prompts/register_user_metadata_prompt.md"),
        )
    }

    private fun loadPrompt(path: String): String? {
        try {
            return object{}.javaClass.getResource(
                path
            )?.readText()
        } catch (e: Exception) {
            return null
        }
    }

     fun get(name: String): String? =
        prompts[name]?.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("Prompt '$name' is blank or missing")

     fun getWithVariables(name: String, variables: Map<String, Any>): String? {
        val prompt = get(name)
        return variables.entries.fold(prompt) { acc, (key, value) ->
            acc?.replace("{$key}", value.toString())
        }
    }
}
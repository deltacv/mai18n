package org.deltacv.mai18n

import java.util.concurrent.ConcurrentHashMap

/**
 * Cache key used for string variable replacement.
 *
 * @param text The base string containing variable placeholders.
 * @param vars A serialized representation of the variables used.
 */
data class CacheKey(val text: String, val vars: String)

/**
 * Cache for processed strings with variable replacements.
 *
 * Uses a ConcurrentHashMap to allow safe concurrent access
 * without explicit synchronization.
 */
internal val stringVarCache = ConcurrentHashMap<CacheKey, String>()

/**
 * Invalidates the cache used by stringVar().
 */
fun invalidateStringVarCache() {
    stringVarCache.clear()
}

/**
 * Replaces indexed variables inside a string.
 *
 * Variables must follow the syntax $[number], where "number"
 * corresponds to the index of the variable in the provided array.
 *
 * Example:
 * stringVar("Hello $[0]", "World") -> "Hello World"
 *
 * Results are cached to avoid re-processing the same input.
 *
 * @param text the base string
 * @param vars variables to inject
 * @return the processed string
 */
fun stringVar(text: String, vararg vars: Any): String {
    val serializedVars = vars.joinToString(separator = "\u0000") { it.toString() }
    val cacheKey = CacheKey(text, serializedVars)

    return stringVarCache.computeIfAbsent(cacheKey) {
        val matches = Language.variableRegex.findAll(text)
        var finalTxt = text

        for (match in matches) {
            val index = match.groupValues[1].toIntOrNull() ?: continue

            if (index in vars.indices) {
                finalTxt = finalTxt.replace(match.value, vars[index].toString())
            }
        }

        finalTxt
    }
}

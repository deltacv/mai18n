@file:JvmName("Mai18n")

package org.deltacv.mai18n

/**
 * Thread-local (contextual) translation language.
 *
 * If set, this takes priority over the global translation language.
 */
private val threadTrLanguage = ThreadLocal<Language>()

/**
 * JVM-global translation language.
 *
 * Used as a fallback when no thread-local language is defined.
 */
@Volatile
private var globalTrLanguage: Language? = null

/**
 * Returns the effective translation language for the current context.
 *
 * Resolution order:
 * 1. Thread-local language
 * 2. Global language
 *
 * @throws IllegalStateException if no language is defined
 */
private fun resolveTrLanguage(): Language {
    return threadTrLanguage.get()
        ?: globalTrLanguage
        ?: throw IllegalStateException(
            "No translation language is defined. " +
                    "Call Language.makeThreadTr() or Language.makeGlobalTr()."
        )
}

/**
 * Calls tr() on the resolved translation language.
 *
 * Prefers the current thread's language if present,
 * otherwise falls back to the global language.
 *
 * @see Language.tr
 */
fun tr(text: String, vararg parameters: Any): String {
    return resolveTrLanguage().tr(text, *parameters)
}

/**
 * Sets this Language as the translation language for the current thread.
 *
 * This overrides the global language for the calling thread only.
 */
fun Language.makeThreadTr() {
    threadTrLanguage.set(this)
}

/**
 * Clears the JVM-global translation language.
 *
 * After calling this, tr() will throw an exception
 * if no thread-local language is defined.
 */
fun clearGlobalTr() {
    globalTrLanguage = null
}

/**
 * Clears the thread-local translation language for the current thread.
 *
 * After calling this, tr() will fall back to the global language.
 */
fun clearThreadTr() {
    threadTrLanguage.remove()
}

/**
 * Sets this Language as the JVM-global translation language.
 *
 * Used when no thread-local language is defined.
 */
fun Language.makeGlobalTr() {
    globalTrLanguage = this
}
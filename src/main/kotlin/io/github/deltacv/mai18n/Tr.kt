@file:JvmName("Mai18n")

package io.github.deltacv.mai18n

var threadTrLanguage = mutableMapOf<String, Language>()

@get:Synchronized
val trLanguage: Language?
    get() = threadTrLanguage[Thread.currentThread().name]

/**
 * Calls tr() on the current Thread's trLanguage and returs the result, for static convenience.
 * Call Language.makeTr() to define a Language as a "trLanguage" for the current thread,.
 *
 * @throws IllegalStateException if there's not a LangManager defined as a tr for this thread
 * @see Language.makeTr
 * @see Language.tr
 */
fun tr(text: String, vararg parameters: Any): String {
    if(trLanguage == null) {
        throw IllegalStateException("There's not a LangManager defined as a tr for ${Thread.currentThread().name}, create a LangManager and makeTr() it")
    }

    return trLanguage!!.tr(text, *parameters)
}

/**
 * Sets the current thread's trLanguage to the given Language.
 * This is used to define a Language as a "trLanguage" for tr().
 */
fun makeTr(language: Language) {
    threadTrLanguage[Thread.currentThread().name] = language
}
@file:JvmName("Mai18n")

package org.deltacv.mai18n

private var threadTrLanguage = ThreadLocal<Language>()

@get:Synchronized
val globalTrLanguage: Language?
    get() = threadTrLanguage.get()

/**
 * Calls tr() on the current Thread's globalTrLanguage and returs the result, for static convenience.
 * Call Language.makeGlobalTr() to define a global Language for the current thread.
 *
 * @throws IllegalStateException if there's not a LangManager defined as a tr for this thread
 * @see Language.tr
 */
fun tr(text: String, vararg parameters: Any): String {
    if(globalTrLanguage == null) {
        throw IllegalStateException("There's not a LangManager defined as a tr for ${Thread.currentThread().name}, create a LangManager and makeTr() it")
    }

    return globalTrLanguage!!.tr(text, *parameters)
}

/**
 * Sets the current thread's trLanguage to the given Language.
 * This is used to define a Language as a "trLanguage" for tr().
 */
@Suppress("UNUSED")
fun Language.makeGlobalTr() {
    threadTrLanguage.set(this)
}
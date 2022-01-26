@file:JvmName("Mai18n")

package io.github.deltacv.mai18n

var trLanguage: Language? = null

/**
 * Calls tr() on the defined "trLanguage" and retursn the result, for static convenience.
 * Call Language.makeTr() to define a Language as a "trLanguage",
 * or manually set it in this class.
 *
 * @throws IllegalStateException if there's not a LangManager defined as a tr
 * @see Language.makeTr
 * @see Language.tr
 */
fun tr(text: String, vararg parameters: Any): String {
    if(trLanguage == null) {
        throw IllegalStateException("There's not a LangManager defined as a tr, create a LangManager and makeTr() it")
    }

    return trLanguage!!.tr(text, *parameters)
}
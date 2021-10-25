@file:JvmName("Mai18n")

package io.github.deltacv.mai18n

var trLangManager: LangManager? = null

/**
 * Calls tr() on the defined "trLangManager" and retursn the result, for static convenience.
 * Call LangManager.makeTr() to define a LangManager as a "trLangManager",
 * or manually set it in this class.
 *
 * @throws IllegalStateException if there's not a LangManager defined as a tr
 * @see LangManager.makeTr
 * @see LangManager.tr
 */
fun tr(text: String, vararg parameters: Any): String {
    if(trLangManager == null) {
        throw IllegalStateException("There's not a LangManager defined as a tr, create a LangManager and makeTr() it")
    }

    return trLangManager!!.tr(text, *parameters)
}
package io.github.deltacv.mai18n

data class CacheKey(val text: String, val vars: List<Any>)

val stringVarCache = mutableMapOf<CacheKey, String>()

fun invalidateStringVarCache() {
    stringVarCache.clear()
}

fun stringVar(text: String, vararg vars: Any): String {
    val cacheKey = CacheKey(text, vars.toList())

    return stringVarCache.getOrPut(cacheKey) {
        val matches = Language.variableRegex.findAll(text)
        var finalTxt = text

        for(match in matches) {
            val numberStr = match.groupValues[1]
            val number = try {
                numberStr.toInt()
            } catch(_: NumberFormatException) {
                continue
            }

            if(number >= 0 && number < vars.size) {
                finalTxt = finalTxt.replace(match.value, vars[number].toString())
            }
        }

        stringVarCache[cacheKey] = finalTxt

        finalTxt
    }
}
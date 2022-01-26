package io.github.deltacv.mai18n

fun stringVar(text: String, vararg vars: Any): String {
    val matches = Language.variableRegex.findAll(text)

    var finalTxt = text

    for(match in matches) {
        val numberStr = match.groupValues[1]
        val number = try {
            numberStr.toInt()
        } catch(ignored: NumberFormatException) {
            continue
        }

        if(number >= 0 && number < vars.size) {
            finalTxt = finalTxt.replace(match.value, vars[number].toString())
        }
    }

    return finalTxt
}
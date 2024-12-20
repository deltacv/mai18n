package io.github.deltacv.mai18n

import com.opencsv.CSVReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.util.*

class Language(langFile: String, lang: String, val encoding: Encoding = Encoding.UTF_8) {

    internal companion object {
        val variableRegex = Regex("\\$\\[(.*?)]")
    }

    /**
     * The CSV language file path.
     * It can be either a resource path of a file inside the jar file,
     * or a external file in any location of the disk.
     *
     * The library will try to look for a resource file in the jar with
     * the specified path first. If that fails, it tries to find it as
     * a file in the disk.
     */
    var langFile = langFile
        set(value) {
            field = value
            load()
        }

    /**
     * The language of the keys that will be returned
     * by the get() method. Languages are determined
     * by the first row in the csv file. The first
     * column is skipped because it contains the keys,
     * not values.
     *
     * Use availableLangs to find the list of languages
     * determined by the first row as explaine before
     */
    var lang = lang
        set(value) {
            loadIfNeeded()

            if(!availableLangs.contains(lang)) {
                throw IllegalArgumentException("The language \"$lang\" is not present in the $langFile file")
            }

            field = value

            langIndex = availableLangs.indexOf(lang) + 1
            trCache.clear()
        }

    /**
     * Returns the list of available languages determined
     * by the first row in the csv file. The first column
     * is then skipped because it contains the keys of the
     * language strings, not values.
     */
    var availableLangs = listOf<String>()
        private set

    /**
     * The index of the language specified by the "lang" variable.
     * The index refers to the column number of the language in
     * the csv, starting from 0 (which is the key column)
     */
    var langIndex = -1
        private set

    lateinit var strings: Map<String, Map<String, String>>
        private set

    private val trCache = HashMap<String, String>()

    /**
     * Gets the given key for the current "lang".
     * @returns the value of the key or null if the key doesn't exist
     */
    fun get(key: String): String? {
        loadIfNeeded()
        return strings[lang]!![key]?.replace("<br>", "\n")
    }

    /**
     * Translates a given string performing a key lookup.
     * Key lookup is performed on tokens surrounded by
     * the $[] syntax, for example "$[lang_test]" will
     * look up for the key "lang_test" in the current
     * language and replace the variable token if its
     * found. If it isn't found, nothing is replaced.
     *
     * If no keys surrounded by the $[] are found, the
     * method then tries to interpret the complete string
     * as a key, for example, if the string is "lang_test",
     * it will look up for the key "lang_test" and return
     * the value if it exists.
     *
     * Otherwise, the exact same passed string is returned
     *
     * The method also performs caching, storing the results
     * in a WeakHashMap, which means that strings translated
     * before won't have to be processed again if they exist
     * in the cache, making the process faster and efficient.
     * If the cached string is garbage collected, it's removed
     * from the WeakHashMap (since that's its purpose)
     *
     * @param text the string to translate following the rules explained before
     */
    fun tr(text: String, vararg parameters: Any): String {
        val cachedText = trCache[text]

        if(cachedText != null) {
            return stringVar(cachedText, *parameters)
        }

        val matches = variableRegex.findAll(text)
        var finalTxt = text

        var hasMatches = false
        for(match in matches) {
            hasMatches = true

            val trValue = get(match.groupValues[1]) ?: continue
            finalTxt = finalTxt.replace(match.value, trValue)
        }

        if(!hasMatches) {
            val trValue = get(text)
            if(trValue != null) {
                finalTxt = trValue
            }
        }

        trCache[text] = finalTxt

        return stringVar(finalTxt, *parameters)
    }

    /**
     * Loads the csv file if it hasn't been loaded yet
     * @throws
     */
    fun loadIfNeeded(): Language {
        if(!::strings.isInitialized) {
            load()
        }

        return this
    }

    private fun load() {
        val resource = try {
            javaClass.getResourceAsStream(langFile)
        } catch (_: Exception) { null }

        val reader = if(resource != null) {
            InputStreamReader(resource, encoding.string)
        } else {
            InputStreamReader(FileInputStream(langFile), encoding.string)
        }

        val csv = CSVReader(reader)
            .readAll()

        if(csv.isEmpty()) {
            throw IllegalArgumentException("The $langFile file is empty")
        }

        val strs = mutableMapOf<String, MutableMap<String, String>>()
        val langs = mutableListOf<String>()

        for((i, values) in csv.withIndex()) {
            if(i == 0) {
                for((langI, lang) in values.withIndex()) {
                    // skipping the first column of the first row, the first column is exclusive to keys
                    if(langI != 0) {
                        langs.add(lang)
                    }
                }

                continue
            }

            var currentKey = ""

            for((langI, value) in values.withIndex()) {
                if(langI == 0) {
                    currentKey = value // the first column (index 0) must be the key for these values
                } else {
                    // gets the language name of the column current index (-1 because of the first index being the key column)
                    val langName = langs[langI - 1]

                    if(!strs.containsKey(langName)) {
                        strs[langName] = mutableMapOf()
                    }

                    strs[langName]!![currentKey] = value
                }
            }
        }

        availableLangs = langs // storing available langs into an inmutable list variable
        strings = strs

        lang = lang // triggering setter
    }

    /**
     * Sets this LangManager as the "trLangManager"
     * @see tr
     * @see trLanguage
     */
    fun makeTr(): Language {
        makeTr(this)
        return this
    }

    /**
     * Returns the cache of the translated strings
     * @see tr
     */
    fun getCache(): Map<String, String> {
        return trCache
    }

    /**
     * Invalidates the cache of the translated strings
     * Forces the tr() method to reprocess the strings
     * @see tr
     */
    fun invalidateCache() {
        trCache.clear()
    }

}
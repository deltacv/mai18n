package org.deltacv.mai18n

import com.opencsv.CSVReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap

/**
 * Language class responsible for managing application translations.
 *
 * Loads a single language from a CSV file and provides access to
 * translated strings by key.
 *
 * CSV format requirements:
 * - The first row must list all available languages.
 * - The first column of each subsequent row is reserved for translation keys.
 *
 * Note:
 * This class is immutable with respect to the selected language.
 * To switch languages, a new instance must be created.
 *
 * @param file The path to the CSV file. May be a classpath resource or a file on disk.
 * @param lang The language to load. Must be one of the languages defined in the CSV header.
 * @param encoding The CSV file encoding. Defaults to UTF-8.
 */
class Language(
    private val file: String,
    val lang: String,
    val encoding: Encoding = Encoding.UTF_8,
    val loadLazily: Boolean = true
) {

    internal companion object {
        /** Regex used to find translation variables in the form of $[key] */
        val variableRegex = Regex("\\$\\[(.*?)]")
    }

    /**
     * The language strings loaded from the csv file.
     *
     * Maps a key to its translated value for the selected language.
     *
     * A ConcurrentHashMap is used so this structure is safe to access
     * concurrently without explicit synchronization.
     */
    internal val strings = ConcurrentHashMap<String, String>()

    /**
     * Cache for translated strings.
     *
     * Stores already processed translations to avoid re-processing them.
     * Uses a ConcurrentHashMap to support concurrent access.
     */
    internal val trCache = ConcurrentHashMap<String, String>()

    init {
        if(!loadLazily) {
            loadIfNeeded()
        }
    }

    /**
     * Gets the given key for the current language.
     *
     * @return the value of the key or null if the key doesn't exist
     */
    fun get(key: String): String? {
        loadIfNeeded()
        return strings[key]?.replace("<br>", "\n")
    }

    /**
     * Translates a given string performing a key lookup and variable substitution.
     *
     * Key lookup is performed on tokens surrounded by the $[] syntax.
     * Example:
     * "$[lang_test]" will look up the key "lang_test" and replace it
     * with the translated value if found.
     *
     * If no $[] tokens are found, the method attempts to interpret the entire
     * string as a translation key and returns the translated value if it exists.
     *
     * In addition to key lookup, the method also supports indexed string variables.
     * Indexed variables follow the same $[] syntax, but contain a numeric index.
     * Example:
     * "$[0]" will be replaced by the first value provided in the parameters array,
     * "$[1]" by the second, and so on.
     *
     * If no translation or variable replacement is found, the original string
     * is returned unchanged.
     *
     * The method performs caching, storing the processed result so repeated
     * calls are faster and more efficient.
     *
     * @param text the string to translate
     */
    fun tr(text: String, vararg parameters: Any): String {
        trCache[text]?.let {
            return stringVar(it, *parameters)
        }

        var finalTxt = text
        val matches = variableRegex.findAll(text).toList()

        if (matches.isNotEmpty()) {
            for (match in matches) {
                val trValue = get(match.groupValues[1]) ?: continue
                finalTxt = finalTxt.replace(match.value, trValue)
            }
        } else {
            get(text)?.let { finalTxt = it }
        }

        trCache[text] = finalTxt
        return stringVar(finalTxt, *parameters)
    }

    /**
     * Loads the csv file if it hasn't been loaded yet.
     *
     * This method relies on the thread-safe nature of ConcurrentHashMap.
     * If multiple threads attempt to load simultaneously, the resulting
     * state is still valid and consistent.
     */
    private fun loadIfNeeded() {
        if (strings.isNotEmpty()) {
            return
        }

        val resource = try {
            javaClass.getResourceAsStream(file)
        } catch (_: Exception) { null }

        val reader = if (resource != null) {
            InputStreamReader(resource, encoding.string)
        } else {
            InputStreamReader(FileInputStream(file), encoding.string)
        }

        val csv = CSVReader(reader).readAll()

        if (csv.isEmpty()) {
            throw IllegalArgumentException("The $file file is empty")
        }

        val header = csv.first()
        val langIndex = header.indexOf(lang)

        if (langIndex == -1) {
            throw IllegalArgumentException("The language \"$lang\" is not present in $file")
        }

        val loaded = HashMap<String, String>()

        for ((i, values) in csv.withIndex()) {
            if (i == 0) continue // skipping header row
            if (values.isEmpty()) continue

            val key = values[0]

            if (langIndex < values.size) {
                loaded[key] = values[langIndex]
            }
        }

        // Atomic bulk insert into the concurrent map
        strings.putAll(loaded)
    }

    /**
     * Invalidates the cache of the translated strings.
     *
     * Forces the tr() method to reprocess strings on next call.
     *
     * @see tr
     */
    fun invalidateCache() {
        trCache.clear()
    }
}

# mai18n

Simple CSV-based internationalization for the JVM.

`mai18n` loads **one language at a time** from a CSV file and provides a small API for
key-based translation and indexed string variables.

---

## Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.deltacv:mai18n:1.2.0'
}
```

---

## CSV format

The CSV file must follow this structure:

* The **first row** defines the available languages
* The **first column** of every row is the translation key
* Each remaining column contains translations for a language

Example:

```csv
key,en,es
hello,Hello,Hola
welcome_user,Welcome $[0],Bienvenido $[0]
```

---

## Usage

### Loading a language

```kotlin
import org.deltacv.mai18n.Language

val lang = Language("/test.csv", "en")
```

By default, the file is loaded lazily on first access.
To load immediately:

```kotlin
val lang = Language("/test.csv", "en", loadLazily = false)
```

### Language immutability

`Language` instances are immutable with respect to the selected language.

To switch languages, create a new instance:

```kotlin
val en = Language("/test.csv", "en")
val es = Language("/test.csv", "es")
```

---

## Getting raw values

Retrieve a translated value by key:

```kotlin
val text = lang.get("hello")
```

Returns `null` if the key does not exist.

---

## `tr()` API

### Key translation

`tr()` resolves translation keys embedded using the `$[]` syntax.

```kotlin
lang.tr("hello")
lang.tr("$[hello]")
```

Both return:

```text
Hello
```

If no `$[]` tokens are found, `tr()` attempts to translate the entire string as a key.

If no translation exists, the original string is returned.

---

### Indexed string variables

`tr()` also supports indexed variables using the same `$[]` syntax.

Indexes refer to the arguments passed to `tr()`:

```kotlin
lang.tr("welcome_user", "Alex")
```

CSV value:

```text
Welcome $[0]
```

Result:

```text
Welcome Alex
```

Multiple variables are supported:

```kotlin
lang.tr("score_msg", "Alex", 42)
```

```text
$[0] scored $[1] points
```

---

## Caching behavior

* Translated strings are cached internally for performance
* Variable substitution (`$[0]`, `$[1]`, etc.) is applied after translation
* Cache access is thread-safe

To clear the translation cache:

```kotlin
lang.invalidateCache()
```
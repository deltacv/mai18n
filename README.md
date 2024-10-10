# mai18n - Simple CSV localization for the JVM

### Gradle

```groovy
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.deltacv:mai18n:1.1.0'
}
```

## Usage

### Check [test.csv](https://github.com/deltacv/mai18n/blob/master/src/test/resources/test.csv) for an example i18n file

Load the file:

```kotlin
import io.github.deltacv.mai18n.Language

val language = Language("/test.csv", "en").loadIfNeeded()
```

Check the languages available for that file & set to another language:

```kotlin
val languages = lang.availableLangs

language.lang = "es"
```

Get a value from the current language:

```
val string = language.get("test1")
```

### tr() api

Coming soon.

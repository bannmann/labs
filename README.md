[![](https://img.shields.io/badge/What%20to%20expect%20➞-purple "What to expect")](https://www.donmccurdy.com/2023/07/03/expectations-in-open-source/)&ensp;[![](https://img.shields.io/badge/maturity-experimental-yellow "Maturity: Experimental")](https://www.donmccurdy.com/2023/07/03/expectations-in-open-source/#maturity)&ensp;[![](https://img.shields.io/badge/development-active-blue "Development: Active")](https://www.donmccurdy.com/2023/07/03/expectations-in-open-source/#development)&ensp;[![](https://img.shields.io/badge/support-limited-yellow "Support: Limited")](https://www.donmccurdy.com/2023/07/03/expectations-in-open-source/#support)


## anansi-postgres
[![](https://img.shields.io/maven-central/v/dev.bannmann.labs/anansi-postgres?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs/anansi-postgres)


----


## annotations

[![](https://img.shields.io/maven-central/v/dev.bannmann.labs/annotations?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs/annotations)&ensp;[![](https://javadoc.io/badge2/dev.bannmann.labs/annotations/javadoc.svg "Javadoc")](https://javadoc.io/doc/dev.bannmann.labs/annotations)


----


## core

[![](https://img.shields.io/maven-central/v/dev.bannmann.labs/core?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs/core)&ensp;[![](https://javadoc.io/badge2/dev.bannmann.labs/core/javadoc.svg "Javadoc")](https://javadoc.io/doc/dev.bannmann.labs/core)

[![](https://img.shields.io/badge/nullness_annotations-JSpecify_1.0-darkgreen "JSpecify 1.0 nullness annotations")](https://jspecify.dev/)


----


## json-nav

API to efficiently navigate JSON structures and extract data.

For some use cases, using JsonNav can lead to more expressive code that is easier to read and maintain than when using other JSON APIs or object mappers.

Compared to other APIs, JsonNav provides these advantages:
- Your code for retrieving and mapping values can be compact and readable, yet powerful
- The fluent API only offers methods that make sense at that point, e.g. once `asString()` is called, `asNumber()` is no longer available.
- Makes use of `java.util.Optional`
- Strict handling of type mismatches, for example:
  - Reading a floating point value as an `int` throws an exception instead of performing coercion
  - Reading a string like `"17"` value as a number throws an exception instead of parsing the value
- Increased robustness due to _not_ implementing the `Map` interface
- Fully null-marked with JSpecify annotations
- All references are immutable

### Core
[![](https://img.shields.io/maven-central/v/dev.bannmann.labs.json-nav/nav-core?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs.json-nav/nav-core)&ensp;[![](https://javadoc.io/badge2/dev.bannmann.labs.json-nav/nav-core/javadoc.svg "Javadoc")](https://javadoc.io/doc/dev.bannmann.labs.json-nav/nav-core)

[![](https://img.shields.io/badge/nullness_annotations-JSpecify_1.0-darkgreen "JSpecify 1.0 nullness annotations")](https://jspecify.dev/)

### Adapter for [Jackson](https://github.com/FasterXML/jackson)
Pass a `com.fasterxml.jackson.databind.node.ObjectNode` or `com.fasterxml.jackson.databind.node.ArrayNode` to `JsonNav.wrap()`.

[![](https://img.shields.io/maven-central/v/dev.bannmann.labs.json-nav/nav-jackson?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs.json-nav/nav-jackson)&ensp;[![](https://javadoc.io/badge2/dev.bannmann.labs.json-nav/nav-jackson/javadoc.svg "Javadoc")](https://javadoc.io/doc/dev.bannmann.labs.json-nav/nav-jackson)

### Adapter for [Gson](https://github.com/google/gson)
Pass a `com.google.gson.JsonObject` or `com.google.gson.JsonArray` to `JsonNav.wrap()`.

[![](https://img.shields.io/maven-central/v/dev.bannmann.labs.json-nav/nav-gson?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs.json-nav/nav-gson)&ensp;[![](https://javadoc.io/badge2/dev.bannmann.labs.json-nav/nav-gson/javadoc.svg "Javadoc")](https://javadoc.io/doc/dev.bannmann.labs.json-nav/nav-gson)

### Adapter for [Jakarta JSON Processing](https://jakarta.ee/specifications/jsonp/)
Pass a `jakarta.json.JsonObject` or `jakarta.json.JsonArray` to `JsonNav.wrap()`.

[![](https://img.shields.io/maven-central/v/dev.bannmann.labs.json-nav/nav-jakarta?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs.json-nav/nav-jakarta)&ensp;[![](https://javadoc.io/badge2/dev.bannmann.labs.json-nav/nav-jakarta/javadoc.svg "Javadoc")](https://javadoc.io/doc/dev.bannmann.labs.json-nav/nav-jakarta)

### Adapter for [JSR-374 JSON Processing](https://javaee.github.io/jsonp/)
Pass a `javax.json.JsonObject` or `javax.json.JsonArray` to `JsonNav.wrap()`.

[![](https://img.shields.io/maven-central/v/dev.bannmann.labs.json-nav/nav-javax?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs.json-nav/nav-javax)&ensp;[![](https://javadoc.io/badge2/dev.bannmann.labs.json-nav/nav-javax/javadoc.svg "Javadoc")](https://javadoc.io/doc/dev.bannmann.labs.json-nav/nav-javax)

----


## kumuluzee-errorhandling

[![](https://img.shields.io/maven-central/v/dev.bannmann.labs/kumuluzee-errorhandling?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs/kumuluzee-errorhandling)


----


## records-api

[![](https://img.shields.io/maven-central/v/dev.bannmann.labs/records-api?color=brightgreen "Maven Central")](https://central.sonatype.com/artifact/dev.bannmann.labs/records-api)&ensp;[![](https://javadoc.io/badge2/dev.bannmann.labs/records-api/javadoc.svg "Javadoc")](https://javadoc.io/doc/dev.bannmann.labs/records-api)

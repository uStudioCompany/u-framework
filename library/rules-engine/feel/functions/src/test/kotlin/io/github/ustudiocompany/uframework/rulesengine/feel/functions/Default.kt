package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

internal const val DEFAULT_DATA_TIME_FORMAT: String = "uuuu-MM-dd'T'HH:mm:ss'Z'"

internal val DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATA_TIME_FORMAT)
    .withResolverStyle(ResolverStyle.STRICT)

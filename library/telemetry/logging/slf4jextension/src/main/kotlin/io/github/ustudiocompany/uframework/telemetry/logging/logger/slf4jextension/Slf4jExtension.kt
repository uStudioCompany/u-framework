package io.github.ustudiocompany.uframework.telemetry.logging.logger.slf4jextension

import org.slf4j.Logger

public inline fun Logger.debug(msg: () -> String) {
    if (isDebugEnabled) debug(msg())
}

public inline fun Logger.info(msg: () -> String) {
    if (isInfoEnabled) info(msg())
}

public inline fun Logger.warn(msg: () -> String) {
    if (isWarnEnabled) warn(msg())
}

public inline fun Logger.error(msg: () -> String) {
    if (isErrorEnabled) error(msg())
}

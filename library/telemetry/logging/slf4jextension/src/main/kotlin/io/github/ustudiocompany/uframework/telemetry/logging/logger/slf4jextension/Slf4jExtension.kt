package io.github.ustudiocompany.uframework.telemetry.logging.logger.slf4jextension

import org.slf4j.Logger

public inline fun Logger.debug(msg: () -> String) {
    if (isDebugEnabled) debug(msg())
}

public inline fun Logger.info(msg: () -> String) {
    if (isInfoEnabled) info(msg())
}

public inline fun Logger.warn(t: Throwable? = null, msg: () -> String) {
    if (isWarnEnabled)
        if (t != null) {
            warn(msg(), t)
        } else {
            warn(msg())
        }
}

public inline fun Logger.error(t: Throwable? = null, msg: () -> String) {
    if (isErrorEnabled)
        if (t != null) {
            error(msg(), t)
        } else {
            error(msg())
        }
}

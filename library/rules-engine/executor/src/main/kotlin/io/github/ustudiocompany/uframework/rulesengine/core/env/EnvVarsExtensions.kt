package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError

internal operator fun EnvVars.get(
    envVarName: EnvVarName
): ResultK<JsonElement, EnvVarReadingErrors.EnvVarMissing> {
    val value = this.getOrNull(envVarName)
    return value?.asSuccess()
        ?: EnvVarReadingErrors.EnvVarMissing(envVarName).asFailure()
}

internal sealed interface EnvVarReadingErrors : BasicRulesEngineError {

    class EnvVarMissing(envVarName: EnvVarName) : EnvVarReadingErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "The variable '${envVarName.get}' is not found in environment variables."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_ENV_VAR_NAME to envVarName.get
        )
    }

    private companion object {
        private const val PREFIX = "GET-VALUE-FROM-ENV-VARS-ERROR-"
    }
}

private const val DETAILS_KEY_ENV_VAR_NAME = "env-var-name"

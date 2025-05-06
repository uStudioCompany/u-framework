package io.github.ustudiocompany.uframework.engine.merge

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError

public sealed interface MergeError : BasicRulesEngineError {

    public class RuleMissing(path: AttributePath) : MergeError {
        override val code: String = PREFIX + "1"
        override val description: String = "The merge rule is missing for the path: $path"
        override val cause: Failure.Cause = Failure.Cause.None
        override val details: Failure.Details = Failure.Details.of(
            ATTRIBUTE_PATH to path.toString()
        )
    }

    public sealed interface Source : MergeError {

        public class TypeMismatch(path: AttributePath, expected: String, actual: String) : Source {
            override val code: String = PREFIX + "2"
            override val description: String =
                "Type mismatch in source at path '$path'. Expected: $expected, Actual: $actual"
            override val cause: Failure.Cause = Failure.Cause.None
            override val details: Failure.Details = Failure.Details.of(
                ATTRIBUTE_PATH to path.toString(),
                EXPECTED_TYPE to expected,
                ACTUAL_TYPE to actual
            )
        }

        public class AttributeForMergeStrategyMissing(path: AttributePath) : MergeError {
            override val code: String = PREFIX + "3"
            override val description: String =
                "The data used as the source for the update is missing an attribute used for the merge strategy " +
                    "at path '$path'."
            override val cause: Failure.Cause = Failure.Cause.None
            override val details: Failure.Details = Failure.Details.of(
                ATTRIBUTE_PATH to path.toString()
            )
        }
    }

    public sealed interface Destination : MergeError {

        public class AttributeForMergeStrategyMissing(path: AttributePath) : MergeError {
            override val code: String = PREFIX + "4"
            override val description: String =
                "The data to be updated is missing an attribute used for the merge strategy on path '$path'."
            override val cause: Failure.Cause = Failure.Cause.None
            override val details: Failure.Details = Failure.Details.of(
                ATTRIBUTE_PATH to path.toString()
            )
        }
    }

    private companion object {
        private const val PREFIX = "DATA-ELEMENT-MERGE-"
        private const val ATTRIBUTE_PATH = "attribute-path"
        private const val EXPECTED_TYPE = "expected-type"
        private const val ACTUAL_TYPE = "actual-type"
    }
}

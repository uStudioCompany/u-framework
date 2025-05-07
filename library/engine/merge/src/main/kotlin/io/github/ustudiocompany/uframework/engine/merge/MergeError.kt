package io.github.ustudiocompany.uframework.engine.merge

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import kotlin.reflect.KClass

public sealed interface MergeError : BasicRulesEngineError {

    public class RuleMissing private constructor(path: String) : MergeError {

        public constructor(path: AttributePath) : this(path = path.toString())

        override val code: String = PREFIX + "1"
        override val description: String = "The merge rule is missing for the path: $path"
        override val cause: Failure.Cause = Failure.Cause.None
        override val details: Failure.Details = Failure.Details.of(
            ATTRIBUTE_PATH to path
        )
    }

    public sealed interface Source : MergeError {

        public class TypeMismatch private constructor(path: String, expected: String, actual: String) : Source {

            public constructor(path: AttributePath, expected: KClass<*>, actual: DataElement) :
                this(path = path.toString(), expected = expected.simpleName!!, actual = actual::class.simpleName!!)

            override val code: String = PREFIX + "2"
            override val description: String =
                "Type mismatch in source at path '$path'. Expected: $expected, Actual: $actual"
            override val cause: Failure.Cause = Failure.Cause.None
            override val details: Failure.Details = Failure.Details.of(
                ATTRIBUTE_PATH to path,
                EXPECTED_TYPE to expected,
                ACTUAL_TYPE to actual
            )
        }

        public class AttributeForMergeStrategyMissing private constructor(path: String) : MergeError {

            public constructor(path: AttributePath) : this(path = path.toString())

            override val code: String = PREFIX + "3"
            override val description: String =
                "The data used as the source for the update is missing an attribute used for the merge strategy " +
                    "at path '$path'."
            override val cause: Failure.Cause = Failure.Cause.None
            override val details: Failure.Details = Failure.Details.of(
                ATTRIBUTE_PATH to path
            )
        }
    }

    public sealed interface Destination : MergeError {

        public class TypeMismatch private constructor(path: String, expected: String, actual: String) : Source {

            public constructor(path: AttributePath, expected: KClass<*>, actual: DataElement) :
                this(path = path.toString(), expected = expected.simpleName!!, actual = actual::class.simpleName!!)

            override val code: String = PREFIX + "2"
            override val description: String =
                "Type mismatch in destination at path '$path'. Expected: $expected, Actual: $actual"
            override val cause: Failure.Cause = Failure.Cause.None
            override val details: Failure.Details = Failure.Details.of(
                ATTRIBUTE_PATH to path,
                EXPECTED_TYPE to expected,
                ACTUAL_TYPE to actual
            )
        }

        public class AttributeForMergeStrategyMissing private constructor(path: String) : MergeError {

            public constructor(path: AttributePath) : this(path = path.toString())

            override val code: String = PREFIX + "4"
            override val description: String =
                "The data to be updated is missing an attribute used for the merge strategy on path '$path'."
            override val cause: Failure.Cause = Failure.Cause.None
            override val details: Failure.Details = Failure.Details.of(
                ATTRIBUTE_PATH to path
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

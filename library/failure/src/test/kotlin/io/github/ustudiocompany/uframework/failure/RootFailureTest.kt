package io.github.ustudiocompany.uframework.failure

import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class RootFailureTest : UnitTest() {

    init {

        "Tee `root` function" - {
            withData(
                nameFn = { (failure, _) -> failure.toString() },
                listOf(
                    ROOT to ROOT,
                    CHILD_1 to ROOT,
                    CHILD_2 to ROOT
                )
            ) { (failure, expected) ->
                failure.root() shouldBe expected
            }
        }
    }

    private companion object {
        private const val CODE_1 = "CODE-1"
        private const val CODE_2 = "CODE-2"
        private const val CODE_3 = "CODE-3"

        private val ROOT: Failure = Root(code = CODE_1)
        private val CHILD_1: Failure = Child(code = CODE_2, cause = cause(ROOT))
        private val CHILD_2: Failure = Child(code = CODE_3, cause = cause(Child(code = CODE_2, cause = cause(ROOT))))

        private fun cause(failure: Failure): Failure.Cause = Failure.Cause.Failure(failure)
    }

    private data class Root(override val code: String) : Failure
    private data class Child(override val code: String, override val cause: Failure.Cause) : Failure
}

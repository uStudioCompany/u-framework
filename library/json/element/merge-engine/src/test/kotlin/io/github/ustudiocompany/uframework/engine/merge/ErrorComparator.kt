package io.github.ustudiocompany.uframework.engine.merge

import io.github.ustudiocompany.uframework.failure.Failure
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal infix fun <T : Failure, R : Failure> T.failuresShouldBeEqual(other: R) {
    if (this === other) return

    assertSoftly {
        withClue("code") {
            this.code shouldBe other.code
        }

        withClue("description") {
            this.description shouldBe other.description
        }

        withClue("cause") {
            this.cause shouldBe other.cause
        }

        withClue("details") {
            this.details shouldContainExactly other.details
        }
    }
}

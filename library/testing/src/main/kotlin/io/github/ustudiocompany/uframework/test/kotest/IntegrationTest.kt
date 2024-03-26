package io.github.ustudiocompany.uframework.test.kotest

import io.kotest.core.spec.style.FreeSpec

public abstract class IntegrationTest : FreeSpec({ tags(TestTags.All, TestTags.Integration) })

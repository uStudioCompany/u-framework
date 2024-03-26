package io.github.ustudiocompany.uframework.test.kotest

import io.kotest.core.spec.style.FreeSpec

public abstract class ComponentTest : FreeSpec({ tags(TestTags.All, TestTags.Component) })

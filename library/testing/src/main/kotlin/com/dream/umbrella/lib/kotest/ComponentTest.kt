package com.dream.umbrella.lib.kotest

import io.kotest.core.spec.style.FreeSpec

public abstract class ComponentTest : FreeSpec({ tags(TestTags.All, TestTags.Component) })

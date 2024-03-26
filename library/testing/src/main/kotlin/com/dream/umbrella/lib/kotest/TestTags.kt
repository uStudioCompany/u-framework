package com.dream.umbrella.lib.kotest

import io.kotest.core.Tag

public object TestTags {
    public object All : Tag()
    public object Unit : Tag()
    public object Component : Tag()
    public object Integration : Tag()
}

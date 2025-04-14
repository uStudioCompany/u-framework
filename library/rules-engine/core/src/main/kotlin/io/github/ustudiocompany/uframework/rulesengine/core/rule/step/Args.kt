package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value

@JvmInline
public value class Args private constructor(public val get: List<Arg>) {

    public companion object {
        public val NONE: Args = Args(emptyList())

        @JvmStatic
        public operator fun invoke(args: List<Arg>): Args = if (args.isEmpty()) NONE else Args(args)

        public operator fun invoke(vararg params: Pair<String, Value>): Args =
            invoke(params.map { (name, value) -> Arg(name, value) })
    }
}

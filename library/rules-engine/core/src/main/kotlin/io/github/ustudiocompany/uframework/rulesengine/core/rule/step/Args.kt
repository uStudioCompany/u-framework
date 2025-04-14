package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

@JvmInline
public value class Args private constructor(public val get: List<Arg>) {

    public companion object {
        public val NONE: Args = Args(emptyList())

        @JvmStatic
        public operator fun invoke(args: List<Arg>): Args = if (args.isEmpty()) NONE else Args(args)
    }
}

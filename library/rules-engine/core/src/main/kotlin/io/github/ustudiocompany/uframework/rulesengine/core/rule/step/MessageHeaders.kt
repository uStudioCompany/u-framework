package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

@JvmInline
public value class MessageHeaders private constructor(public val get: List<MessageHeader>) {

    public companion object {
        public val NONE: MessageHeaders = MessageHeaders(emptyList())

        @JvmStatic
        public operator fun invoke(headers: List<MessageHeader>): MessageHeaders =
            if (headers.isEmpty()) NONE else MessageHeaders(headers)
    }
}

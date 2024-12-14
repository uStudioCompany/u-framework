package io.github.ustudiocompany.uframework.messaging.header.type

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.ACTUAL_VALUE_DETAIL_KEY
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.PATTERN_DETAIL_KEY
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

public class MessageVersion private constructor(
    public val major: Int,
    public val minor: Int,
    public val patch: Int
) : Comparable<MessageVersion> {

    override fun compareTo(other: MessageVersion): Int {
        val byMajor = major.compareTo(other.major)
        if (byMajor != 0) return byMajor

        val byMinor = minor.compareTo(other.minor)
        return if (byMinor != 0) byMinor else patch.compareTo(other.patch)
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other is MessageVersion && this.major == other.major && this.minor == other.minor && this.patch == other.patch)

    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + patch
        return result
    }

    override fun toString(): String = "$major.$minor.$patch"

    public sealed class Errors : TypeFailure<MessageVersion> {
        override val type: TypeOf<MessageVersion>
            get() = typeOf<MessageVersion>()

        public sealed class InvalidVersionSegmentValue : Errors() {
            override val domain: String
                get() = super.domain + "-SEGMENT"

            public class Major(public val value: Int) : InvalidVersionSegmentValue() {
                override val number: String = "1"
                override val description: String
                    get() = "Invalid value `$value` of the major segment."
            }

            public class Minor(public val value: Int) : InvalidVersionSegmentValue() {
                override val number: String = "2"
                override val description: String
                    get() = "Invalid value `$value` of the minor segment."
            }

            public class Patch(public val value: Int) : InvalidVersionSegmentValue() {
                override val number: String = "3"
                override val description: String
                    get() = "Invalid value `$value` of the patch segment."
            }
        }

        public class InvalidFormat(value: String) : Errors() {
            override val number: String = "1"

            override val description: String = "The value `$value` mismatches the pattern `$PATTERN`."

            override val details: Failure.Details = Failure.Details.of(
                PATTERN_DETAIL_KEY to PATTERN,
                ACTUAL_VALUE_DETAIL_KEY to value
            )
        }
    }

    public companion object {

        public fun of(major: Int, minor: Int = 0, patch: Int = 0): ResultK<MessageVersion, Errors> =
            when {
                major < 0 -> Errors.InvalidVersionSegmentValue.Major(value = major).asFailure()
                minor < 0 -> Errors.InvalidVersionSegmentValue.Minor(value = minor).asFailure()
                patch < 0 -> Errors.InvalidVersionSegmentValue.Patch(value = patch).asFailure()
                else -> MessageVersion(major = major, minor = minor, patch = patch).asSuccess()
            }

        public fun of(value: String): ResultK<MessageVersion, Errors> =
            if (value.matches(regex)) {
                val segments: List<String> = value.split(".")
                MessageVersion(major = major(segments), minor = minor(segments), patch = patch(segments)).asSuccess()
            } else
                Errors.InvalidFormat(value = value).asFailure()

        private fun major(segments: List<String>): Int = segments[MAJOR_INDEX].toInt()

        private fun minor(segments: List<String>): Int =
            if (segments.size >= MINOR_INDEX + 1) segments[MINOR_INDEX].toInt() else DEFAULT_VALUE_VERSION_SEGMENT

        private fun patch(segments: List<String>): Int =
            if (segments.size == PATCH_INDEX + 1) segments[PATCH_INDEX].toInt() else DEFAULT_VALUE_VERSION_SEGMENT

        private const val PATTERN: String = """^(0|[1-9]\d*)(\.(0|[1-9]\d*)(\.(0|[1-9]\d*))?)?$"""
        private val regex = PATTERN.toRegex()

        private const val MAJOR_INDEX = 0
        private const val MINOR_INDEX = 1
        private const val PATCH_INDEX = 2
        private const val DEFAULT_VALUE_VERSION_SEGMENT = 0
    }
}

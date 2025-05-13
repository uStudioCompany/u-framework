/*
 * Copyright 2021-2024 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ustudiocompany.uframework.json.element.merge

import io.github.ustudiocompany.uframework.json.element.merge.strategy.path.AttributePath
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class AttributePathTest : FreeSpec() {

    init {

        "The AttributePath type" - {

            "when the path is empty" - {
                val path = AttributePath.None

                "should be empty" {
                    path.isEmpty shouldBe true
                }

                "the 'toString() method should return the path prefix" {
                    path.toString() shouldBe "$"
                }
            }

            "when the path is non-empty" - {
                val path = AttributePath(USER).append(PHONE)

                "should be non-empty" {
                    path.isEmpty shouldBe false
                }

                "the 'toString() method should return a string representation of the path" {
                    path.toString() shouldBe "$.$USER.$PHONE"
                }
            }

            "when the path is created without elements" - {
                val path = AttributePath()

                "then the path should be equal to the empty path" {
                    path shouldBe AttributePath.None
                }
            }

            "should comply with equals() and hashCode() contract" {
                val x = AttributePath(USER).append(PHONE)
                val y = AttributePath(USER).append(PHONE)
                val z = AttributePath(USER).append(PHONE)
                val others = listOf(
                    AttributePath.None,
                    AttributePath(USER),
                    AttributePath(PHONE),
                    AttributePath(PHONE).append(USER)
                )

                assertSoftly {
                    withClue("reflexive") {
                        withClue("$x equal to $x") {
                            x.equals(x) shouldBe true
                        }
                        withClue("hashCode") {
                            x.hashCode() shouldBe x.hashCode()
                        }
                    }

                    withClue("symmetric") {
                        withClue("$x equal to $y") {
                            x.equals(y) shouldBe true
                        }
                        withClue("$y equal to $x") {
                            y.equals(x) shouldBe true
                        }
                        withClue("hashCode") {
                            x.hashCode() shouldBe y.hashCode()
                            y.hashCode() shouldBe x.hashCode()
                        }
                    }

                    withClue("transitive") {
                        withClue("$x equal to $y") {
                            x.equals(y) shouldBe true
                        }
                        withClue("$y equal to $z") {
                            y.equals(z) shouldBe true
                        }
                        withClue("$x equal to $z") {
                            x.equals(z) shouldBe true
                        }
                        withClue("hashCode") {
                            x.hashCode() shouldBe y.hashCode()
                            y.hashCode() shouldBe z.hashCode()
                            x.hashCode() shouldBe z.hashCode()
                        }
                    }

                    withClue("$x never equal to null") {
                        @Suppress("EqualsNullCall")
                        x.equals(null) shouldBe false
                    }

                    withClue("$x never equal to Any") {
                        x.equals(Any()) shouldBe false
                    }

                    others.forEach { other ->
                        withClue("$x never equal to $other") {
                            x.equals(other) shouldBe false
                        }
                        withClue("$other never equal to $x") {
                            other.equals(x) shouldBe false
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private const val USER = "users"
        private const val PHONE = "phone"
    }
}

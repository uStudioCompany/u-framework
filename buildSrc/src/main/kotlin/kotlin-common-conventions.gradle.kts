import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")

    id("detekt-conventions")
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
    testImplementation("io.kotest.extensions:kotest-extensions-pitest:1.2.0")
}

tasks {

    withType<KotlinCompile>()
        .configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(Configuration.JVM.target))
                suppressWarnings = false
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all",
                    "-Xexplicit-api=strict",
                    "-Xcontext-receivers"
                )
            }
        }

    withType<Test> {
        useJUnitPlatform()
        systemProperties = System.getProperties().asIterable().associate { it.key.toString() to it.value }
    }
}

configure<DetektExtension> {
    source.setFrom(project.files("src/main/kotlin", "src/test/kotlin"))
}

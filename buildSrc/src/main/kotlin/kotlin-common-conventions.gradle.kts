import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")

    id("detekt-conventions")
    id("org.jetbrains.kotlinx.kover")
}

tasks {

    withType<KotlinCompile>()
        .configureEach {
            kotlinOptions {
                jvmTarget = Configuration.JVM.target
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
        if (!project.hasProperty("integration-tests"))
            exclude("**/**IT.*")
    }
}

configure<DetektExtension> {
    source.setFrom(project.files("src/main/kotlin", "src/test/kotlin"))
}

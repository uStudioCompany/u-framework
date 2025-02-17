buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.15.0")
    }
}

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.plugin.binary.compatibility.validator)
    implementation(libs.gradle.plugin.detekt)
    implementation(libs.gradle.plugin.dokka)
    implementation(libs.gradle.plugin.git.version)
    implementation(libs.gradle.plugin.knit)
    implementation(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.kover)
    implementation(libs.gradle.plugin.license.report)
    implementation(libs.gradle.plugin.pitest)
}

import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    id("kotlin-library-conventions")
    id("pitest-conventions")
}

dependencies {
    implementation(project(":failure-library"))
    implementation(project(":retry-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }
    implementation(libs.postgresql)

    /* Tests */
    testImplementation(project(":jdbc-test-library"))
    testImplementation(project(":testing-library"))

    testImplementation(libs.airflux.commons.types.kotest.matchers) { isChanging = true }
    testImplementation(libs.kotest.datatest)
    testImplementation(libs.kotest.junit5)
}

configure<PitestPluginExtension> {
    mainSourceSets.set(listOf(project.sourceSets.main.get()))
}

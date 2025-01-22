import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    id("kotlin-library-conventions")
    id("pitest-conventions")
}

dependencies {
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }

    implementation(project(":jdbc-core-library"))

    /* Database */
    api(libs.bundles.database)

    /* Tests */
    testImplementation(project(":jdbc-test-library"))
    testImplementation(project(":jdbc-kotest-matchers-library"))
    testImplementation(libs.bundles.logging)
    testImplementation(libs.airflux.commons.types.kotest.matchers) {
        isChanging = true
    }
    testImplementation(project(":testing-library"))
}

configure<PitestPluginExtension> {
    mainSourceSets.set(listOf(project.sourceSets.main.get()))
}

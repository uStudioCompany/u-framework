import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    id("kotlin-library-conventions")
    id("pitest-conventions")
}

dependencies {
    api(libs.airflux.commons.types) {
        isChanging = true
    }

    /* Failure libs */
    api(project(":failure-library"))

    /* Database */
    api(libs.bundles.database)

    //TODO DELETE
    implementation(libs.bundles.kotest)

    /* Tests */
    testImplementation(project(":jdbc-std-library"))
    testImplementation(project(":jdbc-testcontainers-library"))
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

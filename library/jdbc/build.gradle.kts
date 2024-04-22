import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    id("kotlin-library-conventions")
    id("pitest-conventions")
}

dependencies {
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }

    /* Failure libs */
    implementation(project(":failure-library"))

    /* Retry libs */
    api(project(":retry-library"))

    /* Database */
    api(libs.bundles.database)

    /* Tests */
    testImplementation(project(":jdbc-test-library"))
    testImplementation(libs.bundles.logging)
    testImplementation(libs.airflux.commons.types.test) {
        isChanging = true
    }
    testImplementation(project(":testing-library"))
}

configure<PitestPluginExtension> {
    mainSourceSets.set(listOf(project.sourceSets.main.get()))
}

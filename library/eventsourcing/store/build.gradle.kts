plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":event-sourcing-modeling-library"))
    implementation(project(":failure-library"))
    implementation(project(":messaging-core-types-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }

    /* Test */
    testImplementation(project(":testing-library"))

    testImplementation(libs.airflux.commons.types.kotest.matchers) { isChanging = true }
    testImplementation(libs.bundles.mockito)
    testImplementation(libs.kotest.datatest)
    testImplementation(libs.kotest.junit5)
}

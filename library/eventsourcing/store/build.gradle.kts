plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.airflux.functional.core)

    implementation(project(":failure-library"))
    implementation(project(":event-sourcing-modeling-library"))
    implementation(project(":diagnostic-context-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":messaging-core-types-library"))
    implementation(project(":jdbc-library"))

    /* Test */
    testImplementation(libs.airflux.functional.test)
    testImplementation(libs.bundles.jackson)
    testImplementation(project(":jdbc-test-library"))
    testImplementation(project(":testing-library"))
}

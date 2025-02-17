plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":event-sourcing-modeling-library"))

    implementation(libs.airflux.commons.types) {
        isChanging = true
    }
    implementation(project(":failure-library"))
    implementation(project(":diagnostic-context-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":messaging-core-types-library"))

    /* Test */
    testImplementation(libs.airflux.commons.types.kotest.matchers) {
        isChanging = true
    }
    testImplementation(project(":testing-library"))
}

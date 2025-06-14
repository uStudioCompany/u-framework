plugins {
    id("kotlin-library-conventions")
}

dependencies {
    /* Kotlin */
    implementation(libs.coroutines.core)

    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":diagnostic-context-library"))
    implementation(project(":failure-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":messaging-core-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }
}

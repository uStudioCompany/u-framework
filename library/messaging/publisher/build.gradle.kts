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
    implementation(project(":logging-slf4j-library"))
    implementation(project(":logging-formatter-json-library"))
    implementation(project(":messaging-core-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }
}

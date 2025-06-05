plugins {
    id("kotlin-library-conventions")
}

dependencies {
    /* Kotlin */
    implementation(libs.coroutines.core)

    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":diagnostic-context-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":logging-formatter-json-library"))
    implementation(project(":logging-slf4j-library"))
    implementation(project(":messaging-core-library"))

    testImplementation(project(":testing-library"))

    testImplementation(libs.bundles.mockito)
    testImplementation(libs.kotest.junit5)
}

plugins {
    id("kotlin-library-conventions")
}

dependencies {

    /* Messaging libs */
    implementation(project(":messaging-core-library"))
    api(project(":messaging-core-types-library"))

    /* Libs section */
    implementation(libs.airflux.functional.core)

    /* Failure libs */
    implementation(project(":failure-library"))

    /* Logging libs */
    implementation(project(":diagnostic-context-library"))
    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":logging-formatter-json-library"))
    implementation(project(":logging-slf4j-library"))

    /* Test section */
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.mockito)
}

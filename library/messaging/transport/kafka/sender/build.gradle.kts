plugins {
    id("kotlin-library-conventions")
}

dependencies {
    /* Kotlin */
    implementation(libs.coroutines.core)

    /* Libs section */
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }

    /* Failure libs */
    implementation(project(":failure-library"))

    /* Logging libs */
    implementation(project(":diagnostic-context-library"))
    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":logging-formatter-json-library"))
    implementation(project(":logging-slf4j-library"))

    /* Messaging libs */
    implementation(project(":messaging-core-library"))
    api(libs.kafka.client)
}

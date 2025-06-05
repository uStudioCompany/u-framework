plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":diagnostic-context-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":messaging-core-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }
    implementation(libs.kafka.client)
}

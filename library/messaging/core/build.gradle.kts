plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":diagnostic-context-library"))
    implementation(project(":failure-library"))
    implementation(project(":logging-api-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }
}

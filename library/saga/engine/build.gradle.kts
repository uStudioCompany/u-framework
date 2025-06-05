plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(project(":diagnostic-context-library"))
    implementation(project(":failure-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":messaging-core-library"))
    implementation(project(":messaging-core-types-library"))
    implementation(project(":saga-core-library"))
    implementation(project(":utils-library"))

    implementation(libs.airflux.commons.collections) { isChanging = true }
    implementation(libs.airflux.commons.types) { isChanging = true }
}

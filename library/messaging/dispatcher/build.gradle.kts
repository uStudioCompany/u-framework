plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }

    /* u-framework */
    implementation(project(":failure-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":diagnostic-context-library"))
    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":messaging-core-library"))
    api(project(":messaging-router-library"))
    implementation(project(":messaging-dead-letter-channel-library"))
}

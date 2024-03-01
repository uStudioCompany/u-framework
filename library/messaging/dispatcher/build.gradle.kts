import java.net.URI

plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
    maven {
        url = URI("https://jitpack.io")
    }
}

dependencies {
    implementation(libs.airflux.functional.core)

    /* u-framework */
    implementation(project(":failure-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":diagnostic-context-library"))
    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":messaging-core-library"))
    api(project(":messaging-router-library"))
    implementation(project(":messaging-dead-letter-channel-library"))

    /* Test */
    testImplementation(libs.bundles.kotest)
}

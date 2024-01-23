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
    implementation(project(":messaging-core-types-library"))
    implementation(project(":messaging-core-headers-library"))
    implementation(project(":messaging-publisher-library"))


    /* Test */
    testImplementation(libs.bundles.kotest)
}

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

    api(project(":saga-core-library"))

    /* Libs section */
    implementation(libs.airflux.functional.core)

    /* Utils libs */
    implementation(project(":utils-library"))

    /* Failure lib */
    implementation(project(":failure-library"))

    /* Logging lib */
    implementation(project(":diagnostic-context-library"))
    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":logging-slf4j-library"))

    /* Messaging lib */
    implementation(project(":messaging-core-library"))
    implementation(project(":messaging-core-types-library"))
    implementation(project(":messaging-publisher-library"))

    /* Test */
    testImplementation(libs.bundles.kotest)
}

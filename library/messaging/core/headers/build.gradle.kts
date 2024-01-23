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

    /* Libs section */
    implementation(libs.airflux.functional.core)

    /* Utils libs */
    implementation(project(":utils-library"))

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
    api(project(":messaging-core-types-library"))

    /* Test section */
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.mockito)
}

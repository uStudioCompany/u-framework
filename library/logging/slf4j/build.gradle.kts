plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {

    /* Libs section */
    implementation(project(":diagnostic-context-library"))
    api(project(":logging-api-library"))
    implementation(libs.slf4j.api)
    implementation(libs.logback.core)
    implementation(libs.logback.classic)

    /* Test section */
    testImplementation(libs.bundles.kotest)
}

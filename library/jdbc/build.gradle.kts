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

    /* Failure libs */
    implementation(project(":failure-library"))

    /* Retry libs */
    api(project(":retry-library"))

    /* Database */
    api(libs.bundles.database)

    /* Tests */
    testImplementation(project(":jdbc-test-library"))
    testImplementation(libs.bundles.logging)
    testImplementation(libs.airflux.functional.test)
    testImplementation(libs.bundles.kotest)
}
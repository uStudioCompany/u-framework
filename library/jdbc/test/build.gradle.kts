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

    /* Database */
    implementation(libs.bundles.database)
    api(libs.bundles.testcontainers.postgres)

    implementation(libs.bundles.kotest)
}

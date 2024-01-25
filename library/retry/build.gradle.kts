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

    /* Test section */
    testImplementation(libs.bundles.kotest)
}

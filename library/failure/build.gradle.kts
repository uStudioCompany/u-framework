plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {

    /* Test section */
    testImplementation(libs.bundles.kotest)
}

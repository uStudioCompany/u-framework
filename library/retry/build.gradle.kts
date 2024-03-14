plugins {
    id("kotlin-library-conventions")
}

dependencies {

    /* Libs section */
    implementation(libs.airflux.functional.core)

    /* Test section */
    testImplementation(libs.bundles.kotest)
}

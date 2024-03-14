plugins {
    id("kotlin-library-conventions")
}

dependencies {

    /* Libs section */
    implementation(libs.airflux.functional.core)

    /* Failure libs */
    implementation(project(":failure-library"))

    /* Test section */
    testImplementation(libs.bundles.kotest)
}

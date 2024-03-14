plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(libs.airflux.functional.core)

    /* Database */
    implementation(libs.bundles.database)
    api(libs.bundles.testcontainers.postgres)

    implementation(libs.bundles.kotest)
}

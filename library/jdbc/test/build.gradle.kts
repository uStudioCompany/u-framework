plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(libs.airflux.commons.types)

    /* Database */
    implementation(libs.bundles.database)
    api(libs.bundles.testcontainers.core)
    api(libs.bundles.testcontainers.postgresql)
    implementation(libs.bundles.kotest)
}

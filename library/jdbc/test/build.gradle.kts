plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(libs.airflux.functional.core)

    /* Database */
    implementation(libs.bundles.database)
    api(libs.bundles.testcontainers.core)
    api(libs.bundles.testcontainers.postgresql)

    implementation(project(":testing-library"))
}

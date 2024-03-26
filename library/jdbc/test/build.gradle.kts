plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(libs.airflux.functional.core)

    /* Database */
    implementation(libs.bundles.database)
    api(libs.bundles.testcontainers.postgres)

    implementation(project(":testing-library"))
}

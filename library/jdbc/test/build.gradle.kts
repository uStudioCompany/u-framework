plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(libs.bundles.database)
    implementation(libs.bundles.testcontainers.postgresql)
    implementation(libs.kotest.junit5)
}

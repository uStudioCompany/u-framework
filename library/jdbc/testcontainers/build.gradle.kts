plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(libs.bundles.testcontainers.postgresql)
    implementation(libs.kotest.junit5)
    implementation(libs.kotest.testcontainers)
    implementation(libs.postgresql)
}

plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(project(":failure-library"))
    implementation(project(":messaging-core-library"))
    implementation(project(":messaging-core-types-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }
}

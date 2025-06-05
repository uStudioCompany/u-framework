plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(project(":failure-library"))
    implementation(project(":utils-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }
}

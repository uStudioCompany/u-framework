plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(project(":jdbc-core-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }
    implementation(libs.airflux.commons.types.kotest.matchers) { isChanging = true }
}

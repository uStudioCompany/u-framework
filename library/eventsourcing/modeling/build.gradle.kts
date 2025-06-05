plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":failure-library"))
    implementation(project(":messaging-core-types-library"))

    implementation(libs.airflux.commons.types) { isChanging = true }
}

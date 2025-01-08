plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.bundles.kotest)
    testImplementation(project(":testing-library"))
}

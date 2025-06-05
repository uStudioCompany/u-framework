plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":diagnostic-context-library"))
    implementation(project(":logging-api-library"))

    testImplementation(project(":testing-library"))

    testImplementation(libs.kotest.junit5)
}

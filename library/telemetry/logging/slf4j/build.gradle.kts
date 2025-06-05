plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":diagnostic-context-library"))
    implementation(project(":logging-api-library"))

    implementation(libs.logback.classic)
    implementation(libs.slf4j.api)
}

plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":failure-library"))
    implementation(project(":diagnostic-context-library"))
}

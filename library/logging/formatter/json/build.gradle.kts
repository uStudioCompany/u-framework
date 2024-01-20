plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {

    /* Libs section */
    implementation(project(":diagnostic-context-library"))
    implementation(project(":logging-api-library"))

    /* Test section */
    testImplementation(libs.bundles.kotest)
}

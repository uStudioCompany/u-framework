plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.airflux.functional.core)

    implementation(project(":failure-library"))
    implementation(project(":messaging-core-types-library"))

    /* Test */
    testImplementation(project(":testing-library"))
}

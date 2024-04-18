plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }

    implementation(project(":failure-library"))
    implementation(project(":messaging-core-types-library"))

    /* Test */
    testImplementation(project(":testing-library"))
}

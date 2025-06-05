plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {

    /* Kotlin */
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(project(":failure-library"))
    implementation(project(":json-element"))
    implementation(project(":json-path"))

    implementation(libs.airflux.commons.types) { isChanging = true }

    testImplementation(project(":testing-library"))

    testImplementation(libs.bundles.kotest)
    testImplementation(libs.knit.test)
}

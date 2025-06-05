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

    implementation(project(":json-element"))

    implementation(libs.bundles.jackson)

    testImplementation(project(":testing-library"))

    testImplementation(libs.knit.test)
    testImplementation(libs.kotest.datatest)
    testImplementation(libs.kotest.junit5)
}

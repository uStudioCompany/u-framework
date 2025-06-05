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
    implementation(project(":rules-engine-core"))
    implementation(project(":rules-engine-feel"))
    implementation(project(":rules-engine-parser-core"))

    implementation(libs.airflux.commons.types) { isChanging = true }
    implementation(libs.bundles.jackson)

    testImplementation(project(":testing-library"))

    testImplementation(libs.airflux.commons.types.kotest.matchers) { isChanging = true }
    testImplementation(libs.knit.test)
    testImplementation(libs.kotest.datatest)
    testImplementation(libs.kotest.junit5)
}

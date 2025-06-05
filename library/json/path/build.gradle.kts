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

    implementation(libs.airflux.commons.types) { isChanging = true }
    implementation(libs.bundles.jackson)
    implementation(libs.json.path)

    testImplementation(project(":testing-library"))

    testImplementation(libs.airflux.commons.types.kotest.matchers) { isChanging = true }
    testImplementation(libs.knit.test)
    testImplementation(libs.kotest.junit5)
}

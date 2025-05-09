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
    implementation(project(":rules-engine-core"))
    implementation(project(":rules-engine-feel"))
    implementation(project(":rules-engine-path"))
    implementation(project(":rules-engine-parser-core"))
    implementation(project(":failure-library"))

    /* Libs */
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }
    implementation(libs.bundles.jackson)

    /* Test */
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.knit.test)
    testImplementation(project(":testing-library"))
    testImplementation(libs.airflux.commons.types.kotest.matchers) {
        isChanging = true
    }
}

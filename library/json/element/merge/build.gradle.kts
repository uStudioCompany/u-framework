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
    implementation(project(":json-element-merge-strategy"))

    implementation(libs.airflux.commons.types) { isChanging = true }

    /* Test */
    testImplementation(project(":json-element-jackson-parser"))
    testImplementation(project(":json-element-merge-strategy-parser-core"))
    testImplementation(project(":json-element-merge-strategy-parser-jackson"))
    testImplementation(project(":testing-library"))

    testImplementation(libs.airflux.commons.types.kotest.matchers) { isChanging = true }
    testImplementation(libs.bundles.jackson)
    testImplementation(libs.knit.test)
    testImplementation(libs.kotest.junit5)
}

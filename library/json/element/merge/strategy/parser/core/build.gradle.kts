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
    implementation(project(":json-element-merge-strategy"))

    /* Libs */
    implementation(libs.airflux.commons.types) { isChanging = true }
    implementation(libs.bundles.jackson)

    testImplementation(libs.knit.test)
    testImplementation(libs.kotest.junit5)
}

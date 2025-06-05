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

    implementation(libs.airflux.commons.types) { isChanging = true }
    implementation(libs.feel.engine)

    testImplementation(project(":failure-library"))
    testImplementation(project(":testing-library"))

    testImplementation(libs.airflux.commons.types.kotest.matchers) { isChanging = true }
    testImplementation(libs.knit.test)
    testImplementation(libs.kotest.junit5)
}

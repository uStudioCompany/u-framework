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

    /* Libs */
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }

    /* Test */
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.knit.test)
    testImplementation(libs.airflux.commons.types.kotest.matchers) {
        isChanging = true
    }
    testImplementation(project(":testing-library"))
}

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
    api(project(":json-element-merge-strategy"))
    implementation(project(":failure-library"))

    /* Libs */
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }

    /* Test */
    testImplementation(project(":json-element-jackson-parser"))
    testImplementation(libs.bundles.jackson)

    testImplementation(libs.bundles.kotest)
    testImplementation(libs.knit.test)
    testImplementation(libs.airflux.commons.types.kotest.matchers) {
        isChanging = true
    }
    testImplementation(project(":testing-library"))
}

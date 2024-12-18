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

    /* Libs */
    implementation(libs.feel.engine)
    implementation(libs.json.path)
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }

    /* Test */
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.knit.test)
}

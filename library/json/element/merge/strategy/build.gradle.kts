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

    /* Test */
    testImplementation(libs.bundles.jackson)
    testImplementation(libs.knit.test)
    testImplementation(libs.kotest.junit5)
}

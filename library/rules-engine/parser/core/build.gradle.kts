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
    implementation(project(":rules-engine-core"))

    implementation(libs.airflux.commons.types) { isChanging = true }

    testImplementation(libs.knit.test)
}

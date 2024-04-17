plugins {
    id("kotlin-library-conventions")
}

dependencies {

    /* Libs section */
    implementation(libs.airflux.commons.types)

    /* Failure libs */
    implementation(project(":failure-library"))
}

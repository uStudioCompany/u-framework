plugins {
    id("kotlin-library-conventions")
}

dependencies {

    /* Libs section */
    implementation(libs.airflux.commons.types) {
        isChanging = true
    }

    /* Failure libs */
    implementation(project(":failure-library"))
}

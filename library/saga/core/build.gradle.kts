plugins {
    id("kotlin-library-conventions")
}

dependencies {

    implementation(project(":utils-library"))

    /* Libs section */
    implementation(libs.airflux.functional.core)

    /* Utils libs */
    implementation(project(":utils-library"))

    /* Failure lib */
    implementation(project(":failure-library"))

    /* Logging lib */
    implementation(project(":diagnostic-context-library"))
    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":logging-slf4j-library"))

    /* Messaging lib */
    implementation(project(":messaging-core-library"))
    implementation(project(":messaging-core-types-library"))
}

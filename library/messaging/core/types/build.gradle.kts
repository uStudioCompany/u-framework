plugins {
    id("kotlin-library-conventions")
}

dependencies {

    /* Libs section */
    implementation(libs.airflux.functional.core)

    /* Utils libs */
    api(project(":utils-library"))

    /* Failure libs */
    implementation(project(":failure-library"))

    /* Logging libs */
    implementation(project(":diagnostic-context-library"))
    implementation(project(":diagnostic-context-extension-library"))
    implementation(project(":logging-api-library"))
    implementation(project(":logging-formatter-json-library"))
    implementation(project(":logging-slf4j-library"))
}

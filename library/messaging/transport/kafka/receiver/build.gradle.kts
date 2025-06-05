plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(project(":messaging-core-library"))

    implementation(libs.kafka.client)
}

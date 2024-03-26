plugins {
    id("kotlin-library-conventions")
}

repositories {
    mavenCentral()
}

dependencies {

    /* Libs section */
    implementation(project(":diagnostic-context-library"))
}

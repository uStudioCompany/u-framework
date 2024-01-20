plugins {
    id("kover-merge-conventions")
    id("licenses-conventions")
}

repositories {
    mavenCentral()
}

subprojects {
    repositories {
        mavenCentral()
    }

    version = "0.0.1-SNAPSHOT"
    group = "io.github.ustudiocompany"
}

dependencies {
    kover(project(":utils-library"))
    kover(project(":failure-library"))
    kover(project(":diagnostic-context-library"))
    kover(project(":diagnostic-context-extension-library"))
    kover(project(":logging-api-library"))
    kover(project(":logging-formatter-json-library"))
    kover(project(":logging-slf4j-library"))
    kover(project(":messaging-core-library"))
    kover(project(":messaging-listener-library"))
    kover(project(":messaging-publisher-library"))
    kover(project(":messaging-kafka-receiver-library"))
    kover(project(":messaging-kafka-sender-library"))
    kover(project(":saga-messaging-types-library"))
    kover(project(":saga-messaging-headers-library"))
    kover(project(":saga-engine-library"))
}

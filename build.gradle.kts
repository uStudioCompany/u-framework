import java.net.URI

plugins {
    id("kover-merge-conventions")
    id("licenses-conventions")
}

repositories {
    mavenCentral()
    maven {
        url = URI("https://nexus.eprocurement.systems/repository/maven-snapshots/")
    }
}

subprojects {
    repositories {
        mavenCentral()
        maven {
            url = URI("https://nexus.eprocurement.systems/repository/maven-snapshots/")
        }
    }

    version = "0.0.1-SNAPSHOT"
    group = "io.github.ustudiocompany"

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
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
    kover(project(":saga-engine-library"))
}

import java.net.URI

plugins {
    id("kover-merge-conventions")
    id("licenses-conventions")
    id("git-info-conventions")
}

repositories {
    mavenCentral()
    maven {
        url = URI("https://nexus.devfactory-ustudio.localcert.net/repository/maven-snapshots/")
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = URI("https://nexus.devfactory-ustudio.localcert.net/repository/maven-snapshots/")
        }
    }

    version = "0.0.1-alpha.1-SNAPSHOT"
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
    kover(project(":messaging-core-types-library"))
    kover(project(":messaging-core-headers-library"))
    kover(project(":messaging-listener-library"))
    kover(project(":messaging-publisher-library"))
    kover(project(":messaging-kafka-receiver-library"))
    kover(project(":messaging-router-library"))
    kover(project(":messaging-dispatcher-library"))
    kover(project(":messaging-dead-letter-channel-library"))
    kover(project(":messaging-kafka-receiver-library"))
    kover(project(":messaging-kafka-sender-library"))
    kover(project(":saga-core-library"))
    kover(project(":saga-engine-library"))
    kover(project(":retry-library"))
    kover(project(":jdbc-library"))
    kover(project(":jdbc-test-library"))
    kover(project(":event-sourcing-modeling-library"))
    kover(project(":event-sourcing-store-library"))
}

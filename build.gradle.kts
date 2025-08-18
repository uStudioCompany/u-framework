import java.net.URI

plugins {
    id("kover-merge-conventions")
    id("licenses-conventions")
    id("git-info-conventions")
    id("com.autonomousapps.dependency-analysis") version "1.28.0"
}

repositories {
    mavenCentral()
    maven {
        url = URI("https://nexus.fabrica.ustudio.company/repository/maven-snapshots/")
    }
    maven {
        url = URI("https://nexus.fabrica.ustudio.company/repository/maven-releases/")
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = URI("https://nexus.fabrica.ustudio.company/repository/maven-snapshots/")
        }
        maven {
            url = URI("https://nexus.fabrica.ustudio.company/repository/maven-releases/")
        }
    }

    version = "0.0.2-SNAPSHOT"
    group = "io.github.ustudiocompany"

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}

dependencies {
    kover(project(":diagnostic-context-extension-library"))
    kover(project(":diagnostic-context-library"))
    kover(project(":event-sourcing-modeling-library"))
    kover(project(":event-sourcing-store-library"))
    kover(project(":failure-library"))
    kover(project(":jdbc-core-library"))
    kover(project(":jdbc-library"))
    kover(project(":jdbc-std-library"))
    kover(project(":jdbc-test-library"))
    kover(project(":json-element"))
    kover(project(":json-element-jackson-parser"))
    kover(project(":json-element-merge"))
    kover(project(":json-element-merge-strategy"))
    kover(project(":json-element-merge-strategy-parser-core"))
    kover(project(":json-element-merge-strategy-parser-jackson"))
    kover(project(":json-path"))
    kover(project(":logging-api-library"))
    kover(project(":logging-formatter-json-library"))
    kover(project(":logging-slf4j-library"))
    kover(project(":logging-slf4j-extension-library"))
    kover(project(":messaging-core-headers-library"))
    kover(project(":messaging-core-library"))
    kover(project(":messaging-core-types-library"))
    kover(project(":messaging-dead-letter-channel-library"))
    kover(project(":messaging-dispatcher-library"))
    kover(project(":messaging-kafka-receiver-library"))
    kover(project(":messaging-kafka-receiver-library"))
    kover(project(":messaging-kafka-sender-library"))
    kover(project(":messaging-listener-library"))
    kover(project(":messaging-publisher-library"))
    kover(project(":messaging-router-library"))
    kover(project(":retry-library"))
    kover(project(":rules-engine-core"))
    kover(project(":rules-engine-executor"))
    kover(project(":rules-engine-feel"))
    kover(project(":rules-engine-feel-functions"))
    kover(project(":rules-engine-parser-core"))
    kover(project(":rules-engine-parser-jackson"))
    kover(project(":saga-core-library"))
    kover(project(":saga-engine-library"))
    kover(project(":utils-library"))
}

rootProject.name = "u-framework"

/* Testing */
include(":testing-library")
project(":testing-library").projectDir = file("./library/testing")

/* Utils */
include(":utils-library")
project(":utils-library").projectDir = file("./library/utils")

/* Failure */
include(":failure-library")
project(":failure-library").projectDir = file("./library/failure")

/* Telemetry */

/* Diagnostic-Context */
include(":diagnostic-context-library")
project(":diagnostic-context-library").projectDir = file("./library/telemetry/diagnostic/context")

include(":diagnostic-context-extension-library")
project(":diagnostic-context-extension-library").projectDir = file("./library/telemetry/diagnostic/context/extension")

/* Logging */
include(":logging-api-library")
project(":logging-api-library").projectDir = file("./library/telemetry/logging/api")

include(":logging-formatter-json-library")
project(":logging-formatter-json-library").projectDir = file("./library/telemetry/logging/formatter/json")

include(":logging-slf4j-library")
project(":logging-slf4j-library").projectDir = file("./library/telemetry/logging/slf4j")

/* Messaging */
include(":messaging-core-library")
project(":messaging-core-library").projectDir = file("./library/messaging/core")

include(":messaging-core-types-library")
project(":messaging-core-types-library").projectDir = file("./library/messaging/core/types")

include(":messaging-core-headers-library")
project(":messaging-core-headers-library").projectDir = file("./library/messaging/core/headers")

include(":messaging-router-library")
project(":messaging-router-library").projectDir = file("./library/messaging/router")

include(":messaging-dispatcher-library")
project(":messaging-dispatcher-library").projectDir = file("./library/messaging/dispatcher")

include(":messaging-listener-library")
project(":messaging-listener-library").projectDir = file("./library/messaging/listener")

include(":messaging-publisher-library")
project(":messaging-publisher-library").projectDir = file("./library/messaging/publisher")

include(":messaging-dead-letter-channel-library")
project(":messaging-dead-letter-channel-library").projectDir = file("./library/messaging/channels/deadletter")

include(":messaging-kafka-receiver-library")
project(":messaging-kafka-receiver-library").projectDir = file("./library/messaging/transport/kafka/receiver")

include(":messaging-kafka-sender-library")
project(":messaging-kafka-sender-library").projectDir = file("./library/messaging/transport/kafka/sender")

/* Saga */
include(":saga-core-library")
project(":saga-core-library").projectDir = file("./library/saga/core")

include(":saga-engine-library")
project(":saga-engine-library").projectDir = file("./library/saga/engine")

/* Retry */
include(":retry-library")
project(":retry-library").projectDir = file("./library/retry")

/* Databases */

/* JDBC */
include(":jdbc-library")
project(":jdbc-library").projectDir = file("./library/jdbc")

include(":jdbc-core-library")
project(":jdbc-core-library").projectDir = file("./library/jdbc/core")

include(":jdbc-std-library")
project(":jdbc-std-library").projectDir = file("./library/jdbc/std")

include(":jdbc-testcontainers-library")
project(":jdbc-testcontainers-library").projectDir = file("./library/jdbc/testcontainers")

include(":jdbc-kotest-matchers-library")
project(":jdbc-kotest-matchers-library").projectDir = file("./library/jdbc/kotest")

include(":jdbc-test-library")
project(":jdbc-test-library").projectDir = file("./library/jdbc/test")

/* Event-Sourcing */
include(":event-sourcing-modeling-library")
project(":event-sourcing-modeling-library").projectDir = file("./library/eventsourcing/modeling")

include(":event-sourcing-store-library")
project(":event-sourcing-store-library").projectDir = file("./library/eventsourcing/store")

/* Rules Engine */
include(":rules-engine-core")
project(":rules-engine-core").projectDir = file("./library/rules-engine/core")

include(":rules-engine-parser-core")
project(":rules-engine-parser-core").projectDir = file("./library/rules-engine/parser/core")

include(":rules-engine-parser-jackson")
project(":rules-engine-parser-jackson").projectDir = file("./library/rules-engine/parser/jackson")

include(":rules-engine-feel")
project(":rules-engine-feel").projectDir = file("./library/rules-engine/feel")

include(":rules-engine-feel-functions")
project(":rules-engine-feel-functions").projectDir = file("./library/rules-engine/feel/functions")

include(":rules-engine-path")
project(":rules-engine-path").projectDir = file("./library/rules-engine/path")

include(":rules-engine-executor")
project(":rules-engine-executor").projectDir = file("./library/rules-engine/executor")

include(":json-element")
project(":json-element").projectDir = file("./library/json/element")

include(":json-element-jackson-parser")
project(":json-element-jackson-parser").projectDir = file("./library/json/element/parser/jackson")

include(":json-element-merge-engine")
project(":json-element-merge-engine").projectDir = file("./library/json/element/merge-engine")

rootProject.name = "u-framework"

/* Utils */
include(":utils-library")
project(":utils-library").projectDir = file("./library/utils")

/* Failure */
include(":failure-library")
project(":failure-library").projectDir = file("./library/failure")

/* Diagnostic-Context */
include(":diagnostic-context-library")
project(":diagnostic-context-library").projectDir = file("./library/diagnostic/context")

include(":diagnostic-context-extension-library")
project(":diagnostic-context-extension-library").projectDir = file("./library/diagnostic/context/extension")

/* Logging */
include(":logging-api-library")
project(":logging-api-library").projectDir = file("./library/logging/api")

include(":logging-formatter-json-library")
project(":logging-formatter-json-library").projectDir = file("./library/logging/formatter/json")

include(":logging-slf4j-library")
project(":logging-slf4j-library").projectDir = file("./library/logging/slf4j")

/* Messaging */
include(":messaging-core-library")
project(":messaging-core-library").projectDir = file("./library/messaging/core")

include(":messaging-core-types-library")
project(":messaging-core-types-library").projectDir = file("./library/messaging/core/types")

include(":messaging-core-headers-library")
project(":messaging-core-headers-library").projectDir = file("./library/messaging/core/headers")

include(":messaging-listener-library")
project(":messaging-listener-library").projectDir = file("./library/messaging/listener")

include(":messaging-publisher-library")
project(":messaging-publisher-library").projectDir = file("./library/messaging/publisher")

include(":messaging-kafka-receiver-library")
project(":messaging-kafka-receiver-library").projectDir = file("./library/messaging/transport/kafka/receiver")

include(":messaging-kafka-sender-library")
project(":messaging-kafka-sender-library").projectDir = file("./library/messaging/transport/kafka/sender")

/* Saga */
include(":saga-messaging-types-library")
project(":saga-messaging-types-library").projectDir = file("./library/saga/messaging/types")

include(":saga-messaging-headers-library")
project(":saga-messaging-headers-library").projectDir = file("./library/saga/messaging/headers")

include(":saga-engine-library")
project(":saga-engine-library").projectDir = file("./library/saga/engine")

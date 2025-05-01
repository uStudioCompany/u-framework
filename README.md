# u-framework

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![CI/CD](https://github.com/uStudioCompany/u-framework/actions/workflows/ci-cd.yml/badge.svg?branch=main)](https://github.com/uStudioCompany/u-framework/actions/workflows/ci-cd.yml)

## Overview

u-framework is a comprehensive Kotlin framework designed to simplify the development of robust, scalable applications.
It provides a collection of libraries and utilities for common application concerns such as error handling, messaging,
database access, event sourcing, and more.

## Features

The framework consists of several modules:

- **Failure Handling**: Standardized approach to error handling and propagation
- **JDBC**: Database access utilities and abstractions
- **JSON Data**: Tools for working with JSON data structures
- **Messaging**: Components for message-based communication including publishers, listeners, and routers
- **Rules Engine**: Framework for defining and executing business rules
- **Saga**: Implementation of the Saga pattern for distributed transactions
- **Event Sourcing**: Tools for event-based persistence and modeling
- **Retry**: Utilities for implementing retry logic
- **Telemetry**: Components for application monitoring and observability
- **Testing**: Utilities to simplify testing of applications built with u-framework
- **Utils**: Common utilities used across the framework

## Installation

To use u-framework in your project, add the required dependencies to your `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://...")
    }
}

dependencies {
    // Add the modules you need, for example:
    implementation("io.github.ustudiocompany:failure-library:0.0.1-alpha.6-SNAPSHOT")
    implementation("io.github.ustudiocompany:jdbc-library:0.0.1-alpha.6-SNAPSHOT")
    // Add other modules as needed
}
```

## Usage

Each module in u-framework is designed to be used independently or in combination with other modules. Refer to the
documentation in each module for specific usage examples.

## Building from Source

### Compilation

```bash
./gradlew clean build
```

### Generating License List

To generate a report of all licenses used in the project:

```bash
./gradlew generateLicenseReport
```

The generated report will be placed in the `buildDir/licenses` directory.

## Configuration

### Publishing Artifacts

To publish artifacts to a repository, set the following environment variables:

- `REPOSITORY_SNAPSHOTS_URL` - URL to snapshots repository
- `REPOSITORY_RELEASES_URL` - URL to releases repository
- `REPOSITORY_USERNAME` - Username to access repository
- `REPOSITORY_PASSWORD` - Password to access repository

## Contributing

Contributions to u-framework are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

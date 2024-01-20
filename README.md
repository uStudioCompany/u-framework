# u-framework

## Compilation ##

To compile ocds-publisher, follow these steps:

`./gradlew clean build -Pintegration-tests` - build project with running integration tests

or run  `./gradlew clean build` to dismiss them.

## Generating license list ##

To generate licenses report, run  `./gradlew generateLicenseReport`.
Generated licenses report will be placed in folder 'buildDir/licenses'.

## Configuration Gradle plugins ##

### Publishing artifacts ###
Env variables:
- REPOSITORY_SNAPSHOTS_URL - url to snapshots repository
- REPOSITORY_RELEASES_URL - url to releases repository
- REPOSITORY_USERNAME - username to access repository
- REPOSITORY_PASSWORD - password to access repository

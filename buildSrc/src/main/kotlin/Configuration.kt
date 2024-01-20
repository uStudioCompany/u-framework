import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.maven.MavenPom

@Suppress("unused")
object Configuration {

    object JVM {
        val target: String
            get() = System.getenv("JAVA_VERSION") ?: "17"

        val compatibility: JavaVersion
            get() = JavaVersion.toVersion(target)
    }

    object Artifact {
        val jdk: String
            get() = "-jdk" + JVM.compatibility.majorVersion
    }

    object Publishing {

        val mavenCentralMetadata: MavenPom.() -> Unit = {
            name.set("u-framework")
            description.set("u-framework")

            licenses {
                license {
                    name.set("Apache License Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/uStudioCompany/u-framework.git")
                developerConnection.set("scm:git:ssh://github.com:uStudioCompany/u-framework.git")
                url.set("https://github.com/uStudioCompany/u-framework/tree/main")
            }
        }

        fun RepositoryHandler.mavenSonatypeRepository(project: Project) {
            maven {
                name = "mvn-repository"
                url = if (project.version.toString().endsWith("SNAPSHOT", true))
                    project.uri(System.getenv("REPOSITORY_SNAPSHOTS_URL") ?: DEFAULT_REPOSITORY_URL)
                else
                    project.uri(System.getenv("REPOSITORY_RELEASES_URL") ?: DEFAULT_REPOSITORY_URL)

                credentials {
                    username = System.getenv("REPOSITORY_USERNAME")
                    password = System.getenv("REPOSITORY_PASSWORD")
                }
            }
        }

        private const val DEFAULT_REPOSITORY_URL = ""
    }
}

plugins {
    `maven-publish`
    signing
}

val signingKey: String? = System.getenv("GPG_PRIVATE_KEY")
val signingKeyPassphrase: String? = System.getenv("GPG_PRIVATE_PASSWORD")

configure<SigningExtension> {
    useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
}

tasks.withType<Sign> {
    onlyIf("this is a release build") {
        isReleaseBuild() && signingKey != null && signingKeyPassphrase != null
    }
}

plugins {
    `maven-publish`
    signing
}

configure<SigningExtension> {
    val signingKey: String? = System.getenv("GPG_PRIVATE_KEY")
    val signingKeyPassphrase: String? = System.getenv("GPG_PRIVATE_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
}

tasks.withType<Sign>() {
    onlyIf("this is a release build") { isReleaseBuild() }
}

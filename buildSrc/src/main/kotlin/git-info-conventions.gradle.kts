import com.palantir.gradle.gitversion.VersionDetails

plugins {
    id("com.palantir.git-version")
}

val versionDetails: groovy.lang.Closure<VersionDetails> by extra
val gitDetails = versionDetails()

tasks.register("printGitTag") {
    doLast {
        val tag = gitDetails.lastTag
        val abbrev = gitDetails.gitHash.substring(0, 7)
        val artifactTag = if (tag != abbrev)
            tag
        else
            "${gitDetails.branchName}-$abbrev"
        println(artifactTag)
    }
}

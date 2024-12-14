import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    id("info.solidsoft.pitest")
}

tasks {

    configure<PitestPluginExtension> {
        threads.set(4)
        testPlugin.set("Kotest")
        pitestVersion.set("1.15.0")
        mutators.set(mutableListOf("STRONGER"))
        outputFormats.set(listOf("XML", "HTML"))
        targetClasses.set(mutableListOf("io.github.ustudiocompany.uframework.*"))
        targetTests.set(mutableListOf("io.github.ustudiocompany.uframework.*"))
        excludedTestClasses.set(mutableListOf("io.github.ustudiocompany.uframework.**.*IT"))
        avoidCallsTo.set(mutableListOf("kotlin", "kotlin.jvm.internal", "kotlin.collections"))
        timestampedReports.set(false)
        exportLineCoverage.set(true)
        useClasspathFile.set(true)
    }
}

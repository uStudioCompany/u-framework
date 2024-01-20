plugins {
    id("org.jetbrains.kotlinx.kover")
}

koverReport {
    filters {
        includes {
            classes("io.github.ustudiocompany.*")
        }
    }

    defaults {
        val baseDir = "reports/jacoco/test"
        xml {
            setReportFile(layout.buildDirectory.file("$baseDir/jacocoTestReport.xml"))
        }
        html {
            setReportDir(layout.buildDirectory.dir("$baseDir/html"))
        }
    }
}

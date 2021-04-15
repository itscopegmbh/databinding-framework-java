@file:Suppress("UNCHECKED_CAST")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mavenDevelopmentRepo: String by extra
val mavenReleaseRepo: String by extra

val gitlabCI: Boolean by extra
val gitlabBranchOrTagName: String by extra
val gitlabCIPipelineId: String by extra

val repoUrl: String by extra
val mavenReleaseTargetRepo: String by extra
val user: String by extra
val pass: String by extra

// ------- Application Version --------
group = "de.itscope.digitalsales"

version = if (gitlabCI) {
    if ((gitlabBranchOrTagName.matches("^\\d+.\\d+.\\d+\$".toRegex()))) {
        gitlabBranchOrTagName
    } else {
        "$gitlabBranchOrTagName-SNAPSHOT"
    }
} else {
    "${System.getProperty("user.name")}-LOCAL-SNAPSHOT"
}

// ------- Plugin Configuration --------
plugins {
    val versions = HashMap<String, String>()

    versions["detekt"] = "1.15.0"
    versions["kotlin"] = "1.4.21"
    versions["ktlint"] = "9.4.1"

    java
    `maven-publish`
    jacoco
    kotlin("jvm") version "${versions["kotlin"]}"
    id("io.gitlab.arturbosch.detekt") version "${versions["detekt"]}"
    id("org.jlleitschuh.gradle.ktlint") version "${versions["ktlint"]}"
}

// -------- Build Script configuration -------
buildscript {
    val url = "http://nexus.local.itscope.com/repository"
    val developmentRepo = "/snapshots/"
    val releaseRepo = "/releases/"

    extra.set("repoUrl", url)
    extra.set("mavenDevelopmentRepo", developmentRepo)
    extra.set("mavenReleaseRepo", releaseRepo)

    val gitlabCIEnv = "${System.getenv()["GITLAB_CI"]}"
    val gitlabCI = !gitlabCIEnv.isEmpty() && gitlabCIEnv != "null"
    val gitlabBranchOrTagName = "${System.getenv()["CI_COMMIT_REF_NAME"]}"
    val gitlabCIPipelineId = "${System.getenv()["CI_PIPELINE_ID"]}"

    extra.set("gitlabBranchOrTagName", gitlabBranchOrTagName)
    extra.set("gitlabCI", gitlabCI)
    extra.set("gitlabCIPipelineId", gitlabCIPipelineId)
    // --------------------------------------------------------------------------

    // determine maven credentials
    val releaseTargetRepo: String
    val userName: String
    val password: String

    if (gitlabCI) {
        userName = "${System.getenv()["NEXUS_REPO_USER"]}"
        password = "${System.getenv()["NEXUS_REPO_PASSWORD"]}"

        releaseTargetRepo = when (gitlabBranchOrTagName.matches("^\\d+.\\d+.\\d+\$".toRegex())) {
            true -> releaseRepo
            else -> developmentRepo
        }
    } else {
        val nexusUser: String by project
        val nexusPassword: String by project
        userName = nexusUser
        password = nexusPassword

        releaseTargetRepo = developmentRepo
    }

    extra.set("mavenReleaseTargetRepo", releaseTargetRepo)
    extra.set("user", userName)
    extra.set("pass", password)
}

repositories {
    jcenter()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_15    //JDK
    targetCompatibility = JavaVersion.VERSION_15    //JVM
}

val libraryVersions: MutableMap<String, String> = HashMap()

libraryVersions["gradle"] = "6.8"
libraryVersions["assertJ"] = "3.8.+"
libraryVersions["junit"] = "5.+" //example for dynamic versioning

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework:spring-web:5.3.4")
    implementation("io.projectreactor:reactor-core:3.4.3")
    testImplementation(
        group = "org.assertj",
        name = "assertj-core",
        version = libraryVersions["assertJ"]
    )

    testImplementation(platform("org.junit:junit-bom:${libraryVersions["junit"]}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
    kotlinOptions.allWarningsAsErrors = true
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}

tasks.withType<Wrapper> {
    gradleVersion = libraryVersions["gradle"]
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.jacocoTestReport {

    dependsOn(tasks.test)
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
        html.isEnabled = true
    }
}

detekt {
    config = files("detekt.yml")
    reports {
        xml {
            enabled = true
        }
        html {
            enabled = true
        }
        txt {
            enabled = false
        }
    }
}

ktlint {
    debug.set(false)
    verbose.set(false)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    additionalEditorconfigFile.set(file("$projectDir/.editorconfig"))
    disabledRules.set(setOf("import-ordering", "no-multi-spaces", "comment-spacing"))
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}
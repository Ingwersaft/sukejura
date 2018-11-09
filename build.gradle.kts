import com.jfrog.bintray.gradle.BintrayExtension
import groovy.lang.Closure
import groovy.util.Node
import net.researchgate.release.BaseScmAdapter
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.0"
    id("net.researchgate.release") version "2.7.0"
    id("com.jfrog.bintray") version "1.8.4"
    `maven-publish`
}
group = "io.kesselring.sukejura"

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.0")

    testCompile("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}
tasks.withType(Test::class.java) {
    testLogging.showStandardStreams = true
}

tasks.findByPath("build")?.finalizedBy(sourcesJar)

release {
    buildTasks = listOf("build", "bintrayPublish", "bintrayUpload")
}

publishing {
    publications.invoke {
        register(findProperty("publicationName")!!, MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
            pom.withXml {
                asNode().get("dependencies").delegateClosureOf<Node> {
                    configurations.compile.allDependencies.forEach {
                        appendNode("dependency").apply {
                            appendNode("groupId", it.group)
                            appendNode("artifactId", it.name)
                            appendNode("version", it.version)
                        }
                    }
                }
                asNode().apply {
                    appendNode("name", "Sukejura")
                    appendNode("url", "https://github.com/Ingwersaft/sukejura")
                    appendNode("licenses").apply {
                        appendNode("license").apply {
                            appendNode("name", "MIT License")
                            appendNode("url", "https://github.com/Ingwersaft/sukejura/blob/master/LICENSE")
                            appendNode("distribution", "repo")
                        }
                    }
                }
            }
            version = findProperty("version")
            group = project.group
        }
    }
}
fun findProperty(s: String) = project.findProperty(s) as String?
bintray {
    user = findProperty("bintrayUser")
    key = findProperty("bintrayApiKey")
    publish = true
    dryRun = false
    setPublications(findProperty("publicationName"))
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "Sukejura"
        name = "Sukejura"
        userOrg = user
        websiteUrl = "https://github.com/Ingwersaft/sukejura"
        githubRepo = "https://github.com/Ingwersaft/sukejura"
        vcsUrl = "https://github.com/Ingwersaft/sukejura.git"
        issueTrackerUrl = "https://github.com/Ingwersaft/sukejura/issues"
        description = "cron-like library for kotlin - coroutine based"
        setLabels("kotlin", "cron", "scheduling", "schedule", "sukejura", "coroutines", "tasks")
        setLicenses("MIT")
        desc = description
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = findProperty("version")!!
        })
    })
}

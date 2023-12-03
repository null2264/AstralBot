import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.3-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    kotlin("jvm") version "1.9.21"
    java
}

architectury {
    val minecraftVersion: String by project
    minecraft = minecraftVersion
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "kotlin")

    val minecraftVersion: String by project
    val modLoader = project.name
    val modId = rootProject.name
    val isCommon = modLoader == rootProject.projects.common.name

    base {
        archivesName.set("$modId-$modLoader-$minecraftVersion")
    }

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()
    }

    repositories {
        mavenCentral()
        maven(url = "https://maven.architectury.dev/")
        maven(url = "https://maven.parchmentmc.org")
        maven(url = "https://maven.resourcefulbees.com/repository/maven-public/")
        maven {
            name = "Kotlin for Forge"
            setUrl("https://thedarkcolour.github.io/KotlinForForge/")
        }
        // Forge Config API port
        maven {
            name = "Fuzs Mod Resources"
            setUrl("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        }
    }

    // Bot dependencies
    val jdaVersion: String by project
    val exposedVersion: String by project
    val sqliteJDBCVersion: String by project

    val botDependencies = arrayOf(
        "net.dv8tion:JDA:$jdaVersion",
        "org.jetbrains.exposed:exposed-core:$exposedVersion",
        "org.jetbrains.exposed:exposed-dao:$exposedVersion",
        "org.jetbrains.exposed:exposed-jdbc:$exposedVersion",
        "org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion",
        "org.xerial:sqlite-jdbc:$sqliteJDBCVersion"
    )

    dependencies {
        // Minecraft Mod dependencies
        "minecraft"("::$minecraftVersion")

        @Suppress("UnstableApiUsage")
        "mappings"(project.the<LoomGradleExtensionAPI>().layered {
            val parchmentVersion: String by project

            officialMojangMappings()

            parchment(
                create(
                    group = "org.parchmentmc.data",
                    name = "parchment-$minecraftVersion",
                    version = parchmentVersion
                )
            )
        })

        botDependencies.forEach {
            implementation(it) {
                exclude(module = "opus-java")
                exclude(group = "org.jetbrains.kotlin")
                exclude(group = "org.slf4j")
            }
        }
    }

    java {
        withSourcesJar()
    }

    tasks.jar {
        archiveClassifier.set("dev")
    }

    tasks.named<RemapJarTask>("remapJar") {
        archiveClassifier.set(null as String?)
    }

    tasks.processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        filesMatching(listOf("META-INF/mods.toml", "fabric.mod.json")) {
            expand("version" to project.version)
        }
    }

    if (!isCommon) {
        apply(plugin = "com.github.johnrengelman.shadow")
        configure<ArchitectPluginExtension> {
            platformSetupLoomIde()
        }

        val shadowCommon by configurations.creating {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        dependencies {
            botDependencies.forEach {
                shadowCommon(it) {
                    exclude(module = "opus-java")
                    exclude(group = "org.jetbrains.kotlin")
                    exclude(group = "org.jetbrains.kotlinx")
                    exclude(group = "org.slf4j")
                }
            }
        }

        tasks {
            "shadowJar"(ShadowJar::class) {
                archiveClassifier.set("dev-shadow")
                configurations = listOf(shadowCommon)

                relocate("org.apache.commons.collections4", "dev.erdragh.shadowed.org.apache.commons.collections4")

                exclude(".cache/**") //Remove datagen cache from jar.
                exclude("**/astralbot/datagen/**") //Remove data gen code from jar.
                exclude("**/org/slf4j/**")
            }

            "remapJar"(RemapJarTask::class) {
                dependsOn("shadowJar")
                inputFile.set(named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
            }
        }
    } else {
        sourceSets.main.get().resources.srcDir("src/main/generated/resources")
    }
}

kotlin {
    jvmToolchain(17)
}
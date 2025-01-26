import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "9.0.0-beta6"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
}

val mcversion: String by project

group = providers.gradleProperty("group").getOrElse("jp.sailuna.${project.name}")
version = providers.gradleProperty("version").getOrElse("dev")
description = providers.gradleProperty("description").getOrElse("Random swap players location plugin")

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper", "paper-api", "$mcversion-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        archiveClassifier = ""
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(mcversion)
    }
}

bukkitPluginYaml {
    main = "jp.sailuna.swap.RandomSwap"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.addAll("hqnkuh", "sailuna developer")
    apiVersion = mcversion
}
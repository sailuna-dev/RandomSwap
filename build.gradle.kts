import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.resource.factory.bukkit)
//    alias(libs.plugins.resource.factory.paper)
}

val mcversion: String by project

group = providers.gradleProperty("group").getOrElse("jp.sailuna.${project.name}")
version = providers.gradleProperty("version").getOrElse("SNAPSHOT")
description = providers.gradleProperty("description").getOrElse("Random swap players location plugin")

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    compileOnly(libs.paper.api)
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

// paperPluginYaml {
//    main = "jp.sailuna.swap.RandomSwap"
//    authors.addAll("hqnkuh", "sailuna developer")
//    apiVersion = mcversion
//}
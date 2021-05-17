import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.PluginDependency

plugins {
    `java-library`
    id("org.spongepowered.gradle.plugin") version "1.0.3"
}

group = "me.dags"
version = "0.2.5"
val versionString: String = version.toString()
val apiString: String = "8.0.0"

repositories {
    mavenCentral()
}

sponge {
    apiVersion(apiString)
    plugin("fmt") {
        loader(PluginLoaders.JAVA_PLAIN)
        displayName("Fmt")
        mainClass("me.dags.fmt.Fmt")
        description("Sponge text formatter util")
        links {
            homepage("https://ardacraft.me")
            source("https://github.com/ArdaCraft/Fmt")
            issues("https://github.com/ArdaCraft/Fmt/issues")
        }
        contributor("Dags") {
            description("Original author and developer")
        }
        contributor("Freshmilkymilk") {
            description("Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

val javaTarget = 8 // Sponge targets a minimum of Java 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
}

tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8" // Consistent source file encoding
        if (JavaVersion.current().isJava10Compatible) {
            release.set(javaTarget)
        }
    }
}

// Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
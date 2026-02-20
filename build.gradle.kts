plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "com.toon.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        intellijDependencies()   // REQUIRED
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2023.3")    // Stable IntelliJ version (Android Studio compatible)

        instrumentationTools()    // REQUIRED for instrumentCode task
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "233"
            untilBuild = "999.*"
        }

        changeNotes = """
            TOONToKotlinClass plugin.
        """.trimIndent()
    }
}

// Java/Kotlin must target JVM 17
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

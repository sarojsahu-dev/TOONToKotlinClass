plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "com.toon.kotlin"
version = "1.0.0"

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
            untilBuild = ""   // No upper limit — supports all latest IDE versions
        }

        changeNotes = """
            <h3>v1.0.0 - Initial Release</h3>
            <ul>
                <li>TOON to Kotlin data class conversion with real-time preview</li>
                <li>Support for nested objects, lists, and object lists</li>
                <li>11 annotation frameworks (Gson, Jackson, Moshi, kotlinx.serialization, Firebase, etc.)</li>
                <li>Advanced settings with 4-tab UI (Property, Annotation, Other, Extensions)</li>
                <li>Smart type inference (Int, Long, Double, Boolean, String)</li>
                <li>Built-in TOON formatter and validator</li>
                <li>Parcelable support, @Keep annotations, and more</li>
            </ul>
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

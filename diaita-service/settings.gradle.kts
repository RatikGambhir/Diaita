plugins {
    // Lets Gradle download the JDK declared by `kotlin { jvmToolchain(...) }` when the
    // machine does not already have a matching installation, so the build is
    // reproducible without a manually provisioned JDK.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "diaita-service"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

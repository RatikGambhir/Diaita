plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.diaita"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

// Overridable so the project can be built against a locally available JDK, e.g.
// `./gradlew build -PjavaToolchainVersion=21`, without editing this file.
val javaToolchainVersion = (findProperty("javaToolchainVersion") as String? ?: "23").toInt()

kotlin {
    jvmToolchain(javaToolchainVersion)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(javaToolchainVersion.toString()))
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}

dependencies {
    // --- Ktor server ---
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.sse)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.config.yaml)

    // --- Logging ---
    implementation(libs.logback.classic)

    // --- Supabase Kotlin Client ---
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)

    // --- Ktor client ---
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.apache)

    // --- Tests ---
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.mockk)
}

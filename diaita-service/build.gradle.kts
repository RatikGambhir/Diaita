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

kotlin {
    jvmToolchain(23)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions { jvmTarget = "23" }
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

    // --- DB / pool / logging ---
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.hikariCP)
    implementation(libs.logback.classic)
    implementation("com.google.genai:google-genai:1.0.0")

    // --- Supabase Kotlin Client ---
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.1"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-cio:3.1.1")


    // --- Ktor client (optional in a server module) ---
    implementation(libs.ktor.client.core)
    implementation("io.ktor:ktor-client-cio:<latest_version>")
    implementation(libs.ktor.client.apache)

    // --- Koin (optional; remove if doing manual DI) ---
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation("io.ktor:ktor-server-content-negotiation:3.2.3")
    implementation("io.ktor:ktor-server-core:3.2.3")
    implementation("io.ktor:ktor-server-core:3.2.3")
    implementation("io.ktor:ktor-serialization-gson:3.2.3")
    implementation("io.ktor:ktor-server-content-negotiation:3.2.3")
    implementation("io.ktor:ktor-server-core:3.2.3")


    // --- Tests ---
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation("io.mockk:mockk:1.13.8")
}

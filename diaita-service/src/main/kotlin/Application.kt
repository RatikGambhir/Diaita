package com.diaita

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    val database = configureDatabases()
    val container = configureContainer(database)
    configureSecurity(container.get(), container.get())
    configureHTTP()
    configureRouting(container)
}

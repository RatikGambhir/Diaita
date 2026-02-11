package com.diaita

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureFrameworks()
    configureSerialization()



    val container = configureContainer()


//    ApplicationStopPreparing.run { val source = container.get<HikariDataSource>()
//        source.close()
//    }
    // configureMonitoring()
    configureHTTP()
    // configureSecurity()
    configureRouting(container)
}

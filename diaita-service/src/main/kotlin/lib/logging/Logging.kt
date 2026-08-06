package com.diaita.lib.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Logger for the receiver's class, e.g. `private val log = logger()` inside a class body.
 *
 * `SupabaseManager` and the REST clients expose `inline`/`reified` members, which
 * cannot touch private state, so those types call [loggerFor] with an explicit
 * class instead.
 */
inline fun <reified T : Any> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

fun loggerFor(type: Class<*>): Logger = LoggerFactory.getLogger(type)

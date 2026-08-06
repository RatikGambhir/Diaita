package com.diaita

import io.ktor.server.application.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

fun Application.configureContainer(): Container = Container().apply {
    bindAll(genSingletons())
}

/**
 * Instances that cannot be built by reflection because they need configuration
 * values (API keys, URLs) rather than other beans.
 */
fun Application.genSingletons(): List<Any> = buildList {
    add(configureDatabases())
    addAll(configureRestClient())
}

/**
 * Minimal reflection-based service locator.
 *
 * Beans are singletons keyed by type. Anything not pre-bound is constructed on
 * first [get] by recursively resolving its primary constructor parameters, which
 * is enough for the controller -> service -> repo graph this application has.
 */
class Container {

    private val beans = mutableMapOf<KClass<*>, Any>()

    /** Registers each instance under its own runtime type, first binding wins. */
    fun bindAll(instances: List<Any>) {
        instances.forEach(::bindSingleton)
    }

    fun bindSingleton(instance: Any) {
        bind(instance::class, instance)
    }

    /** Binds an instance under an explicit type — used to inject mocks in tests. */
    fun bind(type: KClass<*>, instance: Any) {
        beans.putIfAbsent(type, instance)
    }

    inline fun <reified T : Any> bind(instance: T) = bind(T::class, instance)

    fun <T : Any> instanceExists(type: KClass<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return beans[type] as T?
    }

    inline fun <reified T : Any> get(): T = get(T::class)

    fun <T : Any> get(classname: KClass<*>): T {
        @Suppress("UNCHECKED_CAST")
        return beans.getOrPut(classname) { createInstance(classname) } as T
    }

    fun <T : Any> createInstance(classname: KClass<T>): T {
        val constructor = requireNotNull(classname.primaryConstructor) {
            "Cannot construct ${classname.qualifiedName}: no primary constructor. Bind it explicitly instead."
        }
        val arguments = constructor.parameters.associateWith { parameter ->
            val parameterType = parameter.type.classifier as? KClass<*>
                ?: error(
                    "Cannot resolve parameter '${parameter.name}' of ${classname.qualifiedName}: " +
                        "its type is not a class."
                )
            getInstance(parameterType)
        }
        return constructor.callBy(arguments)
    }

    fun getInstance(classname: KClass<*>): Any = get<Any>(classname)
}

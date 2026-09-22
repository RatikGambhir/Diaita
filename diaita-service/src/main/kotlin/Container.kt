package com.diaita

import io.ktor.server.application.*
import com.diaita.auth.AuthService
import com.diaita.auth.PasswordHasher
import com.diaita.auth.TokenService
import com.diaita.database.SQLiteDatabase
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.collections.flatten

fun Application.genSingletons(database: SQLiteDatabase): List<Any> {
    val jwtSecret = System.getenv("JWT_SECRET")
        ?.trim()
        ?.takeIf(String::isNotEmpty)
        ?: environment.config.property("jwt.secret").getString()
    val tokenService = TokenService(
        secret = jwtSecret,
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        lifetimeSeconds = environment.config.property("jwt.lifetimeSeconds").getString().toLong()
    )

    return listOf(
        listOf(database, PasswordHasher(), tokenService),
        configureRestClient()
    ).flatten()
}

fun Application.configureContainer(database: SQLiteDatabase): Container {
    val singletons = genSingletons(database)
    val container = Container()
    container.bindAll(singletons)
    container.get<AuthService>()
    return container
}

class Container {

    private val beans = mutableMapOf<KClass<*>, Any>()

    fun bindAll(instances: List<Any>) {
        instances.forEach {
            val classname = it::class
            bindSingleton(it)
            val primaryConstructor = classname.primaryConstructor!!
            val beansToBind = primaryConstructor.parameters.filter { param ->
                param.type.classifier != String::class && param.type.classifier == Object::class
            }.map { param -> param.type.classifier as KClass<*> }
            bindAll(beansToBind)
        }
    }

    fun <T: Any> instanceExists(type: KClass<T>): T? {
        val instance = beans[type]
        @Suppress("UNCHECKED_CAST")
        instance?.let { return it as T }
        return null
    }



    fun bindSingleton(instance: Any) {
        beans[instance::class]?.let {
            return;
        } ?: run {
            beans[instance::class] = instance
        }
    }

    // Binds an instance under a specified type — used for testing with mocks
    fun bind(type: KClass<*>, instance: Any) {
        beans[type]?.let { return } ?: run { beans[type] = instance }
    }

    inline fun <reified T: Any> bind(instance: T) {
        bind(T::class, instance)
    }

    inline fun <reified T: Any> get(): T {
        return get(T::class)
    }

    fun <T: Any> get(classname: KClass<*>): T {
        val exists = instanceExists(classname)
        @Suppress("UNCHECKED_CAST")
        exists?.let { return exists as T}
        val created = createInstance(classname)
        beans[classname] = created
        @Suppress("UNCHECKED_CAST")
        return created as T;
    }

     fun <T: Any> createInstance(classname: KClass<T>): T {
        val constructor = classname.primaryConstructor!!
        val parameters = constructor.parameters.associateWith{param ->
            val classname = param.type.classifier as KClass<*>
            getInstance(classname)

        }
         return constructor.callBy(parameters)

        }

    fun getInstance(classname: KClass<*>): Any {
        @Suppress("UNCHECKED_CAST")
        return get(classname as KClass<Any>)
    }
    }

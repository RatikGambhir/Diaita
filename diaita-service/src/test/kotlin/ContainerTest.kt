package com.diaita

import com.diaita.auth.PasswordHasher
import com.diaita.auth.TokenService
import com.diaita.database.SQLiteDatabase
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class EmptyObject1
class EmptyObject2
class EmptyObject3
class LeafDependency
class NeedsDependency(val leaf: LeafDependency)

class ContainerTest {
    @Test
    fun instance_exists_returns_null_for_missing_type() {
        assertNull(Container().instanceExists(EmptyObject1::class))
    }

    @Test
    fun bind_all_registers_simple_instances() {
        val container = Container()
        container.bindAll(listOf(EmptyObject1(), EmptyObject2(), EmptyObject3()))
        assertNotNull(container.instanceExists(EmptyObject1::class))
        assertNotNull(container.instanceExists(EmptyObject2::class))
        assertNotNull(container.instanceExists(EmptyObject3::class))
    }

    @Test
    fun bind_singleton_does_not_replace_existing_binding_for_same_type() {
        val container = Container()
        val first = EmptyObject1()
        container.bindSingleton(first)
        container.bindSingleton(EmptyObject1())
        assertSame(first, container.instanceExists(EmptyObject1::class))
    }

    @Test
    fun bind_all_supports_sqlite_and_auth_singletons() {
        val container = Container()
        val database = testDatabase()
        val hasher = PasswordHasher()
        val tokens = TokenService("test-secret", "test", "test", 3600)
        container.bindAll(listOf(database, hasher, tokens))
        assertSame(database, container.instanceExists(SQLiteDatabase::class))
        assertSame(hasher, container.instanceExists(PasswordHasher::class))
        assertSame(tokens, container.instanceExists(TokenService::class))
    }

    @Test
    fun get_creates_object_graph_when_not_prebound() {
        val container = Container()
        val created = container.get<NeedsDependency>()
        assertSame(container.instanceExists(LeafDependency::class), created.leaf)
    }
}

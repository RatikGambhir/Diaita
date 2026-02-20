package com.diaita

import com.diaita.lib.clients.GeminiRestClient
import com.diaita.lib.factories.SupabaseManager
import io.github.jan.supabase.SupabaseClient
import io.mockk.mockk
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
        val container = Container()

        assertNull(container.instanceExists(EmptyObject1::class))
    }

    @Test
    fun bind_all_registers_simple_instances() {
        val container = Container()
        val instances = listOf(EmptyObject1(), EmptyObject2(), EmptyObject3())

        container.bindAll(instances)

        assertNotNull(container.instanceExists(EmptyObject1::class))
        assertNotNull(container.instanceExists(EmptyObject2::class))
        assertNotNull(container.instanceExists(EmptyObject3::class))
    }

    @Test
    fun bind_singleton_does_not_replace_existing_binding_for_same_type() {
        val container = Container()
        val first = EmptyObject1()
        val second = EmptyObject1()

        container.bindSingleton(first)
        container.bindSingleton(second)

        val bound = container.instanceExists(EmptyObject1::class)
        assertNotNull(bound)
        assertSame(first, bound)
    }

    @Test
    fun bind_all_supports_current_supabase_and_rest_client_types() {
        val container = Container()
        val mockSupabaseClient = mockk<SupabaseClient>(relaxed = true)
        val supabaseManager = SupabaseManager(mockSupabaseClient)
        val geminiClient = GeminiRestClient(apiKey = "test-api-key")

        container.bindAll(listOf(supabaseManager, geminiClient))

        val boundManager = container.instanceExists(SupabaseManager::class)
        val boundGemini = container.instanceExists(GeminiRestClient::class)

        assertNotNull(boundManager)
        assertNotNull(boundGemini)
        assertSame(supabaseManager, boundManager)
        assertSame(geminiClient, boundGemini)
        assertSame(mockSupabaseClient, boundManager.client)
    }

    @Test
    fun get_creates_object_graph_when_not_prebound() {
        val container = Container()

        val created = container.get<NeedsDependency>()
        val leaf = container.instanceExists(LeafDependency::class)

        assertNotNull(created)
        assertNotNull(leaf)
        assertSame(leaf, created.leaf)
    }

    @Test
    fun bind_registers_instance_under_specified_type() {
        val container = Container()
        val leaf = mockk<LeafDependency>(relaxed = true)

        container.bind<LeafDependency>(leaf)

        val resolved = container.get<NeedsDependency>()
        assertNotNull(resolved)
        assertSame(leaf, resolved.leaf)
    }
}

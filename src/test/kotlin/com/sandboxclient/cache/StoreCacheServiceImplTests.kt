package com.sandboxclient.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.sandboxclient.support.TestDataFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyCollection
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit

class StoreCacheServiceImplTests {
    private val redis = mock(RedisTemplate::class.java) as RedisTemplate<String, String>
    private val values = mock(ValueOperations::class.java) as ValueOperations<String, String>
    private val mapper = ObjectMapper().findAndRegisterModules()
    private lateinit var cache: StoreCacheServiceImpl

    @BeforeEach
    fun setUp() {
        cache = StoreCacheServiceImpl(redis, mapper)
        `when`(redis.opsForValue()).thenReturn(values)
    }

    @Test
    fun `returns null on cache miss`() {
        `when`(values.get(CachePolicy.pageKey(0, 20))).thenReturn(null)
        assertNull(cache.getPage(TestDataFactory.query(0, 20)))
    }

    @Test
    fun `returns page on cache hit`() {
        val query = TestDataFactory.query(1, 5)
        val page = TestDataFactory.page(listOf(TestDataFactory.location(7, "Cached")))
        `when`(values.get(CachePolicy.pageKey(1, 5))).thenReturn(mapper.writeValueAsString(page))

        assertEquals(page, cache.getPage(query))
    }

    @Test
    fun `returns null and tolerates malformed cached JSON`() {
        `when`(values.get(CachePolicy.pageKey(0, 20))).thenReturn("not-json")
        assertNull(cache.getPage(TestDataFactory.query()))
    }

    @Test
    fun `saves page using key and configured TTL`() {
        val query = TestDataFactory.query(2, 10)
        val page = TestDataFactory.page()

        cache.savePage(query, page)

        verify(values).set(
            eq(CachePolicy.pageKey(2, 10)),
            anyString(),
            eq(CachePolicy.ttl.toSeconds()),
            eq(TimeUnit.SECONDS),
        )
    }

    @Test
    fun `save page tolerates redis failure`() {
        `when`(values.set(anyString(), anyString(), eq(CachePolicy.ttl.toSeconds()), eq(TimeUnit.SECONDS)))
            .thenThrow(DataAccessResourceFailureException("Redis unavailable"))
        cache.savePage(TestDataFactory.query(), TestDataFactory.page())
    }

    @Test
    fun `reports snapshot freshness based on key existence`() {
        `when`(redis.hasKey(CachePolicy.SNAPSHOT_KEY)).thenReturn(true)
        assertTrue(cache.hasFreshSnapshot())
        `when`(redis.hasKey(CachePolicy.SNAPSHOT_KEY)).thenReturn(false)
        assertFalse(cache.hasFreshSnapshot())
        `when`(redis.hasKey(CachePolicy.SNAPSHOT_KEY)).thenReturn(null)
        assertFalse(cache.hasFreshSnapshot())
    }

    @Test
    fun `treats redis error checking snapshot as stale`() {
        `when`(redis.hasKey(CachePolicy.SNAPSHOT_KEY)).thenThrow(DataAccessResourceFailureException("down"))
        assertFalse(cache.hasFreshSnapshot())
    }

    @Test
    fun `clears page keys when keys exist`() {
        val keys = setOf(CachePolicy.pageKey(0, 20), CachePolicy.pageKey(1, 20))
        `when`(redis.keys("${CachePolicy.PAGE_KEY_PREFIX}*")).thenReturn(keys)

        cache.clearPages()

        verify(redis).delete(keys)
    }

    @Test
    fun `does not delete when no page keys exist`() {
        `when`(redis.keys("${CachePolicy.PAGE_KEY_PREFIX}*")).thenReturn(emptySet())
        cache.clearPages()
        verify(redis, never()).delete(anyCollection<String>())
    }

    @Test
    fun `tolerates redis failure while clearing pages`() {
        `when`(redis.keys("${CachePolicy.PAGE_KEY_PREFIX}*")).thenThrow(DataAccessResourceFailureException("down"))
        cache.clearPages()
    }

    @Test
    fun `marks snapshot fresh with configured TTL`() {
        cache.markSnapshotFresh()
        verify(values).set(eq(CachePolicy.SNAPSHOT_KEY), eq("ready"), eq(CachePolicy.ttl.toSeconds()), eq(TimeUnit.SECONDS))
    }

    @Test
    fun `tolerates redis failure while marking snapshot fresh`() {
        `when`(values.set(eq(CachePolicy.SNAPSHOT_KEY), eq("ready"), eq(CachePolicy.ttl.toSeconds()), eq(TimeUnit.SECONDS)))
            .thenThrow(DataAccessResourceFailureException("down"))
        cache.markSnapshotFresh()
    }
}

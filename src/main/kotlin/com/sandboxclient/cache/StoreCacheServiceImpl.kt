package com.sandboxclient.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.sandboxclient.config.log.LogMessagesEnum.STORE_CACHE_HIT
import com.sandboxclient.config.log.LogMessagesEnum.STORE_CACHE_MISS
import com.sandboxclient.config.log.LogMessagesEnum.STORE_CACHE_UNAVAILABLE
import com.sandboxclient.config.log.LoggerHandler
import com.sandboxclient.domain.StorePage
import com.sandboxclient.domain.StorePageQuery
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StoreCacheServiceImpl(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : StoreCacheService {
    override fun getPage(query: StorePageQuery): StorePage? {
        val key = CachePolicy.pageKey(query.page, query.size)
        val page = readPage(key)

        if (page == null) {
            LoggerHandler.info(log, STORE_CACHE_MISS, query.page, query.size)
        } else {
            LoggerHandler.info(log, STORE_CACHE_HIT, query.page, query.size)
        }

        return page
    }

    override fun savePage(
        query: StorePageQuery,
        page: StorePage,
    ) {
        try {
            redisTemplate.opsForValue().set(
                CachePolicy.pageKey(query.page, query.size),
                objectMapper.writeValueAsString(page),
                CachePolicy.ttl.toSeconds(),
                TimeUnit.SECONDS,
            )
        } catch (exception: Exception) {
            logUnavailable("write page", exception)
        }
    }

    override fun hasFreshSnapshot(): Boolean =
        try {
            redisTemplate.hasKey(CachePolicy.SNAPSHOT_KEY) == true
        } catch (exception: DataAccessException) {
            logUnavailable("check snapshot", exception)
            false
        }

    override fun clearPages() {
        try {
            val keys = redisTemplate.keys("${CachePolicy.PAGE_KEY_PREFIX}*").orEmpty()

            if (keys.isNotEmpty()) {
                redisTemplate.delete(keys)
            }
        } catch (exception: DataAccessException) {
            logUnavailable("clear pages", exception)
        }
    }

    override fun markSnapshotFresh() {
        try {
            redisTemplate.opsForValue().set(
                CachePolicy.SNAPSHOT_KEY,
                SNAPSHOT_READY_VALUE,
                CachePolicy.ttl.toSeconds(),
                TimeUnit.SECONDS,
            )
        } catch (exception: DataAccessException) {
            logUnavailable("write snapshot marker", exception)
        }
    }

    private fun readPage(key: String): StorePage? =
        try {
            val value = redisTemplate.opsForValue().get(key) ?: return null

            objectMapper.readValue(
                value,
                object : TypeReference<StorePage>() {},
            )
        } catch (exception: Exception) {
            logUnavailable("read page", exception)
            null
        }

    private fun logUnavailable(
        operation: String,
        exception: Exception,
    ) {
        LoggerHandler.warn(
            log,
            STORE_CACHE_UNAVAILABLE,
            operation,
            exception.message ?: "unknown",
        )
    }

    private companion object {
        const val SNAPSHOT_READY_VALUE = "ready"

        val log = LoggerFactory.getLogger(StoreCacheServiceImpl::class.java)
    }
}

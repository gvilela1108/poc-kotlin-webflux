package com.sandboxclient.repository

import com.sandboxclient.domain.Location
import com.sandboxclient.domain.StorePage
import com.sandboxclient.repository.entities.StoreLocationEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class StoreRepository(
    private val jpaRepository: StoreLocationJpaRepository,
) {
    @Transactional
    fun replaceAll(locations: List<Location>) {
        jpaRepository.deleteAllInBatch()
        jpaRepository.saveAll(locations.map(::StoreLocationEntity))
    }

    @Transactional(readOnly = true)
    fun count(): Int = jpaRepository.count().toInt()

    @Transactional(readOnly = true)
    fun findPage(
        page: Int,
        size: Int,
    ): StorePage {
        val result = jpaRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")))
        return StorePage(result.totalElements.toInt(), result.content.map(StoreLocationEntity::toDomain))
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): Location? = jpaRepository.findById(id).orElse(null)?.toDomain()

    @Transactional(readOnly = true)
    fun findAll(): List<Location> =
        jpaRepository
            .findAll(Sort.by(Sort.Direction.ASC, "id"))
            .map(StoreLocationEntity::toDomain)
}

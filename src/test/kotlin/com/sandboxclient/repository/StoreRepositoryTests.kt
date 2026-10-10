package com.sandboxclient.repository

import com.sandboxclient.repository.entities.StoreLocationEntity
import com.sandboxclient.support.TestDataFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.Optional

class StoreRepositoryTests {
    private val jpa = mock(StoreLocationJpaRepository::class.java)
    private lateinit var repository: StoreRepository

    @BeforeEach
    fun setUp() {
        repository = StoreRepository(jpa)
    }

    @Test
    fun `replace all deletes old rows and saves mapped entities`() {
        val locations =
            listOf(
                TestDataFactory.location(1, "One"),
                TestDataFactory.location(
                    2,
                    "Two",
                    openingHours = null,
                    keywords = null,
                ),
            )

        repository.replaceAll(locations)

        verify(jpa).deleteAllInBatch()

        verify(jpa).saveAll(
            ArgumentMatchers.argThat<Iterable<StoreLocationEntity>> { entities ->
                entities.map { it.id } == listOf(1L, 2L)
            },
        )
    }

    @Test
    fun `count converts repository count to int`() {
        `when`(jpa.count()).thenReturn(12L)

        assertEquals(12, repository.count())
    }

    @Test
    fun `find page uses ascending id sort and maps content`() {
        val pageable =
            PageRequest.of(
                1,
                2,
                Sort.by(Sort.Direction.ASC, "id"),
            )

        val entities =
            listOf(
                StoreLocationEntity(TestDataFactory.location(2, "Two")),
                StoreLocationEntity(TestDataFactory.location(3, "Three")),
            )

        `when`(jpa.findAll(pageable))
            .thenReturn(PageImpl(entities, pageable, 7))

        val page = repository.findPage(1, 2)

        assertEquals(7, page.count)
        assertEquals(listOf(2L, 3L), page.locations.map { it.id })

        verify(jpa).findAll(pageable)
    }

    @Test
    fun `find by id maps existing entity`() {
        val entity =
            StoreLocationEntity(
                TestDataFactory.location(8, "Found"),
            )

        `when`(jpa.findById(8L))
            .thenReturn(Optional.of(entity))

        val result = repository.findById(8L)

        assertEquals("Found", result?.name)
    }

    @Test
    fun `find by id returns null when missing`() {
        `when`(jpa.findById(999L))
            .thenReturn(Optional.empty())

        assertNull(repository.findById(999L))
    }

    @Test
    fun `find all sorts by id and maps entities`() {
        val sort = Sort.by(Sort.Direction.ASC, "id")

        val entities =
            listOf(
                StoreLocationEntity(TestDataFactory.location(1, "One")),
                StoreLocationEntity(TestDataFactory.location(2, "Two")),
            )

        `when`(jpa.findAll(sort)).thenReturn(entities)

        val result = repository.findAll()

        assertEquals(listOf(1L, 2L), result.map { it.id })
        assertTrue(result.all { it.openingHours?.isNotEmpty() == true })

        verify(jpa).findAll(sort)
    }
}

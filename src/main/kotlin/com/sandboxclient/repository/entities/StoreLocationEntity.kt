package com.sandboxclient.repository.entities

import com.sandboxclient.domain.Location
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OrderColumn
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.Objects

@Entity
@Table(
    name = "STORE_LOCATION_ENTITY",
    uniqueConstraints = [UniqueConstraint(columnNames = ["ID"])],
)
@Access(AccessType.FIELD)
class StoreLocationEntity protected constructor() {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    var id: Long = 0
        private set

    @Column(name = "NAME", nullable = false, length = 500)
    var name: String = ""
        private set

    @Column(name = "CITY", nullable = false, length = 500)
    var city: String = ""
        private set

    @Column(name = "STREET_AND_NUMBER", nullable = false, length = 1000)
    var streetAndNumber: String = ""
        private set

    @Column(name = "ZIP", length = 50)
    var zip: String? = null
        private set

    @ElementCollection
    @CollectionTable(name = "STORE_LOCATION_OPENING_HOURS")
    @OrderColumn(name = "POSITION")
    var openingHours: MutableList<OpeningHoursEntity> = mutableListOf()
        private set

    @ElementCollection
    @CollectionTable(name = "STORE_LOCATION_KEYWORDS")
    @OrderColumn(name = "POSITION")
    @Column(name = "KEYWORD", nullable = false, length = 500)
    var keywords: MutableList<String> = mutableListOf()
        private set

    @Column(name = "LAT")
    var lat: Double? = null
        private set

    @Column(name = "LNG")
    var lng: Double? = null
        private set

    constructor(location: Location) : this() {
        id = Objects.requireNonNull(location.id, "id must not be null")
        name = Objects.requireNonNull(location.name, "name must not be null")
        city = Objects.requireNonNull(location.city, "city must not be null")
        streetAndNumber = Objects.requireNonNull(location.streetAndNumber, "streetAndNumber must not be null")
        zip = location.zip
        openingHours =
            location.openingHours
                .orEmpty()
                .map(::OpeningHoursEntity)
                .toMutableList()
        keywords = location.keywords.orEmpty().toMutableList()
        lat = location.lat
        lng = location.lng
    }

    fun toDomain() =
        Location(
            id = id,
            name = name,
            city = city,
            streetAndNumber = streetAndNumber,
            zip = zip,
            openingHours = openingHours.map(OpeningHoursEntity::toDomain),
            keywords = keywords.toList(),
            lat = lat,
            lng = lng,
        )

    override fun equals(other: Any?): Boolean = this === other || (other is StoreLocationEntity && id == other.id)

    override fun hashCode(): Int = Objects.hash(id)
}

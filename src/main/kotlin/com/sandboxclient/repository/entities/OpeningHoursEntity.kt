package com.sandboxclient.repository.entities

import com.sandboxclient.domain.OpeningHours
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
@Access(AccessType.FIELD)
class OpeningHoursEntity protected constructor() {
    @Column(name = "CLOSED", nullable = false)
    var closed: Boolean = true
        private set

    @Column(name = "DAY_OF_WEEK", nullable = false)
    var dayOfWeek: Int = 0
        private set

    @Column(name = "FROM_1")
    var from1: String? = null
        private set

    @Column(name = "TO_1")
    var to1: String? = null
        private set

    @Column(name = "FROM_2")
    var from2: String = ""
        private set

    @Column(name = "TO_2")
    var to2: String = ""
        private set

    constructor(openingHours: OpeningHours) : this() {
        closed = openingHours.closed
        dayOfWeek = openingHours.dayOfWeek
        from1 = openingHours.from1
        to1 = openingHours.to1
        from2 = openingHours.from2
        to2 = openingHours.to2
    }

    fun toDomain() = OpeningHours(closed, dayOfWeek, from1, to1, from2, to2)
}

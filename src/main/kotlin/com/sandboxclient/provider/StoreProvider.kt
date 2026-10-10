package com.sandboxclient.provider

import com.sandboxclient.domain.Location

interface StoreProvider {
    fun findAll(): List<Location>
}

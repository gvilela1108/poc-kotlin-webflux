package com.sandboxclient.repository

import com.sandboxclient.repository.entities.StoreLocationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StoreLocationJpaRepository : JpaRepository<StoreLocationEntity, Long>

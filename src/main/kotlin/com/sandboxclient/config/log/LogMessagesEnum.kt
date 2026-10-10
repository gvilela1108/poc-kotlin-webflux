package com.sandboxclient.config.log

enum class LogMessagesEnum(
    val code: String,
    val message: String,
) {
    // APPLICATION
    APP_STARTED("APP-001", "Application started successfully"),
    APP_SHUTDOWN("APP-002", "Application is shutting down"),

    // CONTROLLER
    STORE_FETCH_ALL_REQUEST("CTL-STORE-001", "Fetch all stores request received: page={}, size={}"),
    STORE_FETCH_BY_ID_REQUEST("CTL-STORE-002", "Fetch store by id request received: id={}"),
    STORE_FETCH_BY_ID_NOT_FOUND("CTL-STORE-003", "Store not found: id={}"),

    // SERVICE
    STORE_FETCH_ALL_STARTED("SRV-STORE-001", "Fetching all stores"),
    STORE_FETCH_ALL_RESULT("SRV-STORE-002", "Stores fetched successfully"),
    STORE_FETCH_BY_ID_STARTED("SRV-STORE-003", "Fetching store by id"),
    STORE_FETCH_BY_ID_RESULT("SRV-STORE-004", "Store fetched successfully"),
    STORE_FETCH_FAILED("SRV-STORE-005", "Failed to fetch stores"),

    // CACHE
    STORE_CACHE_HIT("CCH-001", "Store page cache hit: page={}, size={}"),
    STORE_CACHE_MISS("CCH-002", "Store page cache miss: page={}, size={}"),
    STORE_CACHE_UNAVAILABLE("CCH-003", "Redis cache unavailable: operation={}, reason={}"),

    // SYNCHRONIZATION
    STORE_SNAPSHOT_REFRESHED("SYN-001", "Store snapshot refreshed"),
    STORE_SYNCHRONIZATION_EMPTY("SYN-002", "Store synchronization skipped"),
    STORE_SYNCHRONIZATION_FAILED("SYN-003", "Store synchronization failed; keeping existing snapshot"),

    // PROVIDER - SANDBOXCLIENT API
    // PROVIDER - SANDBOXCLIENT API
    SANDBOXCLIENT_FETCH_STARTED("PRV-001", "Fetching locations from SandboxClient API"),
    SANDBOXCLIENT_FETCH_COMPLETED("PRV-002", "Locations fetched from SandboxClient API"),
    SANDBOXCLIENT_PAGE_FETCHED("PRV-003", "SandboxClient API page fetched"),
    EXTERNAL_REQUEST_FAILED("PRV-004", "External request to SandboxClient API failed"),

    // EXCEPTION
    UNEXPECTED_ERROR("ERR-001", "Unexpected internal error occurred"),
    ARGUMENTS_ERROR("ERR-002", "Invalid received parameters"),
    EXTERNAL_SERVICE_UNAVAILABLE("ERR-003", "External service unavailable"),
}

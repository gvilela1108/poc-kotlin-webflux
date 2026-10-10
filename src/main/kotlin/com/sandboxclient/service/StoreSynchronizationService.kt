package com.sandboxclient.synchronization

/** Coordinates refreshing the local store snapshot from the external source. */
interface StoreSynchronizationService {
    /**
     * Refreshes the local snapshot when the external source is available.
     * @return true when a new snapshot was persisted; false when the existing snapshot was retained.
     */
    fun synchronize(): Boolean
}

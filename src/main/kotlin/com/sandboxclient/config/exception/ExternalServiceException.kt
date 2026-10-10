package com.sandboxclient.config.exception

class ExternalServiceException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

package org.example.project

/**
 * Interface representing a platform.
 */
interface Platform {
    /** The name of the platform. */
    val name: String
}

/**
 * Returns the current platform instance.
 */
expect fun getPlatform(): Platform

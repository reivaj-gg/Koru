package org.example.project

/**
 * A class that provides a greeting message based on the platform.
 */
class Greeting {
    private val platform = getPlatform()

    /**
     * Returns a greeting message.
     */
    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}

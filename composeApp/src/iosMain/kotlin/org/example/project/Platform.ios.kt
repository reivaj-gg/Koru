package org.example.project

import platform.UIKit.UIDevice

/**
 * iOS implementation of the [Platform] interface.
 */
class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

/**
 * Returns the [IOSPlatform] instance.
 */
actual fun getPlatform(): Platform = IOSPlatform()

package nz.co.trademe.gradle.includeme.util

import org.gradle.api.initialization.Settings
import java.io.File

val Settings.projectDirectory: File
    get() = rootProject.projectDir

fun File.isGradleProject(): Boolean {
    return File(absolutePath, "settings.gradle").exists() || File(absolutePath, "settings.gradle.kts").exists()
}
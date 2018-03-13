package nz.co.trademe.gradle.includeme.model

import nz.co.trademe.gradle.includeme.util.Constants
import nz.co.trademe.gradle.includeme.util.PropertiesHelper
import nz.co.trademe.gradle.includeme.util.projectDirectory
import org.gradle.api.initialization.Settings
import java.io.File

data class IncludeMeProject(val name: String, val directory: File) {
    val projectPath: String = directory.absolutePath
}

fun IncludeMeProject.isEnabled(plugin: Settings): Boolean {
    return plugin.projectDirectory.let {
        val includeProperties = PropertiesHelper.readProperties(File(it, Constants.INCLUDE_PROPERTIES))
        includeProperties.keys.contains(name) && includeProperties.getProperty(name).toBoolean()
    }
}

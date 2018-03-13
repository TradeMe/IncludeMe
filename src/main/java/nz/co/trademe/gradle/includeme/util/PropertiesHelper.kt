package nz.co.trademe.gradle.includeme.util

import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import java.io.File
import java.util.*

class PropertiesHelper private constructor() {

    companion object {

        fun findIncludeProperties(projectDirectory: File): Properties {
            return readProperties(File(projectDirectory, Constants.INCLUDE_PROPERTIES))
        }

        fun findUsersGradleProperties(gradle: Gradle): Properties? {
            val gradleProperties = File(gradle.gradleUserHomeDir, Constants.GRADLE_PROPERTIES)

            if (gradleProperties.exists()) {
                return readProperties(gradleProperties)
            }
            return null
        }

        fun findGradleProperties(projectDirectory: File): Properties? {
            val localProperties = File(projectDirectory, Constants.GRADLE_PROPERTIES)
            return if (localProperties.exists()) {
                readProperties(localProperties)
            } else {
                null
            }
        }

        fun findLocalProperties(projectDirectory: File): Properties? {
            val localProperties = File(projectDirectory, Constants.LOCAL_PROPERTIES)
            return if (localProperties.exists()) {
                readProperties(localProperties)
            } else {
                null
            }
        }

        fun findAllGradleProperties(settings: Settings): List<Properties> {
            return listOfNotNull(findUsersGradleProperties(settings.gradle), findLocalProperties(settings.projectDirectory), findGradleProperties(settings.projectDirectory))
        }

        fun readProperties(file: File): Properties {
            return Properties().apply {
                if (file.exists()) load(file.inputStream())
            }
        }

    }
}
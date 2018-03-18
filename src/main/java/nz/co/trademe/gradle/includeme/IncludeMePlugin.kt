package nz.co.trademe.gradle.includeme

import nz.co.trademe.gradle.includeme.model.IncludeMeProject
import nz.co.trademe.gradle.includeme.model.isEnabled
import nz.co.trademe.gradle.includeme.util.Constants
import nz.co.trademe.gradle.includeme.util.PropertiesHelper
import nz.co.trademe.gradle.includeme.util.isGradleProject
import nz.co.trademe.gradle.includeme.util.log
import nz.co.trademe.gradle.includeme.util.projectDirectory
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.PluginAware
import java.io.File
import java.util.*

class IncludeMePlugin : Plugin<PluginAware> {

    override fun apply(project: PluginAware) {
        if (project !is Settings) {
            return
        }

        val projects = findGradleProjects(project).apply {
            log("Found ${this.size} projects to include")
        }

        val includeProperties = PropertiesHelper.findIncludeProperties(project.projectDirectory)

        //clear properties
        val keysToRemove = includeProperties.keys.toList() - projects.map { it.name }
        keysToRemove.onEach { includeProperties.remove(it) }

        projects.onEach { addProjectToIncludeProperties(includeProperties, it) }

        includeProperties.store(File(Constants.INCLUDE_PROPERTIES).outputStream(), null)

        projects.onEach { includeProject(project, it) }

        log("IncludeMe plugin applied")
    }

    /**
     * Adds an entry into the include.properties file for the given config
     */
    private fun addProjectToIncludeProperties(props: Properties, config: IncludeMeProject) {
        if (!props.containsKey(config.name)) {
            props.setProperty(config.name, false.toString())
        }
    }

    /**
     * Includes the external project into the current projects build.
     *
     * This is including a build with Gradle's composite builds API which handles dependency
     * resolution also.
     */
    private fun includeProject(settings: Settings, config: IncludeMeProject) {
        if (config.isEnabled(settings)) {
            log("Including ${config.name} as project")

            if (config.directory.exists()) {
                settings.includeBuild(config.projectPath)
            }
        }
    }

    /**
     * Finds Gradle projects in the search paths.
     *
     * Filters out projects that aren't on a whitelist.
     *
     * If no search paths are defined, the parent folder of the project will be used
     */
    private fun findGradleProjects(project: Settings): List<IncludeMeProject> {
        val searchPaths = findSearchPaths(project)
        val whitelistedProjects = findWhitelistedProjects(project)

        val gradleProjects = searchPaths.map {
            it.listFiles()
                    .filter { it.isGradleProject() }
                    .filter {
                        if (whitelistedProjects.isNotEmpty()) whitelistedProjects.contains(it.name) else true
                    }
                    .map {
                        IncludeMeProject(it.name, it)
                    }
        }.flatten()

        return gradleProjects
    }

    /**
     * Finds any projects declared in a whitelist
     */
    private fun findWhitelistedProjects(settings: Settings): List<String> {
        return PropertiesHelper.findAllGradleProperties(settings)
                .mapNotNull {
                    it.getProperty("includeme.whitelist")
                }
                .map { it.split(",") }
                .flatten()
    }

    /**
     * Finds any declared search paths
     */
    private fun findSearchPaths(settings: Settings): List<File> {
        val parentSearchPath = listOf(settings.projectDirectory.parentFile?.absolutePath)

        val declaredSearchPaths = PropertiesHelper.findAllGradleProperties(settings)
                .map {
                    it.getProperty("includeme.searchpaths")
                }

        val searchPaths: List<String?> = if (declaredSearchPaths.isEmpty()) parentSearchPath else declaredSearchPaths

        return searchPaths
                .mapNotNull { it?.split(",") }
                .flatten()
                .map { it.replaceFirst("~", System.getProperty("user.home")) }
                .map { File(it) }
                .filter { it.exists() }
    }

}



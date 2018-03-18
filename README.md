# IncludeMe

IncludeMe is a Gradle plugin that simplifies working with [composite builds.](https://docs.gradle.org/current/userguide/composite_builds.html)

IncludeMe scans directories for Gradle projects and provides a simple toggle to include a project as a composite build. 

No more trying to remember project paths or editing settings.gradle! 

What are composite builds?
-----
*From the Gradle documentation:*

> A composite build is simply a build that includes other builds. In many ways a composite build is similar to a Gradle multi-project build, except that instead of including single projects, complete builds are included.

Composite builds make it really easy to work with external projects and libraries by including them as if they were a module in the current project. 

Dependency substitution is handled by Gradle, so rather than publishing a library snapshot the project can simple be compiled.

Usage
-----

Apply IncludeMe to the settings.gradle file in your project:

```groovy
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'nz.co.trademe.includeme:includeme:1.0.1'
    }
}

include ':app'

apply plugin: 'nz.co.trademe.includeme'

```

After a Gradle sync, IncludeMe will create a `include.properties` file in the project:

```
myModule=false
myLibrary=false
```

Change the value of these properties (and perform a Gradle sync) to include the project as a composite build. 🎉

The `include.properties` should be added to `.gitignore`.

Requirements
-----

**Note:** IncludeMe requires Gradle 4.4 or higher. This version of Gradle includes a fix for the buildscript classpath issue with Android projects.

The warning for this issue has been removed in Android Gradle Plugin 3.1.0, but if using a version below 3.1.0, the following will need to be added to the projects `gradle.properties`:

```
android.enableBuildScriptClasspathCheck=false
```

Search paths
-----

IncludeMe will search for Gradle projects in the projects parent directory by default.

Different search paths (or multiple paths) can be declared in a projects `local.properties`, `gradle.properties` or the users `gradle.properties` file:

```
includeme.searchpaths=~/workspace/projects,~/workspace/libraries
```

Whitelist
-----

A project whitelist can also be setup to tell IncludeMe to only look for specific projects:

```
includeme.whitelist=myModule,myLibrary
```

The whitelist should include project directory names.


Troubleshooting
-----

If you find that IncludeMe is including a project in the build, but Gradle is still compiling the remote dependency rather than the project sources, the included project may not be set up correctly.

Gradle Composite Builds require that the projects `group` and `name` match the artifact group and name.

i.e. if the artifact is `com.myawesomecompany:alibrary:1.0.0`, then the library module should be named `alibrary` and the group should be set to `com.myawesomecompany`.

### Setting a group
The group for a module or project can be set with the `group` dsl. 

```groovy
//in build.gradle (library module)

group = com.myawesomecompany

```

### Setting the module/project name

The project/module name comes from the folder name by default. If you need the project name to be different from the folder name, it can be set in the projects `settings.gradle`.

```groovy
//in settings.gradle

include ':thelibrary'
project(':thelibrary').name = "alibrary"
```


## Contributing

We love contributions, but make sure to checkout `CONTRIBUTING.MD` first!


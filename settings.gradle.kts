pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.fabric.io/public") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
        maven {
            url = uri("http://repository.jetbrains.com/all")
            isAllowInsecureProtocol = true
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.fabric.io/public") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
        maven {
            url = uri("http://repository.jetbrains.com/all")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "MTemplate"
include(":app")
include(":mbase")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://maven.pkg.github.com/aiiisana/chat-library")
            credentials {
                username = providers.gradleProperty("gpr.usr").orNull ?: System.getenv("GPR_USER")
                password = providers.gradleProperty("gpr.token").orNull ?: System.getenv("GPR_TOKEN")
            }
        }
    }
}

rootProject.name = "Money"
include(":app")
include(":newscompose")

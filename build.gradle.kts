// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

tasks {
    register<Copy>("copyGitHooks") {
        description = "Copies the git hooks from root to the .git folder."
        from("$rootDir") {
            include("pre-commit")
          //  rename("(.*).sh", "$1")
        }
        into("$rootDir/.git/hooks")
        eachFile {
            fileMode = 0b111101101
        }
    }

    register<Delete>("deletePreviousGitHooks") {
        description = "Deleting previous gitHook."

        val preCommit = "${rootProject.rootDir}/.git/hooks/pre-commit"
        if (file(preCommit).exists()) {
            delete(preCommit)
        }
    }
}

project.tasks.getByPath("copyGitHooks").dependsOn("deletePreviousGitHooks")
project.tasks.getByPath(":FlowForms-Core:preBuild").dependsOn("copyGitHooks")

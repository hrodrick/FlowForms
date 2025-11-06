plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

fun flowFormsCoreProject() = project(":FlowForms-Core")

kotlin {
    android {}
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries {
            framework {
                export(flowFormsCoreProject())
                baseName = "shared"
            }
            sharedLib {
                export(flowFormsCoreProject())
            }
        }

    }

    sourceSets {
        commonMain {
            dependencies {
                api(flowFormsCoreProject())
                implementation(libs.kotlin.stdlib)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.lifecycle.viewmodelCompose)
            }
        }
        androidUnitTest { }
        iosMain { }
        iosX64Main {}
        iosArm64Main {}
        iosSimulatorArm64Main {}
    }
}

android {
    namespace = "com.rootstrap.flowforms.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

// utility functions

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.implementations(list : List<String>) {
    list.forEach {
        implementation(it)
    }
}

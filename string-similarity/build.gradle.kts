import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JsMainFunctionExecutionMode
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode
import org.jetbrains.kotlin.konan.target.HostManager
import java.util.Base64

plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
    id("binary-compatibility-validator")
    id("com.diffplug.spotless")
    id("org.jetbrains.dokka")
}

kotlin {
    explicitApi()
    jvm()
    js {
        compilerOptions {
            moduleKind = JsModuleKind.MODULE_UMD
            sourceMap = true
            main = JsMainFunctionExecutionMode.NO_CALL
            sourceMapEmbedSources = JsSourceMapEmbedMode.SOURCE_MAP_SOURCE_CONTENT_ALWAYS
        }
        nodejs()
        browser()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    // Tier 1
    linuxX64()

    // Tier 2
    linuxArm64()

    // Tier 3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()

    // Apple macOS hosts only
    if (HostManager.hostIsMac) {
        // Tier 1
        macosX64()
        macosArm64()
        iosSimulatorArm64()
        iosX64()

        // Tier 2
        watchosSimulatorArm64()
        watchosX64()
        watchosArm32()
        watchosArm64()
        tvosSimulatorArm64()
        tvosX64()
        tvosArm64()
        iosArm64()

        // Tier 3
        watchosDeviceArm64()
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test.common)
                implementation(libs.kotlin.test.annotations.common)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.kotlin.test.junit)
            }
        }
        jsTest {
            dependencies {
                implementation(libs.kotlin.test.js)
            }
        }
    }
}

tasks {
    dokkaGeneratePublicationHtml {
        outputDirectory = rootDir.resolve("docs")
    }
}

publishing {
    repositories {
        maven("https://europe-west3-maven.pkg.dev/mik-music/mikbot") {
            credentials {
                username = "_json_key_base64"
                password = System.getenv("GOOGLE_KEY")?.toByteArray()?.let {
                    Base64.getEncoder().encodeToString(it)
                }
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}


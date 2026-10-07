plugins {
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
}

val minecraft = property("deps.minecraft") as String

loom {
    silentMojangMappingsLicense()
//    accessWidenerPath = rootProject.file("src/main/resources/sfcr.accesswidener")

    forge {
//        convertAccessWideners = true
//        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig("${property("mod.id")}.mixins.json")
    }

    if (sc.current.parsed < "1.20") {
        mixin.useLegacyMixinAp = true
        mixin.defaultRefmapName = "${property("mod.id")}-${minecraft}-forge-refmap.json"
    }
}

tasks.named<ProcessResources>("processResources") {

    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["mod_group"] =     prop("mod.group")
        this["mod_id"] =        prop("mod.id")
        this["mod_name"] =      prop("mod.name")
        this["mod_version"] =   prop("mod.version")
        this["mod_description"]=prop("mod.description")
        this["mod_author"] =    prop("mod.author")
        this["mod_contributor"]=prop("mod.contributor")
        this["mod_sources"] =   prop("mod.sources")
        this["mod_issues"] =    prop("mod.issues")
        this["mod_homepage"] =  prop("mod.homepage")
        this["mod_modrinth"] =  prop("mod.modrinth")
        this["mod_mcmod"] =     prop("mod.mcmod")
        this["mod_license"] =   prop("mod.license")
        this["mod_icon"] =      prop("mod.icon")

        this["version_range"] = prop("version_range")
        this["forge_min_version"] = prop("forge_min_version")
        this["cloth_id"] =      if (sc.current.parsed > "1.18") "cloth_config" else "cloth-config"

        // insert version-specific mixins

        // insert deps
    }

    filesMatching(listOf("META-INF/mods.toml", "${prop("mod.id")}.mixins.json")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${minecraft}-forge"
base.archivesName = property("mod.id") as String

repositories {
    mavenLocal()
    maven("https://maven.architectury.dev/")
    maven("https://maven.shedaniel.me/")
    maven("https://api.modrinth.com/maven")
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge:forge:${property("deps.forge")}")

    if (sc.current.parsed < "1.20") {
        annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    }

    // @NonNull 注解（org.jspecify）：新版由映射自带，1.20.1 forge 映射不含 jspecify，需显式提供
    compileOnly("org.jspecify:jspecify:1.0.0")

    // MixinExtras
//    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${property("deps.mixinextras")}")!!)
//    if (sc.current.parsed > "1.17") {
//        implementation(include("io.github.llamalad7:mixinextras-forge:${property("deps.mixinextras")}")) {}
//    } else {
//        // mixinextras JIJ on 1.16.5 is unsupported
//        implementation("io.github.llamalad7:mixinextras-forge:${property("deps.mixinextras")}") {}
//    }

    // cloth
    modApi("me.shedaniel.cloth:cloth-config-forge:${property("deps.cloth")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    // pehkui
    modCompileOnly("maven.modrinth:pehkui:${property("deps.pehkui")}")
    // random-mob-sizes
    if (sc.current.parsed > "1.16.5")
        modCompileOnly("maven.modrinth:random-mob-sizes:${property("deps.random-mob-sizes")}")
}

tasks {
    processResources {
        exclude("**/fabric.mod.json", "**/*.accesswidener", "**/neoforge.mods.toml")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs"))
        dependsOn("build")
    }

    jar {
        manifest.attributes["MixinConfigs"] = "${project.property("mod.id")}.mixins.json"
    }

    // resolve 1.16.5 mixinextras classDefNotFound / JIJ load failure issue, powered by https://www.doubao.com/
    // Merged .class with mixinextras...
//    if (sc.current.parsed < "1.17") {
//        val outerJar by lazy {
//            configurations.runtimeClasspath.get().files.first {
//                it.name.startsWith("mixinextras-forge-${project.property("deps.mixinextras")}")
//            }
//        }
//
//        val copyMixinExtras = register("copyMixinExtras", Copy::class) {
//            group = "build"
//            description = "Copy MixinExtras classes (from nested jar) to compile directory"
//
//            dependsOn(configurations.runtimeClasspath)
//
//            val tempDir = file("$buildDir/tmp/mixinextras")
//            delete(tempDir)
//            copy {
//                from(zipTree(outerJar)) {
//                    include("META-INF/jars/MixinExtras-${project.property("deps.mixinextras")}.jar") // ֻ��ȡ�ڲ����jar
//                }
//                into(tempDir)
//            }
//
//            val innerJar = file("$tempDir/META-INF/jars/MixinExtras-${project.property("deps.mixinextras")}.jar")
//            from(zipTree(innerJar)) {
//                include("com/**")
//                exclude("module-info.class")
//            }
//
//            into("$buildDir/classes/java/main")
//
//            doLast {
//                delete(tempDir)
//            }
//
//            notCompatibleWithConfigurationCache("Copy MixinExtras classes from nested jar")
//        }
//
//        val copyMixinExtrasLicense = register("copyMixinExtrasLicense", Copy::class) {
//            dependsOn(configurations.runtimeClasspath)
//
//            from(zipTree(outerJar)) {
//                include("LICENSE_MixinExtras")
//            }
//            into("$buildDir/classes/java/main/META-INF/licenses/mixinextras")
//
//            notCompatibleWithConfigurationCache("Copy MixinExtras LICENSE file")
//        }
//
//        jar {
//            dependsOn(copyMixinExtras, copyMixinExtrasLicense)
//        }
//        remapJar {
//            dependsOn(copyMixinExtras)
//        }
//    }
}

java {
    val javaCompat = if (stonecutter.eval(stonecutter.current.version, ">=1.21")) {
        JavaVersion.VERSION_21
    } else if (stonecutter.eval(stonecutter.current.version, ">=1.18")) {
        JavaVersion.VERSION_17
    } else {
        JavaVersion.VERSION_1_8
    }
    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
}

plugins {
    id("dev.architectury.loom-no-remap") version "1.14-SNAPSHOT"
}

val minecraft = property("deps.minecraft") as String

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
        this["neoforge_min_version"] = prop("neoforge_min_version")
    }

    filesMatching(listOf("META-INF/neoforge.mods.toml", "${prop("mod.id")}.mixins.json")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${minecraft}-neoforge"
base.archivesName = property("mod.id") as String

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.architectury.dev/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.shedaniel.me/")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    neoForge("net.neoforged:neoforge:${property("deps.neoforge")}")

    // cloth（neoforge 版本）
    api("me.shedaniel.cloth:cloth-config-neoforge:${property("deps.cloth")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

tasks {
    processResources {
        // neoforge 构建包里剔除 fabric 元数据和其他 loader 的 mods.toml。
        exclude("**/fabric.mod.json", "**/mods.toml", "**/*.accesswidener")
    }

    jar {
        // NeoForge 靠 jar manifest 的 MixinConfigs 属性加载 mixin（fabric 侧走 fabric.mod.json）。
        manifest.attributes["MixinConfigs"] = "${project.property("mod.id")}.mixins.json"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

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
        this["fabric_api_id"] = "fabric-api"

        // insert version-specific mixins

        // insert deps
    }

    filesMatching(listOf("fabric.mod.json", "${prop("mod.id")}.mixins.json")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${minecraft}-fabric"
base.archivesName = property("mod.id") as String

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.architectury.dev/")
    maven("https://maven.shedaniel.me/")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    implementation("net.fabricmc:fabric-loader:${property("deps.fabric")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")

    // @NonNull 注解（org.jspecify）
    compileOnly("org.jspecify:jspecify:1.0.0")

    // modmenu
    implementation("com.terraformersmc:modmenu:${property("deps.modmenu")}")
    // cloth config
    api("me.shedaniel.cloth:cloth-config-fabric:${property("deps.cloth")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/mods.toml", "**/*.accesswidener", "**/*.mcmeta", "**/*-forge.mixins.json")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

plugins {
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
}

val minecraft = property("deps.minecraft") as String

loom {
    silentMojangMappingsLicense()
    //accessWidenerPath = rootProject.file("src/main/resources/sfcr.accesswidener")
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
        this["neoforge_min_version"] = prop("neoforge_min_version")

        // insert version-specific mixins
    }

    filesMatching(listOf("META-INF/neoforge.mods.toml", "${prop("mod.id")}.mixins.json")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${minecraft}-neoforge"
base.archivesName = property("mod.id") as String

repositories {
    mavenLocal()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.shedaniel.me/")
    maven("https://api.modrinth.com/maven")
//    maven("https://cursemaven.com")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    mappings(loom.officialMojangMappings())
    neoForge("net.neoforged:neoforge:${property("deps.neoforge")}")

    // @NonNull 注解（org.jspecify）：新版由映射自带，旧版（1.21.1）需显式提供
    compileOnly("org.jspecify:jspecify:1.0.0")

    modApi("me.shedaniel.cloth:cloth-config-neoforge:${property("deps.cloth")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    if (sc.current.parsed < "1.21.11") {
        // pehkui
        modCompileOnly("maven.modrinth:pehkui:${property("deps.pehkui")}")
        // random-mob-sizes
//        modCompileOnly("curse.maven:random-mob-sizes-555230:${property("deps.random-mob-sizes")}")
    }
}

tasks {
    processResources {
        exclude("**/fabric.mod.json", "**/mods.toml", "**/*.accesswidener")
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
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

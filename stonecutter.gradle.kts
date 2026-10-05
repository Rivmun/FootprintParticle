plugins {
    id("dev.kikugie.stonecutter")
    // Forgix：合并同一 MC 版本、不同 loader 的产物。
    // https://plugins.gradle.org/plugin/io.github.pacifistmc.forgix
    id("io.github.pacifistmc.forgix") version "2.0.0"
}

stonecutter active "26.1-fabric"

stonecutter parameters {
    // 供 build 脚本用 sc.fabric / sc.neoforge / sc.forge 常量分支；后续加 loader 时直接扩展参数即可。
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
}

// ===================== Forgix 合并配置 =====================
// Stonecutter 不会把 stonecutter.properties.toml 的顶层全局属性注入根工程（仅注入各子工程节点），
// 因此根构建脚本无法用 providers.gradleProperty/findProperty 取到 mod.id、mod.version，
// 这里直接解析 toml 的顶层（第一个 [section] 之前）全局属性。
fun tomlGlobalProp(key: String): String {
    val global = file("stonecutter.properties.toml").readLines()
        .takeWhile { !it.trimStart().startsWith("[") }
    return global.first { it.trim().startsWith("$key=") }
        .substringAfter('=').trim().trim('"')
}
val modId      = tomlGlobalProp("mod.id")
val modVersion = tomlGlobalProp("mod.version")

// 用 -Pforgix.mc=26.1 指定要合并哪个 MC 版本；未指定则回落 Stonecutter active 前缀，再回落 26.1
val targetMc: String = (findProperty("forgix.mc") as String?)
    ?: (findProperty("stonecutter.node") as String?)?.substringBefore('-')
    ?: "26.1"

// 每个 MC 版本参与合并的 loader 集合，必须与 settings.gradle.kts 中 mc(...) 声明一致。
// 仅无混淆的 26.x 参与合并；1.21.11 及更早版本仍走 remap 字节必然分裂，不参与 Forgix。
val loadersFor: (String) -> List<String> = { mc ->
    when (mc) {
        "26.3", "26.2", "26.1" -> listOf("fabric", "neoforge")
        else -> error("版本 $mc 不参与 Forgix 合并（仅 26.x unobf 版本支持）")
    }
}

val activeLoaders = loadersFor(targetMc)

// loader 组合 → 产物平台后缀昵称
val classifier: String = when (activeLoaders.sorted().joinToString("+")) {
    "fabric+neoforge" -> "neobric"
    "fabric+forge"    -> "forbric"
    else              -> activeLoaders.joinToString("-")
}

forgix {
    silence = true
    // 合并产物统一落到 build/merged
    destinationDirectory = layout.buildDirectory.dir("merged")
    archiveBaseName      = modId
    archiveVersion       = "$modVersion+$targetMc"
    archiveClassifier    = classifier

    // Stonecutter 子工程名是 :<mc>-<loader>，Forgix 自动探测失效，
    // 用 merge("<mc>-<loader>") 指定实际子工程；inputJar 直接指向其最终产物文件路径：
    // 路径在配置期即可确定（provider 恒 present），且只含 FileSystemLocation、不捕获 Project 引用，
    // 因此 MergeJarsTask 可正常序列化进配置缓存。
    activeLoaders.forEach { loader ->
        val sub = project(":$targetMc-$loader")
        val mcActual = (sub.findProperty("deps.minecraft") as String?)
            ?: error("子工程 :$targetMc-$loader 缺少 deps.minecraft 属性")
        val jarName = "$modId-$modVersion+$mcActual-$loader.jar"
        val jar = sub.layout.buildDirectory.file("libs/$jarName")
        merge("$targetMc-$loader") { inputJar = jar }
    }
}

// 让 mergeJars 依赖所选版本下所有 loader 子工程的构建产物
tasks.named("mergeJars") {
    activeLoaders.forEach { dependsOn(":$targetMc-$it:build") }
    // Forgix 的 MergeJarsTask 内部持有 Project 引用，与配置缓存不兼容 → 仅对该任务声明式关闭缓存
    notCompatibleWithConfigurationCache("Forgix MergeJarsTask 持有 Project 引用，无法序列化到配置缓存")
    doLast { logger.lifecycle("forgix archive -> build/merged/$modId-$modVersion+$targetMc-$classifier.jar") }
}

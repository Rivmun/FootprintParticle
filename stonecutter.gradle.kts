plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.1-fabric"

stonecutter parameters {
    // 供 build 脚本用 sc.fabric / sc.neoforge / sc.forge 常量分支；后续加 loader 时直接扩展参数即可。
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
}

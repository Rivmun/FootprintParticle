//? if fabric {
package com.rimo.footprintparticle.loaders.fabric;

import com.rimo.footprintparticle.config.Config;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfigClient;

/**
 * Fabric ModMenu 配置入口（{@code fabric.mod.json} 的 {@code modmenu} entrypoint）。
 * NeoForge 侧的等价物在 {@code loaders.neoforge.Platform} 里通过 {@code IConfigScreenFactory} 扩展点注册。
 */
public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfigClient.getConfigScreen(Config.class, parent).get();
	}
}
//? }

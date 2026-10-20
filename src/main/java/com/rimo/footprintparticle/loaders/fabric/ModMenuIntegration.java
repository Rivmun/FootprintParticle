//? if fabric {
package com.rimo.footprintparticle.loaders.fabric;

import com.rimo.footprintparticle.config.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new ConfigScreen().build(parent);
	}
}
//? }

package com.rimo.footprintparticle.config;

import com.rimo.footprintparticle.Client;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//? if < 1.19.2
//import net.minecraft.network.chat.TranslatableComponent;

import static com.rimo.footprintparticle.Client.CONFIG;

/**
 * 唯一配置屏幕。
 * 原生屏幕的跨版本构造会破坏模组构建产物的多版本兼容，故此项目不引入无 Cloth 提示屏。
 * （特指 26.1 构建对于 26.1-26.3 的全版本兼容）
 */
public class ConfigScreen {
	ConfigBuilder builder;
	ConfigEntryBuilder entryBuilder;

	public Screen build(Screen parent) {
		builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(t("title"));
		entryBuilder = builder.entryBuilder();

		buildCategory(builder.getOrCreateCategory(t("category.default")));
		buildMiscCategory(builder.getOrCreateCategory(t("category.misc")));

		// Saving...
		builder.setSavingRunnable(Client.CONFIG::save);

		return builder.build();
	}

	private void buildCategory(ConfigCategory general) {
		general.addEntry(entryBuilder
				.startEnumSelector(t("option.enableMod")
						, WorkMode.class
						, CONFIG.getEnableMod())
				.setDefaultValue(Config.DEF_ENABLE_MOD)
				.setEnumNameProvider(value -> t(value.toString()))
				.setSaveConsumer(CONFIG::setEnableMod)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(t("option.wetDuration")
						, CONFIG.getWetDuration()
						,0
						,30)
				.setDefaultValue(Config.DEF_WET_DURATION)
				.setTextGetter(value -> {
					if (value == 0) {
						return t("disabled");
					} else {
						return t("seconds", value);
					}
				})
				.setTooltip(t("option.wetDuration.@Tooltip"))
				.setSaveConsumer(CONFIG::setWetDuration)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(t("option.secPerPrint")
						,(int) (CONFIG.getSecPerPrint() * 10)
						,0
						,20)
				.setDefaultValue(Config.DEF_SEC_PER_PRINT)
				.setTextGetter(value -> {
					if (value == 0) {
						return p("¿");
					} else {
						return t("seconds", value / 10);
					}
				})
				.setSaveConsumer(CONFIG::setSecPerPrint)
				.build()
		);
		general.addEntry(entryBuilder
				.startFloatField(t("option.printLifetime")
						, CONFIG.getPrintLifetime())
				.setDefaultValue(Config.DEF_PRINT_LIFETIME)
				.setSaveConsumer(CONFIG::setPrintLifetime)
				.build()
		);
		general.addEntry(entryBuilder
				.startFloatField(t("option.watermarkLifetime")
						, CONFIG.getWatermarkLifetime())
				.setDefaultValue(Config.DEF_WATERMARK_LIFETIME)
				.setSaveConsumer(CONFIG::setWatermarkLifetime)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(t("option.lifeTimeAcc")
						, CONFIG.getLifeTimeAcc()
						, 0
						, 10)
				.setDefaultValue(Config.DEF_LIFETIME_ACC)
				.setTextGetter(value -> p(value + "x"))
				.setTooltip(t("option.lifeTimeAcc.@Tooltip"))
				.setSaveConsumer(CONFIG::setLifeTimeAcc)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(t("option.footprintAlpha")
						,(int) (CONFIG.getFootprintAlpha() * 10)
						,1
						,10)
				.setDefaultValue(Config.DEF_FOOTPRINT_ALPHA)
				.setTextGetter(value -> p(value * 10 + "%"))
				.setSaveConsumer(CONFIG::setFootprintAlpha)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(t("option.watermarkAlpha")
						,(int) (CONFIG.getWatermarkAlpha() * 10)
						,1
						,10)
				.setDefaultValue(Config.DEF_WATERMARK_ALPHA)
				.setTextGetter(value -> p(value * 10 + "%"))
				.setSaveConsumer(CONFIG::setWatermarkAlpha)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(t("option.printHeight")
						,(int) (CONFIG.getPrintHeight() / 0.0625F)
						,-8
						,8)
				.setDefaultValue(Config.DEF_PRINT_HEIGHT)
				.setTextGetter(value -> t("blocks", value * 0.0625f))
				.setTooltip(t("option.printHeight.@Tooltip"))
				.setSaveConsumer(CONFIG::setPrintHeight)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(t("option.applyBlocks")
						, CONFIG.getApplyBlocks())
				.setDefaultValue(Config.DEF_APPLYBLOCKS)
				.setTooltip(t("option.applyBlocks.@Tooltip"))
				.setSaveConsumer(CONFIG::setApplyBlocks)
				.build()
		);
		general.addEntry(entryBuilder
				.startFloatField(t("option.hardnessGate")
						, CONFIG.getHardnessGate())
				.setDefaultValue(Config.DEF_HARDNESS_GATE)
				.setTooltip(t("option.hardnessGate.@Tooltip"))
				.setSaveConsumer(CONFIG::setHardnessGate)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(t("option.excludedBlocks")
						, CONFIG.getExcludedBlocks())
				.setDefaultValue(Config.DEF_EXCLUDEDBLOCKS)
				.setTooltip(t("option.excludedBlocks.@Tooltip"))
				.setSaveConsumer(CONFIG::setExcludedBlocks)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(t("option.blockHeight")
						, CONFIG.getBlockHeight())
				.setDefaultValue(Config.DEF_BLOCKHEIGHT)
				.setTooltip(t("option.blockHeight.@Tooltip"))
				.setSaveConsumer(CONFIG::setBlockHeight)
				.build()
		);
		general.addEntry(entryBuilder
				.startBooleanToggle(t("option.canGenWhenInvisible")
						, CONFIG.getCanGenWhenInvisible())
				.setDefaultValue(Config.DEF_CAN_GEN_WHEN_INVISIBLE)
				.setSaveConsumer(CONFIG::setCanGenWhenInvisible)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(t("option.excludedMobs")
						, CONFIG.getExcludedMobs())
				.setDefaultValue(Config.DEF_MODS)
				.setTooltip(t("option.excludedMobs.@Tooltip"))
				.setSaveConsumer(CONFIG::setExcludedMobs)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(t("option.mobInterval")
						, CONFIG.getMobInterval())
				.setDefaultValue(Config.DEF_MOB_INTERVAL)
				.setTooltip(t("option.mobInterval.@Tooltip"))
				.setSaveConsumer(CONFIG::setMobInterval)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(t("option.sizePerMob")
						, CONFIG.getSizePerMob())
				.setDefaultValue(Config.DEF_SIZE)
				.setTooltip(t("option.sizePerMob.@Tooltip"))
				.setSaveConsumer(CONFIG::setSizePerMob)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(t("option.horseLikeMobs")
						, CONFIG.getHorseLikeMobs())
				.setDefaultValue(Config.DEF_FOUR_LEGS)
				.setTooltip(t("option.horseLikeMobs.@Tooltip"))
				.setSaveConsumer(CONFIG::setHorseLikeMobs)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(t("option.spiderLikeMobs")
						, CONFIG.getSpiderLikeMobs())
				.setDefaultValue(Config.DEF_EIGHT_LEGS)
				.setTooltip(t("option.spiderLikeMobs.@Tooltip"))
				.setSaveConsumer(CONFIG::setSpiderLikeMobs)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(t("option.customPrint")
						, CONFIG.getCustomPrint())
				.setDefaultValue(Config.DEF_CUSTOM_PRINT)
				.setTooltip(t("option.customPrint.@Tooltip"))
				.setSaveConsumer(CONFIG::setCustomPrint)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntField(t("option.footprintSize")
						, CONFIG.getFootprintSize())
				.setDefaultValue(Config.DEF_FOOTPRINT_SIZE)
				.setTooltip(t("option.footprintSize.@Tooltip"))
				.setSaveConsumer(CONFIG::setFootprintSize)
				.build()
		);
	}

	private void buildMiscCategory(ConfigCategory misc) {
		misc.addEntry(entryBuilder
				.startIntSlider(t("option.railSpark")
						,(int) (CONFIG.getRailFlameRange() * 10)
						,0
						,10)
				.setDefaultValue(Config.DEF_RAIL_FLAME_RANGE)
				.setTextGetter(value -> {
					if (value != 0) {
						return p(value * 10 + "%");
					} else {
						return t("disabled");
					}
				})
				.setSaveConsumer(CONFIG::setRailFlameRange)
				.build()
		);
		misc.addEntry(entryBuilder
				.startBooleanToggle(t("option.boatTrail")
						, CONFIG.isEnableBoatTrail())
				.setDefaultValue(Config.DEF_ENABLE_BOAT_TRAIL)
				.setSaveConsumer(CONFIG::setEnableBoatTrail)
				.build()
		);
		misc.addEntry(entryBuilder
				.startEnumSelector(t("option.swimPop")
						, WorkMode.class
						, CONFIG.getSwimPopLevel())
				.setDefaultValue(Config.DEF_SWIM_POP_LEVEL)
				.setEnumNameProvider(value -> t(value.toString()))
				.setSaveConsumer(CONFIG::setSwimPopLevel)
				.build()
		);
		misc.addEntry(entryBuilder
				.startEnumSelector(t("option.snowDust")
						, WorkMode.class
						, CONFIG.getSnowDustLevel())
				.setDefaultValue(Config.DEF_SNOW_DUST_LEVEL)
				.setEnumNameProvider(value -> t(value.toString()))
				.setSaveConsumer(CONFIG::setSnowDustLevel)
				.build()
		);
		misc.addEntry(entryBuilder
				.startEnumSelector(t("option.waterSplash")
						, WorkMode.class
						, CONFIG.getWaterSplashLevel())
				.setDefaultValue(Config.DEF_WATER_SPLASH_LEVEL)
				.setEnumNameProvider(value -> t(value.toString()))
				.setSaveConsumer(CONFIG::setWaterSplashLevel)
				.build()
		);
	}

	//? if > 1.18.2 {
	private Component t(String key, Object... args) {
		return Component.translatable("text.footprintparticle." + key, args);
	}
	//? } else {
	/*private TranslatableComponent t(String key, Object... args) {
		return new TranslatableComponent("text.footprintparticle." + key, args);
	}
	*///? }

	private Component p(String text) {
		return Component.nullToEmpty(text);
	}
}

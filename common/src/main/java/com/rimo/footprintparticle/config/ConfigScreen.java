package com.rimo.footprintparticle.config;

import com.rimo.footprintparticle.FPPClient;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
	ConfigBuilder builder = ConfigBuilder.create()
    		.setParentScreen(MinecraftClient.getInstance().currentScreen)
    		.setTitle(Text.translatable("text.footprintparticle.option.title"));
	ConfigEntryBuilder entryBuilder = builder.entryBuilder();

    ConfigCategory general = builder.getOrCreateCategory(Text.translatable("text.footprintparticle.option.title"));
    ConfigCategory misc = builder.getOrCreateCategory(Text.translatable("text.footprintparticle.option.misc"));

	FPPConfig config = FPPClient.CONFIGHOLDER.getConfig();

	public Screen buildScreen() {
    	buildCategory();
    	buildMiscCategory();

    	// Saving...
    	builder.setSavingRunnable(FPPClient.CONFIGHOLDER::save);

    	return builder.build();
	}

	private void buildCategory() {
		general.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.enableMod")
						,config.isEnable()
						,0
						,2)
				.setDefaultValue(2)
				.setTextGetter(this::getAllMobOrPlayerOnlyText)
				.setSaveConsumer(config::setEnableMod)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.wetDuration")
						,config.getWetDuration()
						,0
						,30)
				.setDefaultValue(10)
				.setTextGetter(value -> {
					if (value == 0) {
						return Text.translatable("text.footprintparticle.disabled");
					} else {
						return Text.translatable("text.footprintparticle.seconds", value);
					}
				})
				.setTooltip(Text.translatable("text.footprintparticle.option.wetDuration.@Tooltip"))
				.setSaveConsumer(config::setWetDuration)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.secPerPrint")
						,(int) (config.getSecPerPrint() * 10)
						,0
						,10)
				.setDefaultValue(5)
				.setTextGetter(value -> {
					if (value == 0) {
						return Text.of("¿");
					} else {
						return Text.translatable("text.footprintparticle.seconds", value / 10f);
					}
				})
				.setSaveConsumer(value -> config.setSecPerPrint(value / 10f))
				.build()
		);
		general.addEntry(entryBuilder
				.startFloatField(Text.translatable("text.footprintparticle.option.printLifetime")
						,config.getPrintLifetime())
				.setDefaultValue(5.0f)
				.setSaveConsumer(config::setPrintLifetime)
				.build()
		);
		general.addEntry(entryBuilder
				.startFloatField(Text.translatable("text.footprintparticle.option.watermarkLifetime")
						,config.getWatermarkLifetime())
				.setDefaultValue(5.0f)
				.setSaveConsumer(config::setWatermarkLifetime)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.lifeTimeAcc")
						,config.getLifeTimeAcc()
						,0
						,10)
				.setDefaultValue(0)
				.setTooltip(Text.translatable("text.footprintparticle.option.lifeTimeAcc.@Tooltip"))
				.setSaveConsumer(config::setLifeTimeAcc)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.footprintAlpha")
						,(int) (config.getFootprintAlpha() * 10)
						,1
						,10)
				.setDefaultValue(7)
				.setTextGetter(value -> Text.of(value * 10 + "%"))
				.setSaveConsumer(value -> config.setFootprintAlpha(value / 10f))
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.watermarkAlpha")
						,(int) (config.getWatermarkAlpha() * 10)
						,1
						,10)
				.setDefaultValue(4)
				.setTextGetter(value -> Text.of(value * 10 + "%"))
				.setSaveConsumer(value -> config.setWatermarkAlpha(value / 10f))
				.build()
		);
		general.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.printHeight")
						,(int) (config.getPrintHeight() / 0.0625f)
						,-8
						,8)
				.setDefaultValue(0)
				.setTextGetter(value -> Text.translatable("text.footprintparticle.blocks", value * 0.0625f))
				.setTooltip(Text.translatable("text.footprintparticle.option.printHeight.@Tooltip"))
				.setSaveConsumer(value -> config.setPrintHeight(value * 0.0625f))
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.applyBlocks")
						,config.getApplyBlocks())
				.setDefaultValue(FPPConfig.DEF_APPLYBLOCKS)
				.setTooltip(Text.translatable("text.footprintparticle.option.applyBlocks.@Tooltip"))
				.setSaveConsumer(config::setApplyBlocks)
				.build()
		);
		general.addEntry(entryBuilder
				.startFloatField(Text.translatable("text.footprintparticle.option.hardnessGate")
						,config.getHardnessGate())
				.setDefaultValue(0.7f)
				.setTooltip(Text.translatable("text.footprintparticle.option.hardnessGate.@Tooltip"))
				.setSaveConsumer(config::setHardnessGate)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.excludedBlocks")
						,config.getExcludedBlocks())
				.setDefaultValue(FPPConfig.DEF_EXCLUDEDBLOCKS)
				.setTooltip(Text.translatable("text.footprintparticle.option.excludedBlocks.@Tooltip"))
				.setSaveConsumer(config::setExcludedBlocks)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.blockHeight")
						,config.getBlockHeight())
				.setDefaultValue(FPPConfig.DEF_BLOCKHEIGHT)
				.setTooltip(Text.translatable("text.footprintparticle.option.blockHeight.@Tooltip"))
				.setSaveConsumer(config::setBlockHeight)
				.build()
		);
		general.addEntry(entryBuilder
				.startBooleanToggle(Text.translatable("text.footprintparticle.option.canGenWhenInvisible")
						,config.getCanGenWhenInvisible())
				.setDefaultValue(true)
				.setSaveConsumer(config::setCanGenWhenInvisible)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.excludedMobs")
						,config.getExcludedMobs())
				.setDefaultValue(FPPConfig.DEF_MODS)
				.setTooltip(Text.translatable("text.footprintparticle.option.excludedMobs.@Tooltip"))
				.setSaveConsumer(config::setExcludedMobs)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.mobInterval")
						,config.getMobInterval())
				.setDefaultValue(FPPConfig.DEF_MOB_INTERVAL)
				.setTooltip(Text.translatable("text.footprintparticle.option.mobInterval.@Tooltip"))
				.setSaveConsumer(config::setMobInterval)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.sizePerMob")
						,config.getSizePerMob())
				.setDefaultValue(FPPConfig.DEF_SIZE)
				.setTooltip(Text.translatable("text.footprintparticle.option.sizePerMob.@Tooltip"))
				.setSaveConsumer(config::setSizePerMob)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.horseLikeMobs")
						,config.getHorseLikeMobs())
				.setDefaultValue(FPPConfig.DEF_FOUR_LEGS)
				.setTooltip(Text.translatable("text.footprintparticle.option.horseLikeMobs.@Tooltip"))
				.setSaveConsumer(config::setHorseLikeMobs)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.spiderLikeMobs")
						,config.getSpiderLikeMobs())
				.setDefaultValue(FPPConfig.DEF_EIGHT_LEGS)
				.setTooltip(Text.translatable("text.footprintparticle.option.spiderLikeMobs.@Tooltip"))
				.setSaveConsumer(config::setSpiderLikeMobs)
				.build()
		);
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.customPrint")
						,config.getCustomPrint())
				.setDefaultValue(FPPConfig.DEF_CUSTOM_PRINT)
				.setTooltip(Text.translatable("text.footprintparticle.option.customPrint.@Tooltip"))
				.setSaveConsumer(config::setCustomPrint)
				.build()
		);
		general.addEntry(entryBuilder
				.startIntField(Text.translatable("text.footprintparticle.option.footprintSize")
						,config.getFootprintSize())
				.setDefaultValue(5)
				.setTooltip(Text.translatable("text.footprintparticle.option.footprintSize.@Tooltip"))
				.setSaveConsumer(config::setFootprintSize)
				.build()
		);
	}

	private void buildMiscCategory() {
		misc.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.railSpark")
						,(int) (config.getRailFlameRange() * 10)
						,0
						,10)
				.setDefaultValue(2)
				.setTextGetter(value -> {
					if (value != 0) {
						return Text.of(value * 10 + "%");
					} else {
						return Text.translatable("text.footprintparticle.disabled");
					}
				})
				.setSaveConsumer(value -> config.setRailFlameRange(value / 10f))
				.build()
		);
		misc.addEntry(entryBuilder
				.startBooleanToggle(Text.translatable("text.footprintparticle.option.boatTrail")
						,config.isEnableBoatTrail())
				.setDefaultValue(true)
				.setSaveConsumer(config::setEnableBoatTrail)
				.build()
		);
		misc.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.swimPop")
						,config.getSwimPopLevel()
						,0
						,2)
				.setDefaultValue(2)
				.setTextGetter(this::getAllMobOrPlayerOnlyText)
				.setSaveConsumer(config::setSwimPopLevel)
				.build()
		);
		misc.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.snowDust")
						,config.getSnowDustLevel()
						,0
						,2)
				.setDefaultValue(2)
				.setTextGetter(this::getAllMobOrPlayerOnlyText)
				.setSaveConsumer(config::setSnowDustLevel)
				.build()
		);
		misc.addEntry(entryBuilder
				.startIntSlider(Text.translatable("text.footprintparticle.option.waterSplash")
						,config.getWaterSplashLevel()
						,0
						,2)
				.setDefaultValue(1)
				.setTextGetter(this::getAllMobOrPlayerOnlyText)
				.setSaveConsumer(config::setWaterSplashLevel)
				.build()
		);
	}

	private Text getAllMobOrPlayerOnlyText(int value) {
		switch (value) {
			case 0 -> {return Text.translatable("text.footprintparticle.disabled");}
			case 1 -> {return Text.translatable("text.footprintparticle.playerOnly");}
			case 2 -> {return Text.translatable("text.footprintparticle.all");}
			default -> {return Text.of("");}
		}
	}
}

package rimo.footprintparticle.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import rimo.footprintparticle.FPPClient;

public class ConfigScreen {
	ConfigBuilder builder = ConfigBuilder.create()
    		.setParentScreen(MinecraftClient.getInstance().currentScreen)
    		.setTitle(Text.translatable("text.footprintparticle.option.title"));
	ConfigEntryBuilder entryBuilder = builder.entryBuilder();

    ConfigCategory general = builder.getOrCreateCategory(Text.translatable("text.footprintparticle.option.title"));

	FPPConfig config = FPPClient.CONFIGHOLDER.getConfig();

	public Screen buildScreen() {
    	buildCategory();

    	// Saving...
    	builder.setSavingRunnable(() -> FPPClient.CONFIGHOLDER.save());

    	return builder.build();
	}

	private void buildCategory() {
		general.addEntry(entryBuilder
				.startBooleanToggle(Text.translatable("text.footprintparticle.option.enableMod")
						,config.isEnable())
				.setDefaultValue(true)
				.setSaveConsumer(config::setEnableMod)
				.build());
		general.addEntry(entryBuilder
				.startFloatField(Text.translatable("text.footprintparticle.option.secPerPrint")
						,config.getSecPerPrint())
				.setDefaultValue(0.5f)
				.setSaveConsumer(config::setSecPerPrint)
				.build());
		general.addEntry(entryBuilder
				.startFloatField(Text.translatable("text.footprintparticle.option.printLifetime")
						,config.getPrintLifetime())
				.setDefaultValue(5.0f)
				.setSaveConsumer(config::setPrintLifetime)
				.build());
		general.addEntry(entryBuilder
				.startFloatField(Text.translatable("text.footprintparticle.option.printHeight")
						,config.getPrintHeight())
				.setTooltip(Text.translatable("text.footprintparticle.option.printHeight.@Tooltip"))
				.setDefaultValue(0f)
				.setSaveConsumer(config::setPrintHeight)
				.build());
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.applyBlocks")
						,config.getApplyBlocks())
				.setDefaultValue(FPPConfig.DEF_APPLYBLOCKS)
				.setTooltip(Text.translatable("text.footprintparticle.option.applyBlocks.@Tooltip"))
				.setSaveConsumer(config::setApplyBlocks)
				.build());
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.blockHeight")
						,config.getBlockHeight())
				.setDefaultValue(FPPConfig.DEF_BLOCKHEIGHT)
				.setTooltip(Text.translatable("text.footprintparticle.option.blockHeight.@Tooltip"))
				.setSaveConsumer(config::setBlockHeight)
				.build());
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.excludedBlocks")
						,config.getExcludedBlocks())
				.setDefaultValue(FPPConfig.DEF_EXCLUDEDBLOCKS)
				.setTooltip(Text.translatable("text.footprintparticle.option.excludedBlocks.@Tooltip"))
				.setSaveConsumer(config::setExcludedBlocks)
				.build());
		general.addEntry(entryBuilder
				.startBooleanToggle(Text.translatable("text.footprintparticle.option.canGenWhenInvisible")
						,config.getCanGenWhenInvisible())
				.setDefaultValue(true)
				.setSaveConsumer(config::setCanGenWhenInvisible)
				.build());
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.excludedMobs")
						,config.getExcludedMobs())
				.setDefaultValue(FPPConfig.DEF_MODS)
				.setTooltip(Text.translatable("text.footprintparticle.option.excludedMobs.@Tooltip"))
				.setSaveConsumer(config::setExcludedMobs)
				.build());
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.sizePerMob")
						,config.getSizePerMob())
				.setDefaultValue(FPPConfig.DEF_SIZE)
				.setTooltip(Text.translatable("text.footprintparticle.option.sizePerMob.@Tooltip"))
				.setSaveConsumer(config::setSizePerMob)
				.build());
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.horseLikeMobs")
						,config.getHorseLikeMobs())
				.setDefaultValue(FPPConfig.DEF_FOUR_LEGS)
				.setTooltip(Text.translatable("text.footprintparticle.option.horseLikeMobs.@Tooltip"))
				.setSaveConsumer(config::setHorseLikeMobs)
				.build());
		general.addEntry(entryBuilder
				.startStrList(Text.translatable("text.footprintparticle.option.spiderLikeMobs")
						,config.getSpiderLikeMobs())
				.setDefaultValue(FPPConfig.DEF_EIGHT_LEGS)
				.setTooltip(Text.translatable("text.footprintparticle.option.spiderLikeMobs.@Tooltip"))
				.setSaveConsumer(config::setSpiderLikeMobs)
				.build());
	}

}

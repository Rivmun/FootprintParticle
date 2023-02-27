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
    					, true)
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
    			.startStrList(Text.translatable("text.footprintparticle.option.applyBlocks")
    					,config.getApplyBlocks())
    			.setDefaultValue(FPPConfig.DEF_BLOCKS)
    			.setTooltip(Text.translatable("text.footprintparticle.option.applyBlocks.@Tooltip"))
    			.setSaveConsumer(config::setApplyBlocks)
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
    }

}

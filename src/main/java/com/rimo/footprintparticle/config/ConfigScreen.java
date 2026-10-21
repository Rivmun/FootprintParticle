package com.rimo.footprintparticle.config;

import com.rimo.footprintparticle.Client;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//? if < 1.19.2
//import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.Optional;

import static com.rimo.footprintparticle.Client.CONFIG;

/**
 * Manually construct vanilla screen across multiple version will break the compatibility for multi-version.
 *  Especially 26.1 subproject for 26.x.<br>
 * So we no longer provide a MissingClothScreen.
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
		buildFilterCategory(builder.getOrCreateCategory(t("category.filter")));
		buildCustomCategory(builder.getOrCreateCategory(t("category.custom")));
		buildMiscCategory(builder.getOrCreateCategory(t("category.misc")));

		// Saving...
		builder.setSavingRunnable(Client.CONFIG::save);

		return builder.build();
	}

	private void buildCategory(ConfigCategory category) {
		category.addEntry(entryBuilder
				.startEnumSelector(t("option.enableMod")
						, WorkMode.class
						, CONFIG.getEnableMod())
				.setDefaultValue(Config.DEF_ENABLE_MOD)
				.setEnumNameProvider(value -> t(value.toString()))
				.setSaveConsumer(CONFIG::setEnableMod)
				.build()
		);
		category.addEntry(entryBuilder
				.startIntSlider(t("option.wetDuration")
						, CONFIG.getWetDuration()
						,0
						,30)
				.setDefaultValue(Config.DEF_WET_DURATION)
				.setTextGetter(value -> {
					if (value == 0) {
						return t("disabled");
					} else {
						return t("second", value);
					}
				})
				.setTooltip(t("option.wetDuration.@Tooltip"))
				.setSaveConsumer(CONFIG::setWetDuration)
				.build()
		);
		category.addEntry(entryBuilder
				.startIntSlider(t("option.genDistChunk")
						, (int) Math.sqrt(CONFIG.getGenDistSqr() / 256F)
						,1
						,16)
				.setDefaultValue(Config.DEF_GEN_DIST_CHUNK)
				.setTooltip(t("option.genDistChunk.@Tooltip"))
				.setSaveConsumer(CONFIG::setGenDistChunk)
				.build()
		);
		category.addEntry(entryBuilder
				.startIntSlider(t("option.secPerPrint")
						,(int) (CONFIG.getSecPerPrint() * 10)
						,0
						,20)
				.setDefaultValue(Config.DEF_SEC_PER_PRINT)
				.setTextGetter(value -> {
					if (value == 0) {
						return p("¿");
					} else {
						return t("second", value / 10F);
					}
				})
				.setSaveConsumer(CONFIG::setSecPerPrint)
				.build()
		);
		category.addEntry(entryBuilder
				.startFloatField(t("option.printLifetime")
						, CONFIG.getPrintLifetime())
				.setDefaultValue(Config.DEF_PRINT_LIFETIME)
				.setSaveConsumer(CONFIG::setPrintLifetime)
				.build()
		);
		category.addEntry(entryBuilder
				.startFloatField(t("option.watermarkLifetime")
						, CONFIG.getWatermarkLifetime())
				.setDefaultValue(Config.DEF_WATERMARK_LIFETIME)
				.setSaveConsumer(CONFIG::setWatermarkLifetime)
				.build()
		);
		category.addEntry(entryBuilder
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
		category.addEntry(entryBuilder
				.startIntSlider(t("option.footprintAlpha")
						,(int) (CONFIG.getFootprintAlpha() * 10)
						,1
						,10)
				.setDefaultValue(Config.DEF_FOOTPRINT_ALPHA)
				.setTextGetter(value -> p(value * 10 + "%"))
				.setSaveConsumer(CONFIG::setFootprintAlpha)
				.build()
		);
		category.addEntry(entryBuilder
				.startIntSlider(t("option.watermarkAlpha")
						,(int) (CONFIG.getWatermarkAlpha() * 10)
						,1
						,10)
				.setDefaultValue(Config.DEF_WATERMARK_ALPHA)
				.setTextGetter(value -> p(value * 10 + "%"))
				.setSaveConsumer(CONFIG::setWatermarkAlpha)
				.build()
		);
		category.addEntry(entryBuilder
				.startIntSlider(t("option.printHeight")
						,(int) (CONFIG.getPrintHeight() / 0.0625F)
						,-8
						,8)
				.setDefaultValue(Config.DEF_PRINT_HEIGHT)
				.setTextGetter(value -> t("block", value * 0.0625f))
				.setTooltip(t("option.printHeight.@Tooltip"))
				.setSaveConsumer(CONFIG::setPrintHeight)
				.build()
		);
		category.addEntry(entryBuilder
				.startIntSlider(t("option.genHeightGap")
						, (int) (CONFIG.getGenHeightGap() * 10)
						,1
						,10)
				.setDefaultValue(Config.DEF_GEN_HEIGHT_GAP)
				.setTextGetter(value -> t("block", value / 10F))
				.setTooltip(t("option.genHeightGap.@Tooltip"))
				.setSaveConsumer(CONFIG::setGenHeightGap)
				.build()
		);
	}

	private void buildFilterCategory(ConfigCategory category) {
		category.addEntry(entryBuilder
				.startStrList(t("option.excludedMobs")
						, CONFIG.getExcludedMobs())
				.setDefaultValue(Config.DEF_MODS)
				.setTooltip(t("option.excludedMobs.@Tooltip"))
				.setCellErrorSupplier(this::setLineError)
				.setSaveConsumer(CONFIG::setExcludedMobs)
				.build()
		);
		category.addEntry(entryBuilder
				.startBooleanToggle(t("option.canGenWhenInvisible")
						, CONFIG.getCanGenWhenInvisible())
				.setDefaultValue(Config.DEF_CAN_GEN_WHEN_INVISIBLE)
				.setSaveConsumer(CONFIG::setCanGenWhenInvisible)
				.build()
		);
		category.addEntry(entryBuilder
				.startStrList(t("option.applyBlocks")
						, CONFIG.getApplyBlocks())
				.setDefaultValue(Config.DEF_APPLYBLOCKS)
				.setTooltip(t("option.applyBlocks.@Tooltip"))
				.setCellErrorSupplier(this::setLineError)
				.setSaveConsumer(CONFIG::setApplyBlocks)
				.build()
		);
		category.addEntry(entryBuilder
				.startFloatField(t("option.hardnessGate")
						, CONFIG.getHardnessGate())
				.setDefaultValue(Config.DEF_HARDNESS_GATE)
				.setTooltip(t("option.hardnessGate.@Tooltip"))
				.setSaveConsumer(CONFIG::setHardnessGate)
				.build()
		);
		category.addEntry(entryBuilder
				.startStrList(t("option.excludedBlocks")
						, CONFIG.getExcludedBlocks())
				.setDefaultValue(Config.DEF_EXCLUDEDBLOCKS)
				.setTooltip(t("option.excludedBlocks.@Tooltip"))
				.setCellErrorSupplier(this::setLineError)
				.setSaveConsumer(CONFIG::setExcludedBlocks)
				.build()
		);
	}

	private void buildCustomCategory(ConfigCategory category) {
		StringListListEntry blockHeight = entryBuilder
				.startStrList(t("option.blockHeight")
						, CONFIG.getBlockHeight())
				.setDefaultValue(Config.DEF_BLOCKHEIGHT)
				.setTooltip(t("option.blockHeight.@Tooltip"))
				.setSaveConsumer(CONFIG::setBlockHeight)
				.build();
		blockHeight.setCellErrorSupplier(line -> floatLineError(line, blockHeight.getValue()));
		category.addEntry(blockHeight);
		StringListListEntry mobInterval = entryBuilder
				.startStrList(t("option.mobInterval")
						, CONFIG.getMobInterval())
				.setDefaultValue(Config.DEF_MOB_INTERVAL)
				.setTooltip(t("option.mobInterval.@Tooltip"))
				.setSaveConsumer(CONFIG::setMobInterval)
				.build();
		mobInterval.setCellErrorSupplier(line -> floatLineError(line, mobInterval.getValue()));
		category.addEntry(mobInterval);
		StringListListEntry sizePerMob = entryBuilder
				.startStrList(t("option.sizePerMob")
						, CONFIG.getSizePerMob())
				.setDefaultValue(Config.DEF_SIZE)
				.setTooltip(t("option.sizePerMob.@Tooltip"))
				.setSaveConsumer(CONFIG::setSizePerMob)
				.build();
		sizePerMob.setCellErrorSupplier(line -> floatLineError(line, sizePerMob.getValue()));
		category.addEntry(sizePerMob);
		StringListListEntry horseLikeMobs = entryBuilder
				.startStrList(t("option.horseLikeMobs")
						, CONFIG.getHorseLikeMobs())
				.setDefaultValue(Config.DEF_FOUR_LEGS)
				.setTooltip(t("option.horseLikeMobs.@Tooltip"))
				.setSaveConsumer(CONFIG::setHorseLikeMobs)
				.build();
		horseLikeMobs.setCellErrorSupplier(line -> floatLineError(line, horseLikeMobs.getValue()));
		category.addEntry(horseLikeMobs);
		StringListListEntry spiderLikeMobs = entryBuilder
				.startStrList(t("option.spiderLikeMobs")
						, CONFIG.getSpiderLikeMobs())
				.setDefaultValue(Config.DEF_EIGHT_LEGS)
				.setTooltip(t("option.spiderLikeMobs.@Tooltip"))
				.setSaveConsumer(CONFIG::setSpiderLikeMobs)
				.build();
		spiderLikeMobs.setCellErrorSupplier(line -> floatLineError(line, spiderLikeMobs.getValue()));
		category.addEntry(spiderLikeMobs);
		StringListListEntry customPrint = entryBuilder
				.startStrList(t("option.customPrint")
						, CONFIG.getCustomPrint())
				.setDefaultValue(Config.DEF_CUSTOM_PRINT)
				.setTooltip(t("option.customPrint.@Tooltip"))
				.setSaveConsumer(CONFIG::setCustomPrint)
				.build();
		customPrint.setCellErrorSupplier(line -> customPrintLineError(line, customPrint.getValue()));
		category.addEntry(customPrint);
		category.addEntry(entryBuilder
				.startIntField(t("option.footprintSize")
						, CONFIG.getFootprintSize())
				.setDefaultValue(Config.DEF_FOOTPRINT_SIZE)
				.setTooltip(t("option.footprintSize.@Tooltip"))
				.setSaveConsumer(CONFIG::setFootprintSize)
				.build()
		);
	}

	private void buildMiscCategory(ConfigCategory category) {
		category.addEntry(entryBuilder
				.startIntSlider(t("option.railFlameRange")
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
		category.addEntry(entryBuilder
				.startBooleanToggle(t("option.enableBoatTrail")
						, CONFIG.isEnableBoatTrail())
				.setDefaultValue(Config.DEF_ENABLE_BOAT_TRAIL)
				.setSaveConsumer(CONFIG::setEnableBoatTrail)
				.build()
		);
		category.addEntry(entryBuilder
				.startEnumSelector(t("option.swimPopLevel")
						, WorkMode.class
						, CONFIG.getSwimPopLevel())
				.setDefaultValue(Config.DEF_SWIM_POP_LEVEL)
				.setEnumNameProvider(value -> t(value.toString()))
				.setSaveConsumer(CONFIG::setSwimPopLevel)
				.build()
		);
		category.addEntry(entryBuilder
				.startEnumSelector(t("option.snowDustLevel")
						, WorkMode.class
						, CONFIG.getSnowDustLevel())
				.setDefaultValue(Config.DEF_SNOW_DUST_LEVEL)
				.setEnumNameProvider(value -> t(value.toString()))
				.setSaveConsumer(CONFIG::setSnowDustLevel)
				.build()
		);
		category.addEntry(entryBuilder
				.startEnumSelector(t("option.waterSplashLevel")
						, WorkMode.class
						, CONFIG.getWaterSplashLevel())
				.setDefaultValue(Config.DEF_WATER_SPLASH_LEVEL)
				.setEnumNameProvider(value -> t(value.toString()))
				.setSaveConsumer(CONFIG::setWaterSplashLevel)
				.build()
		);
	}

	/** Shown on a float-list cell while its line is blank, malformed or repeats a key used by another line. */
	private Optional<Component> floatLineError(String line, List<String> lines) {
		if (line.trim().isEmpty()) return Optional.of(t("error.empty"));
		String key = floatKey(line);
		if (key == null) return Optional.of(t("error.float"));
		int count = 0;
		for (String other : lines) {
			if (key.equals(floatKey(other))) count++;
		}
		return count > 1 ? Optional.of(t("error.duplicate", key)) : Optional.empty();
	}

	/** @return the key of a well-formed float line, or null if the line is blank or malformed. */
	private static String floatKey(String line) {
		if (line == null || line.trim().isEmpty()) return null;
		int idx = line.indexOf(',');
		String key = idx < 0 ? line : line.substring(0, idx);
		if (key.isEmpty()) return null;
		if (idx >= 0) {
			String raw = line.substring(idx + 1).trim();
			if (!raw.isEmpty() && !isFiniteFloat(raw)) return null;
		}
		return key;
	}

	private static boolean isFiniteFloat(String raw) {
		try {
			return Float.isFinite(Float.parseFloat(raw));
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/** Shown on a customPrint cell while its line is blank, malformed or repeats a key used by another line. */
	private Optional<Component> customPrintLineError(String line, List<String> lines) {
		if (line.trim().isEmpty()) return Optional.of(t("error.empty"));
		String key = customPrintKey(line);
		if (key == null) return Optional.of(t("error.customPrint"));
		int count = 0;
		for (String other : lines) {
			if (key.equals(customPrintKey(other))) count++;
		}
		return count > 1 ? Optional.of(t("error.duplicate", key)) : Optional.empty();
	}

	/** @return the key of a well-formed customPrint line, or null if the line is blank or malformed. */
	private static String customPrintKey(String line) {
		if (line == null || line.trim().isEmpty()) return null;
		int idx = line.indexOf(',');
		if (idx <= 0) return null;
		for (String name : line.substring(idx + 1).split(",", -1)) {
			if (name.isEmpty()) return null;
		}
		return line.substring(0, idx);
	}

	/** Shown on a set-list cell while its line is blank. */
	private Optional<Component> setLineError(String line) {
		return line.trim().isEmpty() ? Optional.of(t("error.empty")) : Optional.empty();
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

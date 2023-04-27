package com.mactso.hardcorecontrol.config;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hardcorecontrol.Main;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
	public class MyConfig
	{

		private static final Logger LOGGER = LogManager.getLogger();
		public static final Common COMMON;
		public static final ForgeConfigSpec COMMON_SPEC;
		static
		{
			final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
			COMMON_SPEC = specPair.getRight();
			COMMON = specPair.getLeft();
		}

		public static int ghostSeconds;   // how long dead in seconds
		public static int inventoryLossOdds; // odds a given piece of equipment will be lost
		public static int armorLossOdds; // odds a given piece of equipment will be lost
		public static int hotbarLossOdds; // odds a given piece of equipment will be lost
		public static int XpImmunityLevel; // xp level where the death will not be "hardcore"		
		public static boolean defaultSpectatorGenerateChunkSetting;
		
		public static int getGhostSeconds() {
			return ghostSeconds;
		}

		public static long getGhostTicksOnDeath() {
			return (ghostSeconds * 20);
		}

		public static int getXpImmunityLevel() {
			return XpImmunityLevel;
		}
		
		public static int getInventoryLossOdds() {
			return inventoryLossOdds;
		}

		public static int getArmorLossOdds() {
			return armorLossOdds;
		}

		public static int getHotbarLossOdds() {
			return hotbarLossOdds;
		}

		
		public static boolean isDefaultSpectatorGenerateChunkSetting() {
			return defaultSpectatorGenerateChunkSetting;
		}

		public static void setDefaultSpectatorGenerateChunkSetting(boolean defaultSpectatorGenerateChunkSetting) {
			MyConfig.defaultSpectatorGenerateChunkSetting = defaultSpectatorGenerateChunkSetting;
		}

		@SubscribeEvent
		public static void onModConfigEvent(final ModConfigEvent configEvent)
		{
			if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC)
			{
				bakeConfig();
			}
		}

		public static void bakeConfig()
		{
			ghostSeconds  = COMMON.ghostSeconds.get();
			XpImmunityLevel = COMMON.XpImmunityLevel.get();
			inventoryLossOdds = COMMON.inventoryLossOdds.get();
			armorLossOdds = COMMON.armorLossOdds.get();
			hotbarLossOdds = COMMON.hotbarLossOdds.get();
			
			LOGGER.info(Main.MODID + " configuration loaded");
			
		}


		


		public static class Common
		{

			public final IntValue ghostSeconds;
			public final IntValue inventoryLossOdds;
			public final IntValue armorLossOdds;
			public final IntValue hotbarLossOdds;

			public final IntValue XpImmunityLevel;

			
			public Common(ForgeConfigSpec.Builder builder)
			{

				ghostSeconds = builder.comment("how many seconds player will be a ghost 1 to 2 billion, default is 60 seconds.")
						.translation(Main.MODID + ".config." + "ghostSeconds").defineInRange("ghostSeconds", () -> 60, 1, Integer.MAX_VALUE);

				XpImmunityLevel = builder.comment("Xp level which provides immunity to hardcore death effects.  default is 30")
						.translation(Main.MODID + ".config." + "XpImmunityLevel").defineInRange("XpImmunityLevel", () -> 30, 0, Integer.MAX_VALUE);
				
				inventoryLossOdds = builder.comment("Inventoryloss odds 0 to 100.  default is 66%")
						.translation(Main.MODID + ".config." + "inventoryLossOdds ").defineInRange("inventoryLossOdds ", () -> 66, 0, 100);

				armorLossOdds = builder.comment("Inventoryloss odds 0 to 100.  default is 33%")
						.translation(Main.MODID + ".config." + "armorLossOdds").defineInRange("armorLossOdds", () -> 33, 0, 100);

				hotbarLossOdds = builder.comment("Inventoryloss odds 0 to 100.  default is 25%")
						.translation(Main.MODID + ".config." + "hotbarLossOdds").defineInRange("hotbarLossOdds", () -> 25, 0, 100);

				
			}

		}
	
	}


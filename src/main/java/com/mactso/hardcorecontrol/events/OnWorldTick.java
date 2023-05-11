package com.mactso.hardcorecontrol.events;

import com.mactso.hardcorecontrol.Main;
import com.mactso.hardcorecontrol.config.MyConfig;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class OnWorldTick {

	private static int spectatorTime = 0;
	private static boolean spectatorSettingSaved = false;

	// assumes this event only raised for server worlds. TODO verify.
	@SubscribeEvent
	public static void onWorldTickEvent(LevelTickEvent event) {

		if (event.phase == Phase.START)
			return;

		MinecraftServer server = event.level.getServer();

		if (server == null) {
			return;  // server not running yet
		}
		
		GameRules rules = server.getGameRules();
		saveSpectatorSetting(rules);

		// this is always serverlevel
		if (event.level instanceof ServerLevel level) {
			if (spectatorTime > 0) {
				spectatorTime--;
				if (!rules.getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS)) {
					rules.getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(true, server);
				}
			} else {
				if ((rules.getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS))) {
					rules.getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS)
							.set(MyConfig.isDefaultSpectatorGenerateChunkSetting(), server);
				}
			}

		}
	}

	public static void startSpectatorTime() {
		spectatorTime = 50;
	}

	private static void saveSpectatorSetting(GameRules rules) {
		if (!spectatorSettingSaved) {
			MyConfig.setDefaultSpectatorGenerateChunkSetting(rules.getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS));
			spectatorSettingSaved = true;
		}
	}
}

package com.mactso.hardcorecontrol.events;

import java.util.UUID;

import com.mactso.hardcorecontrol.Main;
import com.mactso.hardcorecontrol.config.MyConfig;
import com.mactso.hardcorecontrol.util.Utility;

import managers.DeadPlayerManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.FORGE)
public class PlayerTick {

	static UUID UUID_HARDCORECONTROLSPEED = UUID.fromString("ab570b50-f074-40c6-8389-b65e0fac65e9");
	private static boolean spectatorSettingSaved = false;

	@SubscribeEvent
	public static void handlePlayerTick(PlayerTickEvent event) {

		if (!(event.player instanceof ServerPlayer)) {
			return;
		}

		ServerPlayer serverplayer = (ServerPlayer) event.player;
		Level level = serverplayer.level;
		long gametime = level.getGameTime();
		
		MinecraftServer server = level.getServer();
		if (!server.isHardcore()) {
			return;
		}

		Stat<ResourceLocation> deaths = Stats.CUSTOM.get(Stats.DEATHS);
		if (serverplayer.getStats().getValue(deaths) == 0) {
			return;
		}

		ServerPlayerGameMode gm = serverplayer.gameMode;
		GameType gt = gm.getGameModeForPlayer();
		if (gt != GameType.SPECTATOR) {
			return;
		}

		Stat<ResourceLocation> timeSinceDead = Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH);
		int ghostTicksCounter = serverplayer.getStats().getValue(timeSinceDead);
		
		long ghostTicksOnDeath = MyConfig.getGhostTicksOnDeath();
		if (DeadPlayerManager.isExperienceRecord(serverplayer)) {
			if (DeadPlayerManager.getExperienceValue(serverplayer) > MyConfig.getXpImmunityLevel()) {
				ghostTicksOnDeath = 10;
			}
		}
		
		if (ghostTicksCounter < ghostTicksOnDeath) {
			BlockPos surfacePos = level.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, serverplayer.blockPosition());
			BlockPos spawnpos = serverplayer.getRespawnPosition();
			if (spawnpos == null) {
				spawnpos = new BlockPos(level.getLevelData().getXSpawn(), level.getLevelData().getYSpawn(),
						level.getLevelData().getZSpawn());
				if (surfacePos.getY() < spawnpos.getY()) { 
					spawnpos = new BlockPos(level.getLevelData().getXSpawn(), surfacePos.getY()+1,
							level.getLevelData().getZSpawn());
				}
			}

			GameRules rules = server.getGameRules();

			// TODO add config / main startup to save default value for this rule.
			
			if (!spectatorSettingSaved) {
				MyConfig.setDefaultSpectatorGenerateChunkSetting(rules.getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS));
				spectatorSettingSaved = true;
			}
			if ((ghostTicksCounter <10) && (!rules.getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS))) {
				rules.getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(true, server);
			} else if ((ghostTicksCounter >10) && (rules.getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS))) {
				rules.getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(MyConfig.isDefaultSpectatorGenerateChunkSetting(), server);
			}
	

			int distance = serverplayer.blockPosition().distManhattan(spawnpos) + 
					Mth.abs( serverplayer.blockPosition().getY() - spawnpos.getY());
			if (distance > 2) {
				serverplayer.setDeltaMovement(serverplayer.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
				((ServerPlayer)serverplayer).teleportTo(spawnpos.getX(),spawnpos.getY(),spawnpos.getZ());
	            
			}

			ChatFormatting color = ChatFormatting.AQUA;
			if (gametime%180 > 60) {
				color = ChatFormatting.LIGHT_PURPLE;
			}
			if (gametime%180 > 120) {
				color = ChatFormatting.WHITE;
			}

			Utility.sendClientMessage(serverplayer,
						"You will be a ghost for " + calcDeathSeconds(ghostTicksOnDeath, ghostTicksCounter) + " more seconds.", color);
			return;
		}
		DeadPlayerManager.removePlayerXpRecord(serverplayer);
// TODO get working sound code from HarderFarther
		level.playSound(null, serverplayer.blockPosition() , SoundEvents.ANVIL_LAND, SoundSource.AMBIENT, 0.5f, 0.25f);
		serverplayer.resetStat(Stats.CUSTOM.get(Stats.DEATHS));
		serverplayer.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
		serverplayer.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
		serverplayer.setGameMode(GameType.SURVIVAL);
		// TODO set player hit points to configurable value.
	}

	static int calcDeathSeconds(long ghostTicksOnDeath, int deathTicks) {
		return (int) ((ghostTicksOnDeath - deathTicks) / 20);
	}
}

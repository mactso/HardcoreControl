package com.mactso.hardcorecontrol.events;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import com.mactso.hardcorecontrol.Main;
import com.mactso.hardcorecontrol.config.MyConfig;
import com.mactso.hardcorecontrol.managers.DeadPlayerManager;
import com.mactso.hardcorecontrol.timer.CapabilityDeathTime;
import com.mactso.hardcorecontrol.timer.IDeathTime;
import com.mactso.hardcorecontrol.util.Utility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.storage.LevelData;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.FORGE)
public class PlayerTick {

	static UUID UUID_HARDCORECONTROLSPEED = UUID.fromString("ab570b50-f074-40c6-8389-b65e0fac65e9");


	@SubscribeEvent
	public static void handlePlayerTick(PlayerTickEvent event) {

		if (!(event.player instanceof ServerPlayer)) {
			return;
		}

		ServerPlayer serverplayer = (ServerPlayer) event.player;

		IDeathTime dt = serverplayer.getCapability(CapabilityDeathTime.DEATH_TIME).orElse(null);
		if (dt == null) 
			return;
		
		
		LocalDateTime deathTime = dt.getDeadTime();
		if (deathTime == null) 
			return;

		LocalDateTime reviveTime = deathTime.plusSeconds(MyConfig.getGhostSeconds());

		if (DeadPlayerManager.isExperienceRecord(serverplayer)) {
			if (DeadPlayerManager.getExperienceValue(serverplayer) >= MyConfig.getXpImmunityLevel()) {
				reviveTime = deathTime.plusSeconds(1);
			}
		}
		
		LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
		
		if ((currentTime.isBefore(reviveTime))) {
			doPlayerGhostMode(serverplayer,  currentTime, reviveTime);
			return;
		}  
			
		doPlayerRevive(serverplayer);
		
	}

	private static void doPlayerGhostMode(ServerPlayer serverplayer,
			LocalDateTime currentTime, LocalDateTime reviveTime) {
		serverplayer.setGameMode(GameType.SPECTATOR);

		Level level = serverplayer.level;
		MinecraftServer server = level.getServer();		
        ServerLevel serverlevel = server.getLevel(serverplayer.getRespawnDimension());
		long gametime = level.getGameTime();

		BlockPos surfacePos = level.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, serverplayer.blockPosition());
		BlockPos spawnpos = serverplayer.getRespawnPosition();

		if (spawnpos == null) {
			LevelData ld = level.getLevelData();
			spawnpos = new BlockPos(ld.getXSpawn(), ld.getYSpawn(),ld.getZSpawn());
		}

		HandleWorldTick.startSpectatorTime();

		int distance = serverplayer.blockPosition().distManhattan(spawnpos) + 
				Mth.abs( serverplayer.blockPosition().getY() - spawnpos.getY());
		if (distance > 2) {
			serverplayer.setDeltaMovement(serverplayer.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
			((ServerPlayer)serverplayer).teleportTo(spawnpos.getX()+0.5D,spawnpos.getY()+0.1D,spawnpos.getZ()+0.5D);
		    
		}


		if (gametime%20 == 0) {
			ChatFormatting color = ChatFormatting.AQUA;
			if (gametime%180 > 60) {
				color = ChatFormatting.LIGHT_PURPLE;
			}
			if (gametime%180 > 120) {
				color = ChatFormatting.WHITE;
			}

			long secondsR = reviveTime.toEpochSecond(ZoneOffset.UTC);
			long secondsC = currentTime.toEpochSecond(ZoneOffset.UTC);
			long netSeconds = secondsR-secondsC;
			Utility.sendClientMessage(serverplayer,
					"You will be a ghost for " + netSeconds + " more seconds.", color);
		}

	}

	private static void doPlayerRevive(ServerPlayer serverplayer) {

		Level level = serverplayer.level;
		MinecraftServer server = level.getServer();		

		DeadPlayerManager.removePlayerXpRecord(serverplayer);
		
		IDeathTime dt = serverplayer.getCapability(CapabilityDeathTime.DEATH_TIME).orElse(null);
		if (dt != null) {
		    dt.setDeathTime(null);
		}
		
		level.playSound(null, serverplayer.blockPosition() , SoundEvents.ANVIL_LAND, SoundSource.AMBIENT, 0.5f, 0.25f);

		if (server.getDefaultGameType() == GameType.ADVENTURE) {
			serverplayer.setGameMode(GameType.ADVENTURE);
		} else {
			serverplayer.setGameMode(GameType.SURVIVAL);
		}

		if (!server.isHardcore()) {
			return;
		}
		
		serverplayer.resetStat(Stats.CUSTOM.get(Stats.DEATHS));
		serverplayer.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
		int playtime = serverplayer.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
		serverplayer.getStats().setValue(serverplayer, Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH), playtime);
	}


	static int calcDeathSeconds(long ghostTicksOnDeath, int deathTicks) {
		return (int) ((ghostTicksOnDeath - deathTicks) / 20);
	}
}

package com.mactso.hardcorecontrol.events;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.FORGE)
public class OnPlayerTick {

	static UUID UUID_HARDCORECONTROLSPEED = UUID.fromString("ab570b50-f074-40c6-8389-b65e0fac65e9");

	static Map<ServerPlayer, Vec3> actualRespawnPos = new HashMap<>() ;
	
	@SubscribeEvent
	public static void handlePlayerTick(PlayerTickEvent event) {

		if (!(event.player instanceof ServerPlayer)) {
			return;
		}


		ServerPlayer serverplayer = (ServerPlayer) event.player;

		if (!serverplayer.isAlive()) {
			return;
		}
		
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

		if (serverplayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE) {
			reviveTime = currentTime;
		}
		
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

		if (!actualRespawnPos.containsKey(serverplayer) ) {
			actualRespawnPos.put(serverplayer, serverplayer.getPosition(0));
		}
		
		Vec3 spawnpos = actualRespawnPos.get(serverplayer);
		
		OnWorldTick.startSpectatorTime();

		if ((spawnpos.y > serverplayer.getPosition(0).y+0.1) ||
		    (spawnpos.distanceTo(serverplayer.getPosition(0)) > 2.0))  {
			serverplayer.setDeltaMovement(serverplayer.getDeltaMovement().multiply(0.0D, 0.0D, 0.0D));
			((ServerPlayer)serverplayer).teleportTo(spawnpos.x,spawnpos.y,spawnpos.z);
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
		
		if (actualRespawnPos.containsKey(serverplayer) ) {
			Vec3 spawnpos = actualRespawnPos.get(serverplayer);
			actualRespawnPos.remove(serverplayer);
			serverplayer.teleportTo(spawnpos.x,spawnpos.y,spawnpos.z);
		}
		
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
	
	public static void clearMapOnServerStop () {
		actualRespawnPos.clear();
	}
}

package com.mactso.hardcorecontrol.events;

import java.time.LocalDateTime;

import com.mactso.hardcorecontrol.Main;
import com.mactso.hardcorecontrol.timer.CapabilityDeathTime;
import com.mactso.hardcorecontrol.timer.IDeathTime;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.FORGE)
public class OnPlayerCloned {
    @SubscribeEvent
    public static void onPlayerCloned(Clone event)
    {

		if (!(event.getEntity() instanceof ServerPlayer)) {
			return;
		}
	
		if (!event.isWasDeath()) {
			return;
		}

		ServerPlayer newPlayer = (ServerPlayer) event.getEntity();
		ServerPlayer oldPlayer = (ServerPlayer) event.getOriginal();
		oldPlayer.reviveCaps();
		IDeathTime capNew = newPlayer.getCapability(CapabilityDeathTime.DEATH_TIME).orElse(null);
		IDeathTime capOld = oldPlayer.getCapability(CapabilityDeathTime.DEATH_TIME).orElse(null);
		LocalDateTime ldt = capOld.getDeadTime();
		capNew.setDeathTime(ldt);
		oldPlayer.invalidateCaps();

    }
}
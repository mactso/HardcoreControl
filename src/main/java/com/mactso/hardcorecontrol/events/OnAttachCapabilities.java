package com.mactso.hardcorecontrol.events;

import com.mactso.hardcorecontrol.Main;
import com.mactso.hardcorecontrol.timer.CapabilityDeathTime;
import com.mactso.hardcorecontrol.timer.DeathTimeProvider;
import com.mactso.hardcorecontrol.timer.IDeathTime;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.FORGE)
public class OnAttachCapabilities {
	private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(Main.MODID, "deadtime_capability");

	 @SubscribeEvent
	 public static void onPlayer(AttachCapabilitiesEvent <Entity> event)
	 {
		 ServerPlayer serverPlayerEntity;
		 if (event.getObject() instanceof ServerPlayer) {
			 serverPlayerEntity = (ServerPlayer) event.getObject();
		 } else {
			 return;
		 }
		 LazyOptional<IDeathTime> optPlayer = serverPlayerEntity.getCapability(CapabilityDeathTime.DEATH_TIME);
		 if (optPlayer.isPresent()) {
			 System.out.println ("HardCore Control Utility : Player already has death time capability and this should never happen.");
			 return;
		 }
		 else {
			 event.addCapability(KEY, new DeathTimeProvider(serverPlayerEntity));
		 }
	 }
}


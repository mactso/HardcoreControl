package com.mactso.hardcorecontrol.events;

import com.mactso.hardcorecontrol.Main;

import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class OnServerStopping {

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		OnPlayerTick.clearMapOnServerStop();
	}
}

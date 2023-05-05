package com.mactso.hardcorecontrol.timer;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CapabilityDeathTime
{
    public static final Capability<IDeathTime> DEATH_TIME = CapabilityManager.get(new CapabilityToken<>(){});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event)
    {
    	event.register(IDeathTime.class);
    }
}

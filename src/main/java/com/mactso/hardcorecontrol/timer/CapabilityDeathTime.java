package com.mactso.hardcorecontrol.timer;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

// @Mod.EventBusSubscriber(bus = Bus.MOD)
public class CapabilityDeathTime
{
    public static final Capability<IDeathTime> DEATH_TIME = CapabilityManager.get(new CapabilityToken<>(){});

//    @SubscribeEvent
//    public static void register(RegisterCapabilitiesEvent event)
//    {
//    	event.register(IDeathTime.class);
//    }
}

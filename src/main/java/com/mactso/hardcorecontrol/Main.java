package com.mactso.hardcorecontrol;

import com.mactso.hardcorecontrol.config.MyConfig;

import managers.DeadPlayerManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod("hardcorecontrol")
public class Main {

	    public static final String MODID = "hardcorecontrol"; 
	    private static final Logger LOGGER = LogManager.getLogger();
	    
	    public Main()
	    {
			ModLoadingContext.get().registerExtensionPoint(DisplayTest.class,
					() -> new DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
	    	System.out.println(MODID + ": Registering Mod.");
 	        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,MyConfig.COMMON_SPEC );
 	        
	    }
	    
	    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	    public static class CommonEvents {
	        @SubscribeEvent
	        public static void onServerStarting(FMLCommonSetupEvent event) {
	            // Code to handle the event
	            LOGGER.info("Hardcore Control starting");
	            DeadPlayerManager.init();
	        }
	    }

}

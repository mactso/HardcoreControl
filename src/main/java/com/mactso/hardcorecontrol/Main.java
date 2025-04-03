package com.mactso.hardcorecontrol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hardcorecontrol.commands.MyCommands;
import com.mactso.hardcorecontrol.config.MyConfig;
import com.mactso.hardcorecontrol.managers.DeadPlayerManager;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;


@Mod("hardcorecontrol")
public class Main {

	    public static final String MODID = "hardcorecontrol"; 
	    private static final Logger LOGGER = LogManager.getLogger();
	    
	    public Main()
	    {
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
	    
	    @Mod.EventBusSubscriber()
	    public static class ForgeEvents
	    {
			@SubscribeEvent 		
			public static void onCommandsRegistry(final RegisterCommandsEvent event) {
				System.out.println("Happy Trails: Registering Command Dispatcher");
				MyCommands.register(event.getDispatcher());			
			}

	    }

}

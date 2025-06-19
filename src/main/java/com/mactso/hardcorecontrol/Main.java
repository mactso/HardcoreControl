package com.mactso.hardcorecontrol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.hardcorecontrol.commands.MyCommands;
import com.mactso.hardcorecontrol.config.MyConfig;
import com.mactso.hardcorecontrol.managers.DeadPlayerManager;
import com.mactso.hardcorecontrol.util.Utility;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod("hardcorecontrol")
public class Main {

	    public static final String MODID = "hardcorecontrol"; 
	    private static final Logger LOGGER = LogManager.getLogger();
	    
		public Main(FMLJavaModLoadingContext context) {
			context.getModEventBus().register(this);
			context.registerConfig(ModConfig.Type.COMMON, MyConfig.COMMON_SPEC);
			Utility.debugMsg(0, MODID + ": Registering Mod"); 	        
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

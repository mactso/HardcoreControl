package com.mactso.hardcorecontrol.events;

import com.mactso.hardcorecontrol.Main;
import com.mactso.hardcorecontrol.config.MyConfig;

import managers.DeadPlayerManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.FORGE)
public class PlayerDeath {

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {

		if (!(event.getEntity() instanceof ServerPlayer)) {
			return;
		}

		ServerPlayer sp = (ServerPlayer) event.getEntity();

		if (!sp.level.getServer().isHardcore()) {
			return;
		}

		// TODO persist this to disk at some point.
		
		// save experience level when player died.
		if (DeadPlayerManager.getExperienceRecord(sp) == null) {
			DeadPlayerManager.addExperienceRecord(sp);
		}

		if ((MyConfig.getInventoryLossOdds() == 0) && (MyConfig.getArmorLossOdds() == 0)
				&& (MyConfig.getHotbarLossOdds() == 0)) {
			return;
		}

		Inventory inventory = sp.getInventory();

		RandomSource rand = sp.level.getRandom();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			System.out.println(i);
			if (isArmorSlot(i)) {
				if (rand.nextInt(100) <= MyConfig.getArmorLossOdds()) {
					System.out.println("Under armor odds: "+ MyConfig.getArmorLossOdds() +",removing slot " + i);
					inventory.setItem(i, ItemStack.EMPTY);
				}
			} else if (isHotbarSlot(i)) {
				if (rand.nextInt(100) <= MyConfig.getHotbarLossOdds()) {
					System.out.println("Under hotbar odds: "+ MyConfig.getHotbarLossOdds() +",removing slot " + i);
					inventory.setItem(i, ItemStack.EMPTY);
				}
			} else {
				if (rand.nextInt(100) <= MyConfig.getInventoryLossOdds()) {
					System.out.println("Under generalinventory odds: "+ MyConfig.getInventoryLossOdds() +",removing slot " + i);
					System.out.println("removing slot " + i);
					inventory.setItem(i, ItemStack.EMPTY);
				}
			}
		}
	}

	// replace inventory.isHotbarSlot so code will be symmetrical visually
	private static boolean isHotbarSlot(int i) {
		if ((i >= 0) && (i <= 9)) {
			return true;
		}
		return false;
	}

	private static boolean isArmorSlot(int i) {
		if ((i >= 36) && (i <= 39)) {
			return true;
		}
		return false;
	}

}

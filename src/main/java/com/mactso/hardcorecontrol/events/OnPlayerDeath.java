package com.mactso.hardcorecontrol.events;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.mactso.hardcorecontrol.Main;
import com.mactso.hardcorecontrol.commands.MyCommands;
import com.mactso.hardcorecontrol.config.MyConfig;
import com.mactso.hardcorecontrol.managers.DeadPlayerManager;
import com.mactso.hardcorecontrol.timer.CapabilityDeathTime;
import com.mactso.hardcorecontrol.timer.IDeathTime;
import com.mactso.hardcorecontrol.util.Utility;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.FORGE)
public class OnPlayerDeath {

	@SubscribeEvent
	public void onPlayerDeath(PlayerEvent.Clone event) {


		if (!(event.getEntity() instanceof ServerPlayer)) {
			return;
		}

		ServerPlayer sp = (ServerPlayer) event.getEntity();
		Inventory originalPlayerInventory = event.getOriginal().getInventory();
		
		Inventory inventory = sp.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (isArmorSlot(i) && isKeepInventory(MyConfig.getArmorLossOdds())) {
				sp.getInventory().setItem(i, originalPlayerInventory.getItem(i)); // restore 
			} else if (isShieldSlot(i) && isKeepInventory(MyConfig.getArmorLossOdds())) {
				sp.getInventory().setItem(i, originalPlayerInventory.getItem(i)); // restore 
			} else if (Inventory.isHotbarSlot(i) && isKeepInventory(MyConfig.getHotbarLossOdds())) {
				sp.getInventory().setItem(i, originalPlayerInventory.getItem(i)); // restore 
			} else if (isGeneralInventorySlot(i) && isKeepInventory(MyConfig.getInventoryLossOdds())) {
				sp.getInventory().setItem(i, originalPlayerInventory.getItem(i)); // restore 
			} else {
			}
		}
		
		// MyCommands.doInfoReport(sp);

	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {

		if (!(event.getEntity() instanceof ServerPlayer)) {
			return;
		}

		ServerPlayer sp = (ServerPlayer) event.getEntity();

		// save experience level when player died.
		if (DeadPlayerManager.getExperienceRecord(sp) == null) {
			DeadPlayerManager.addExperienceRecord(sp);
		}

		IDeathTime dt = sp.getCapability(CapabilityDeathTime.DEATH_TIME).orElse(null);
		if (dt != null) {
			LocalDateTime ldt = LocalDateTime.now(ZoneOffset.UTC);
			dt.setDeathTime(ldt);
		}

		if ((MyConfig.getInventoryLossOdds() == 0) && (MyConfig.getArmorLossOdds() == 0)
				&& (MyConfig.getHotbarLossOdds() == 0)) {
			return;
		}

		if (DeadPlayerManager.getExperienceValue(sp) >= MyConfig.getXpImmunityLevel()) {
			return;
		}

		Inventory inventory = sp.getInventory();

		RandomSource rand = sp.level().getRandom();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (isArmorSlot(i)) {
				if (rand.nextInt(100) <= MyConfig.getArmorLossOdds()) {
					inventory.setItem(i, ItemStack.EMPTY);
				}
			} else if (isShieldSlot(i)) {
				if (rand.nextInt(100) <= MyConfig.getArmorLossOdds()) {
					inventory.setItem(i, ItemStack.EMPTY);
				}
			} else if (Inventory.isHotbarSlot(i)) {
				if (rand.nextInt(100) <= MyConfig.getHotbarLossOdds()) {
					inventory.setItem(i, ItemStack.EMPTY);
				}
			} else if (isGeneralInventorySlot(i)) {
				if (rand.nextInt(100) <= MyConfig.getInventoryLossOdds()) {
					inventory.setItem(i, ItemStack.EMPTY);
				}
			} else {

			}
		}
	}

	
	private static boolean isKeepInventory(int odds) {

		if (odds == MyConfig.KEEP_ITEM) return true;
		return false;
	
	}
	
	
	private static boolean isArmorSlot(int i) {

		if ((i >= 36) && (i <= 39)) {
			return true;
		}
		return false;

	}


	private static boolean isShieldSlot(int i) {
		
		if (i == Inventory.SLOT_OFFHAND) {
			return true;
		}
		return false;
	}


	private static boolean isGeneralInventorySlot(int i) {
	
		if ((i >= 9) && (i <= 35)) {
			return true;
		}
		return false;
	}

}

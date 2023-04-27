package com.mactso.hardcorecontrol.commands;

import com.mactso.hardcorecontrol.Main;
import com.mactso.hardcorecontrol.config.MyConfig;
import com.mactso.hardcorecontrol.util.Utility;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class MyCommands {
	String subcommand = "";
	String value = "";

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal(Main.MODID)  
		.then(Commands.literal("info")
				.then(Commands.literal("info").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					doInfoReport(p);
					return 1;
				}))));

	}

	private static void doInfoReport(ServerPlayer p) {
		String chatMessage = "  Harecore Control Info Report";  
		Utility.sendChat((Player) p, chatMessage, ChatFormatting.GREEN);
		chatMessage = 
				  "\n  Ghost Seconds ...........................: " + MyConfig.getGhostSeconds()
				+ "\n  Immunity XP ...........................: " + MyConfig.getXpImmunityLevel()
				+ "\n  Armor Loss Odds...............: " + MyConfig.getArmorLossOdds()
				+ "\n  Inventory Loss Odds......: " + MyConfig.getInventoryLossOdds()
				+ "\n  HotBar Loss Odds...... " + MyConfig.getHotbarLossOdds()
				;
		Utility.sendChat((Player) p, chatMessage, ChatFormatting.AQUA);
	}

}

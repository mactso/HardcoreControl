package com.mactso.hardcorecontrol.commands;

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
		dispatcher.register(Commands.literal("hardcorecontrol").requires((source) -> {
			return source.hasPermission(0);
		}).then(Commands.literal("info").executes(ctx -> {
			ServerPlayer p = ctx.getSource().getPlayerOrException();
			doInfoReport(p);
			return 1;
		})));

	}

	public static void doInfoReport(ServerPlayer p) {

		String chatMessage = "Hardcore Control Info Report";
		Utility.sendChat((Player) p, chatMessage, ChatFormatting.GREEN);

		chatMessage = "\n  Ghost Seconds ....................: " + MyConfig.getGhostSeconds() + " seconds."
				+ "\n  Immunity XP .............................: " + MyConfig.getXpImmunityLevel() + " levels."
				+ "\n  Armor Loss Odds................: " + formatLossOdds(MyConfig.getArmorLossOdds()) 
				+ "\n  Inventory Loss Odds......: " + formatLossOdds(MyConfig.getInventoryLossOdds()) 
				+ "\n  HotBar Loss Odds..............: " + formatLossOdds(MyConfig.getHotbarLossOdds());
		Utility.sendChat((Player) p, chatMessage, ChatFormatting.AQUA);
	}

	private static String formatLossOdds(int odds) {
		String s = "Keep Inventory on Death.";
		if ((odds) >= 0) {
			s = ""+odds+ " %.";
		}
		return s;
	}

}

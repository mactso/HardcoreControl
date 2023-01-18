package com.mactso.hardcorecontrol.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class Utility {
	
	public static void sendBoldChat(Player p, String chatMessage, ChatFormatting textColor) {

		TextComponent component = new TextComponent(chatMessage);
		component.setStyle(component.getStyle().withBold(true));
		component.setStyle(component.getStyle().withColor(textColor));
		p.sendMessage(component, p.getUUID());

	}

	public static void sendChat(Player p, String chatMessage, ChatFormatting textColor) {

		TextComponent component = new TextComponent(chatMessage);
		component.setStyle(component.getStyle().withColor(textColor));
		p.sendMessage(component, p.getUUID());

	}
	
	public static void sendClientMessage (Player p, String chatMessage, ChatFormatting textColor) {
		TextComponent component = new TextComponent(chatMessage);
		component.setStyle(component.getStyle().withColor(textColor));
		p.displayClientMessage(component, true);
	}

}

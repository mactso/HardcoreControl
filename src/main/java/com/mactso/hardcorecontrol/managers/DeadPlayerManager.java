package com.mactso.hardcorecontrol.managers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;

class ExperienceRecord {
	private ServerPlayer player;
	private int experience;

	public ExperienceRecord(ServerPlayer player, int experience) {
		this.player = player;
		this.experience = experience;
	}

	public ServerPlayer getPlayer() {
		return player;
	}

	public int getExperience() {
		return experience;
	}
}

public class DeadPlayerManager {

	private static List<ExperienceRecord> experienceRecords = new ArrayList<>();

	public static void init() {
		experienceRecords.clear();
	}

	public static void addExperienceRecord(ServerPlayer player) {

		experienceRecords.add(new ExperienceRecord(player, player.experienceLevel));
	}

	public static ExperienceRecord getExperienceRecord(ServerPlayer player) {
		for (ExperienceRecord record : experienceRecords) {
			if (record.getPlayer().equals(player)) {
				return record;
			}
		}
		return null;
	}

	public static int getExperienceValue(ServerPlayer player) {
		for (ExperienceRecord record : experienceRecords) {
			if (record.getPlayer().equals(player)) {
				return record.getExperience();
			}
		}
		return 0;
	}
	
	public static boolean isExperienceRecord(ServerPlayer player) {
		for (ExperienceRecord record : experienceRecords) {
			if (record.getPlayer().equals(player)) {
				return true;
			}
		}
		return false;
	}

	public static void removePlayerXpRecord(ServerPlayer player) {
		for (ExperienceRecord record : experienceRecords) {
			if (record.getPlayer().equals(player)) {
				experienceRecords.remove(record);
				return;
			}
		}
	}



}

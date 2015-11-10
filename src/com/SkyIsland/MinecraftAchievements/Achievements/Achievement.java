package com.SkyIsland.MinecraftAchievements.Achievements;

import org.bukkit.event.Listener;

/**
 * An achievement handles figuring out if players have unlocked themselves.<br />
 * They are responsible for updating the player records kept by the player manager and for actually
 * doing the listening and figuring out if the achievement requirements have been met.
 */
public abstract class Achievement implements Listener {

	private String name;
	
	protected Achievement(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}

package com.SkyIsland.MinecraftAchievements.Achievements;

import org.bukkit.event.Listener;

/**
 * Active Achievements actively listen to find out if players have unlocked them.<br />
 * They are responsible for updating the player records kept by the player manager and for actually
 * doing the listening and figuring out if the achievement requirements have been met.
 */
public abstract class ActiveAchievement extends Achievement implements Listener {

	protected ActiveAchievement(String name, String description, int point_value) {
		super(name, description, point_value);
	}
	
}

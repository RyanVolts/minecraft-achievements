package com.SkyIsland.MinecraftAchievements.Achievements;

/**
 * An achievement handles figuring out if players have unlocked themselves.
 */
public abstract class Achievement {

	private String name;
	
	protected Achievement(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}

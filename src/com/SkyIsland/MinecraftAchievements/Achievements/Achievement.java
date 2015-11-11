package com.SkyIsland.MinecraftAchievements.Achievements;

/**
 * An achievement handles figuring out if players have unlocked themselves.
 */
public abstract class Achievement {

	private String name;
	private int point_value;
	private String description;
	
	protected Achievement(String name, String description, int point_value) {
		this.name = name;
		this.description = description;		
		this.point_value = point_value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public int getPoint_Value() {
		return this.point_value;
	}	
}
package com.SkyIsland.MinecraftAchievements.Achievements;

/**
 * Achievement unlocked by a certain period of time passing.
 * @author Skyler
 *
 */
public abstract class TimedAchievement extends PassiveAchievement {

	private int ticks;
	
	protected TimedAchievement(String name, int ticksTillAwarded) {
		super(name);
		ticks = ticksTillAwarded;
	}
	
	public int getTicks() {
		return ticks;
	}

}

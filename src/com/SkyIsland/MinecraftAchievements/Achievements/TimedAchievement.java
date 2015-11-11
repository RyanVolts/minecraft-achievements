package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.UUID;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;
import com.SkyIsland.MinecraftAchievements.Scheduler.Scheduler;
import com.SkyIsland.MinecraftAchievements.Scheduler.Tickable;

/**
 * Achievement unlocked by a certain period of time passing.
 * @author Skyler
 *
 */
public abstract class TimedAchievement extends PassiveAchievement implements Tickable {

	private int time;
	
	protected UUID playerId;
	
	protected TimedAchievement(UUID playerID, String name, String description, int point_value, int secondsTillAwarded) {
		super(name, description, point_value);
		time = secondsTillAwarded;
		this.playerId = playerID;
		
		Scheduler.getScheduler().schedule(this, this, secondsTillAwarded);
	}
	
	public int getTime() {
		return time;
	}
	
	@Override
	public void tick(Object key) {
		//no matter what key we get, just assume it's good and award to players.
		//TODO do error checking
		
		MinecraftAchievementsPlugin.plugin.getPlayerManager().onTimer(playerId, this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TimedAchievement) {
			if (((TimedAchievement) o).getName().equals(getName())) {
				return true;
			}
		}
		
		return false;
	}

}

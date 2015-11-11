package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.UUID;

/**
 * Achievement for the survive events.
 * @author Skyler
 *
 */
public class SurviveAchievement extends TimedAchievement {

	public SurviveAchievement(UUID playerID, String name, String description, int point_value, int secondsTillAwarded) {
		super(playerID, name, description, point_value, secondsTillAwarded);
	}

}

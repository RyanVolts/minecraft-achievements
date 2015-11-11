package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

/**
 * Achievement for not taken damage for a certain period of time
 * @author Skyler
 *
 */
public class NoDamageSelfAchievement extends TimedAchievement {

	public NoDamageSelfAchievement(UUID playerID, String name, String description, int point_value, int secondsTillAwarded) {
		super(playerID, name, description, point_value, secondsTillAwarded);
	}
	
	@Override
	public void tick(Object key) {
		//check if they've damaged someone before awarding
		Player player = Bukkit.getPlayer(this.playerId);
		
		if (player == null) {
			return;
		}
		
		//check their kill stats first
		if (player.getStatistic(Statistic.DAMAGE_TAKEN) == 0) {
			super.tick(key);
		}
	}

}

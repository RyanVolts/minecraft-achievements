package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

/**
 * Achievement for:<br />
 * <ul>
 * <li>Opening at least one chest</li>
 * <li>Not taking any damage</li>
 * <li>Not doing any damage</li>
 * </ul>
 * in a given time.
 * @author Skyler
 *
 */
public class KatnissAchievement extends TimedAchievement {

	public KatnissAchievement(UUID playerID, String name, String description, int point_value, int secondsTillAwarded) {
		super(playerID, name, description, point_value, secondsTillAwarded);
	}
	
	@Override
	public void tick(Object key) {
		//check if they've damaged someone before awarding
		Player player = Bukkit.getPlayer(this.playerId);
		
		if (player == null) {
			return;
		}
		
		//check their damage stats first
		if (player.getStatistic(Statistic.DAMAGE_DEALT) == 0
			&& player.getStatistic(Statistic.DAMAGE_TAKEN) == 0
			&& (player.getStatistic(Statistic.CHEST_OPENED) 
					+ player.getStatistic(Statistic.TRAPPED_CHEST_TRIGGERED)) >= 1) {
			super.tick(key);
		}
	}

}

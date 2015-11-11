package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;

public class OpenChestAchievement extends ActiveAchievement {

	private Map<UUID, Boolean> playerCache; //faster lookups
	
	private int count;
	
	public OpenChestAchievement(String name, String description, int point_value, int chestCount) {
		super(name, description, point_value);
		playerCache = new HashMap<UUID, Boolean>();
		count = chestCount;
	}

	@EventHandler
	public void OnPlayerDeath(PlayerStatisticIncrementEvent e) {
		
		if (e.getStatistic() != Statistic.CHEST_OPENED && e.getStatistic() != Statistic.TRAPPED_CHEST_TRIGGERED) {
			return;
		}
		
		if (!MinecraftAchievementsPlugin.plugin.getPlayerManager().isActive(e.getPlayer())) {
			return;
		}

		if (!playerCache.containsKey(e.getPlayer().getUniqueId())) {
			playerCache.put(e.getPlayer().getUniqueId(), false);
		}
		
		if (playerCache.get(e.getPlayer().getUniqueId()) == false) {
			//if they haven't been awarded & cached, actually check
			int stat = e.getPlayer().getStatistic(Statistic.CHEST_OPENED) 
					+ e.getPlayer().getStatistic(Statistic.TRAPPED_CHEST_TRIGGERED)
					+ 1; //Add one cause the scores haven't been updated yet!
			if (stat >= count) {
				playerCache.put(e.getPlayer().getUniqueId(), true);
				MinecraftAchievementsPlugin.plugin.getPlayerManager().addAchievement(
						e.getPlayer(), this);
			}
		}
	}	

}

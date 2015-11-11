package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;

public class KillPlayerAchievement extends ActiveAchievement {

	private Map<UUID, Boolean> playerCache; //faster lookups
	
	private int killCount;
	
	public KillPlayerAchievement(String name, String description, int points, int killCount) {
		super(name, description, points);
		playerCache = new HashMap<UUID, Boolean>();
		this.killCount = killCount;
	}

	@EventHandler
	public void OnPlayerDeath(PlayerStatisticIncrementEvent e) {
		
		if (e.getStatistic() != Statistic.PLAYER_KILLS) {
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
			int stat = e.getNewValue();
			if (stat >= killCount) {
				playerCache.put(e.getPlayer().getUniqueId(), true);
				MinecraftAchievementsPlugin.plugin.getPlayerManager().addAchievement(
						e.getPlayer(), this);
			}
		}
	}
	
}

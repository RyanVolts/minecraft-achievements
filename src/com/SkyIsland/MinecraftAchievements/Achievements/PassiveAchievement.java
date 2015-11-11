package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;
import com.SkyIsland.MinecraftAchievements.Players.PlayerManager;

/**
 * Passive achievements do not do any listening themselves, and instead wait to be called to be added
 * to players.<br />
 * This works great for timed achievements which work better by having an external scheduler award vs
 * operating on an unreliable delayed task chain.
 */
public abstract class PassiveAchievement extends Achievement implements Listener {

	protected PassiveAchievement(String name, String description, int point_value) {
		super(name, description, point_value);
	}
	
	public void awardToPlayers(Collection<Player> players) {
		PlayerManager manager = MinecraftAchievementsPlugin.plugin.getPlayerManager();
		for (Player player : players) {
			manager.addAchievement(player, getName());
		}
	}

}

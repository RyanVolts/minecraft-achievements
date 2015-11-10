package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
	
	protected TimedAchievement(String name, int secondsTillAwarded) {
		super(name);
		time = secondsTillAwarded;
		
		Scheduler.getScheduler().schedule(this, this, secondsTillAwarded);
	}
	
	public int getTime() {
		return time;
	}
	
	@Override
	public void tick(Object key) {
		//no matter what key we get, just assume it's good and award to players.
		//TODO do error checking
		Set<Player> players = new HashSet<Player>();
		Player cache;
		
		for (UUID id : MinecraftAchievementsPlugin.plugin.getPlayerManager().getActivePlayers()) {
			cache = Bukkit.getPlayer(id);
			if (cache != null) {
				players.add(cache);
			}
		}
		
		awardToPlayers(players);
	}

}

package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;

/**
 * Achievement for killing a player with a specific instrument
 * @author Skyler
 *
 */
public class KillPlayerWithBowAchievement extends ActiveAchievement {

	private Map<UUID, Boolean> playerCache; //faster lookups
	
	public KillPlayerWithBowAchievement(String name, String description, int points) {
		super(name, description, points);
		playerCache = new HashMap<UUID, Boolean>();
	}

	@EventHandler
	public void OnPlayerDeath(EntityDamageByEntityEvent e) {
		
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		
		if (e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
			return;
		}
		
		Player damagee = (Player) e.getEntity();
		Player damager = null;
		Projectile proj = (Projectile) e.getDamager();
		if (!(proj.getShooter() instanceof Player)) {
			return;
		}
		damager = (Player) ((Projectile) e.getDamager()).getShooter();
		
		if (e.getFinalDamage() < damagee.getHealth()) {
			return;
		}
				
		if (!MinecraftAchievementsPlugin.plugin.getPlayerManager().isActive(damager)) {
			return;
		}

		if (!playerCache.containsKey(damager.getUniqueId())) {
			playerCache.put(damager.getUniqueId(), false);
		}
		
		if (playerCache.get(damager.getUniqueId()) == false) {
			//if they haven't been awarded & cached, actually check
			playerCache.put(damager.getUniqueId(), true);
			MinecraftAchievementsPlugin.plugin.getPlayerManager().addAchievement(
					damager, this);
		}
	}
	
}

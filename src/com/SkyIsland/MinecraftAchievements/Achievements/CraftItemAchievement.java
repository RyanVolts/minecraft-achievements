package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;

public class CraftItemAchievement extends ActiveAchievement {

	private Map<UUID, Boolean> playerCache; //faster lookups
	
	private Material targetItem;
	
	public CraftItemAchievement(String name, String description, int points, Material targetItem) {
		super(name, description, points);
		this.targetItem = targetItem;
		playerCache = new HashMap<UUID, Boolean>();
	}

	@EventHandler
	public void interactEvent(CraftItemEvent e) {

		if (e.isCancelled()) {
			return;
		}
		
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		
		if (!MinecraftAchievementsPlugin.plugin.getPlayerManager().isActive((Player) e.getWhoClicked())) {
			return;
		}

		if (!playerCache.containsKey(e.getWhoClicked().getUniqueId())) {
			playerCache.put(e.getWhoClicked().getUniqueId(), false);
		}
		
		if (playerCache.get(e.getWhoClicked().getUniqueId()) == false) {
			//if they haven't been awarded & cached, actually check
			if (e.getRecipe().getResult().getType() == targetItem) {
					//have equipment now
					Player player = (Player) e.getWhoClicked();
					playerCache.put(player.getUniqueId(), true);
					MinecraftAchievementsPlugin.plugin.getPlayerManager()
						.addAchievement(player, this);
					return;
			}
		}
	}

}

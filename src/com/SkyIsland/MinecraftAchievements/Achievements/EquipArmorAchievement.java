package com.SkyIsland.MinecraftAchievements.Achievements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;

public class EquipArmorAchievement extends ActiveAchievement {

	private Map<UUID, Boolean> playerCache; //faster lookups
	
	public EquipArmorAchievement() {
		super("Equip Armor");
		playerCache = new HashMap<UUID, Boolean>();
	}

	@EventHandler
	public void interactEvent(InventoryClickEvent e) {

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
			if (e.getCursor() != null && e.getCursor().getType() != Material.AIR)
			if (e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
					//have equipment now
					Player player = (Player) e.getWhoClicked();
					playerCache.put(player.getUniqueId(), true);
					MinecraftAchievementsPlugin.plugin.getPlayerManager()
						.addAchievement(player, getName());
					return;
			}
		}
	}
	
}

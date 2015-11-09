package com.SkyIsland.AchievementHunter.Achievements;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.SkyIsland.AchievementHunter.AchievementHunterPlugin;

public class EquipArmorAchievement extends Achievement {

	private Map<UUID, Boolean> playerCache; //faster lookups
	
	public EquipArmorAchievement() {
		super("Equip Armor");
		playerCache = new HashMap<UUID, Boolean>();
	}

	@EventHandler
	public void interactEvent(InventoryInteractEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		
		if (!playerCache.containsKey(e.getWhoClicked().getUniqueId())) {
			playerCache.put(e.getWhoClicked().getUniqueId(), false);
		}
		
		if (playerCache.get(e.getWhoClicked().getUniqueId()) == false) {
			//if they haven't been awarded & cached, actually check
			Player player = (Player) e.getWhoClicked();
			for (ItemStack item : player.getInventory().getArmorContents()) {
				if (item != null && item.getType() != Material.AIR) {
					//have equipment now
					playerCache.put(player.getUniqueId(), true);
					AchievementHunterPlugin.plugin.getPlayerManager().getRecord(player.getUniqueId())
						.addAchievement(getName());
					return;
				}
			}
		}
	}
	
}

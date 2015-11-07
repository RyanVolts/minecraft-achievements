package com.SkyIsland.AchievementHunter;

import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.AchievementHunter.Players.PlayerManager;

/**
 * Main plugin class.<br />
 * Starts the plugin and registers with the server, prepares files, and intializes achievement listeners.
 * @author Skyler
 *
 */
public class AchievementHunterPlugin extends JavaPlugin {
	
	public static AchievementHunterPlugin plugin;
	
	private PlayerManager playerManager;
	
	@Override
	public void onLoad() {
		PlayerManager.PlayerRecord.registerAliases();
		
		this.playerManager = new PlayerManager();
	}
	
	@Override
	public void onEnable() {
			plugin = this;
	}
	
	@Override
	public void onDisable() {
		
	}
}

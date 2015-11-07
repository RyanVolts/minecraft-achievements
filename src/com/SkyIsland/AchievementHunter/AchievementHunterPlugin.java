package com.SkyIsland.AchievementHunter;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class.<br />
 * Starts the plugin and registers with the server, prepares files, and intializes achievement listeners.
 * @author Skyler
 *
 */
public class AchievementHunterPlugin extends JavaPlugin {
	
	public static AchievementHunterPlugin plugin;
	
	@Override
	public void onLoad() {
		
	}
	
	@Override
	public void onEnable() {
			plugin = this;
	}
	
	@Override
	public void onDisable() {
		
	}
}

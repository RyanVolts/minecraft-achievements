package com.SkyIsland.AchievementHunter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.AchievementHunter.Players.PlayerManager;

/**
 * Main plugin class.<br />
 * Starts the plugin and registers with the server, prepares files, and intializes achievement listeners.
 * @author Skyler
 *
 */
public class AchievementHunterPlugin extends JavaPlugin {
	
	private static final String playerSaveFile = "players.yml"; 
	
	public static AchievementHunterPlugin plugin;
	
	private PlayerManager playerManager;
	
	@Override
	public void onLoad() {
		PlayerManager.PlayerRecord.registerAliases();
		
		this.playerManager = new PlayerManager();
		
		if (!new File(getDataFolder(), playerSaveFile).exists()) {
			getLogger().info("Unable to find player database, so one will be created...");
		}
	}
	
	@Override
	public void onEnable() {
			plugin = this;
			
			loadPlayerManager();
	}
	
	@Override
	public void onDisable() {
		File saveFile = new File(getDataFolder(), playerSaveFile);
		try {
			playerManager.save(saveFile, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Will load a player manager from the default playerSaveFile if it exists.<br />
	 * If it does not exist, no action is taken and an empty player manager will be used.
	 */
	private void loadPlayerManager() {
		File saveFile = new File(getDataFolder(), playerSaveFile);
		if (saveFile.exists()) {
			try {
				playerManager.load(saveFile);
			} catch (FileNotFoundException e) {
				//Shouldn't happen
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				//Important error: invalid config
				e.printStackTrace();
			} catch (IOException e) {
				//Sucky error, keep trace
				e.printStackTrace();
			}
		}
	}
}

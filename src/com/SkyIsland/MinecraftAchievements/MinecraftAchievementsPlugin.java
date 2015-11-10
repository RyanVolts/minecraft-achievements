package com.SkyIsland.MinecraftAchievements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.MinecraftAchievements.Achievements.EquipArmorAchievement;
import com.SkyIsland.MinecraftAchievements.Players.PlayerManager;

/**
 * Main plugin class.<br />
 * Starts the plugin and registers with the server, prepares files, and intializes achievement listeners.
 * @author Skyler
 *
 */
public class MinecraftAchievementsPlugin extends JavaPlugin {
	
	private static final String playerSaveFile = "players.yml"; 
	
	public static MinecraftAchievementsPlugin plugin;
	
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
			
			registerAchievements();
			
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
	
	private void registerAchievements() {
		Bukkit.getPluginManager().registerEvents(new EquipArmorAchievement(), this);
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("report")) {
			if (args.length != 1) {
				sender.sendMessage("Please supply a filename to save the report to");
				return false;
			}
			
			File out = new File(getDataFolder(), args[0]);
			return true;
		}
		
		return false;
	}
}

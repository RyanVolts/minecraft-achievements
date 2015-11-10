package com.SkyIsland.MinecraftAchievements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.MinecraftAchievements.Achievements.EquipArmorAchievement;
import com.SkyIsland.MinecraftAchievements.Output.ReportWriter;
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
		plugin = this;
		PlayerManager.PlayerRecord.registerAliases();
		
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		if (!new File(getDataFolder(), playerSaveFile).exists()) {
			getLogger().info("Unable to find player database, so one will be created...");
		}
	}
	
	@Override
	public void onEnable() {
		
		this.playerManager = new PlayerManager();
			
		registerAchievements();
			
		loadPlayerManager();
	}
	
	@Override
	public void onDisable() {
		File saveFile = new File(getDataFolder(), playerSaveFile);
		savePlayerManager(saveFile, true); //always overwrite when saving database
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
			return report(sender, args);
		}
		
		if (cmd.getName().equalsIgnoreCase("playersnapshot")) {
			return snapshot(sender, args);
		}
		
		if (cmd.getName().equalsIgnoreCase("cleardata")) {
			return clear(sender, args);
		}
		
		return false;
	}
	
	/**
	 * Processes a report command.<br />
	 * This command pritns out a report of the player database, including achievements and tracked statistics.
	 * @param sender
	 * @param args
	 * @return
	 * @see {@link com.SkyIsland.MinecraftAchievements.Output.ReportWriter ReportWriter}
	 */
	private boolean report(CommandSender sender, String[] args) {
		if (args.length < 1 || args.length > 2) {
			sender.sendMessage("Please supply a filename to save the report to.");
			return false;
		}
		
		File out = new File(getDataFolder(), args[0]);
		boolean overwrite = false;
		
		if (args.length == 2 && args[1].equalsIgnoreCase("true")) {
			overwrite = true;
		}
		
		boolean success = false;
		try {
			success = ReportWriter.printReport(out, overwrite);
		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "Failed to write out to file!" + ChatColor.RESET);
		}
		
		
		if (!success) {
			sender.sendMessage(ChatColor.YELLOW + "That file already exists!" + ChatColor.RESET);
			sender.sendMessage("Use '/report " + args[0] + " true' to overwrite it.");
			return false;
		}
		
		//print was successful
		sender.sendMessage(ChatColor.GREEN + "Your report has been generated!" + ChatColor.RESET);
		return true;
	}
	
	/**
	 * Processes the snapshot command.<br />
	 * This takes a snapshot of the player database, saving it to a file.
	 * @param sender
	 * @param args
	 * @return
	 */
	private boolean snapshot(CommandSender sender, String[] args) {
		if (args.length != 1 && args.length != 2) {
			sender.sendMessage("Please provide a filename to save the snapshot to!");
			return false;
		}
		
		boolean overwrite = false;
		String fileName = formatFileName(args[0]);
	
		if (args.length == 2 && args[1].equalsIgnoreCase("true")) {
			overwrite = true;
		}
		
		File saveFile = new File(getDataFolder(), fileName);
		if (savePlayerManager(saveFile, overwrite)) {
			sender.sendMessage(ChatColor.GREEN + "Snapshot saved successfully!" + ChatColor.RESET);
		} else {
			sender.sendMessage(ChatColor.RED + "There was a problem saving the snapshot!" + ChatColor.RESET);
		}
		
		return true;
	}
	
	/**
	 * Adjusts filename to fit print criteria.<br />
	 * This method performs the checks to see if it violates the protocol before making changes.
	 * @param rawFileName
	 * @return
	 */
	private String formatFileName(String rawFileName) {
		if (rawFileName.endsWith(".yml")) {
			return rawFileName;
		}
		
		rawFileName += ".yml";
		
		return rawFileName;
	}
	
	private boolean clear(CommandSender sender, String[] args) {
		
		if (args.length != 0) {
			return false;
		}
		
		playerManager.clear();
		
		sender.sendMessage(ChatColor.YELLOW + "Player database has been cleared");
		
		return true;
	}
	
	private boolean savePlayerManager(File saveFile, boolean overwrite) {
		if (!overwrite && saveFile.exists()) {
			getLogger().warning("Unable to save player manager file because the file exists!");
			return false;
		}
		
		try {
			playerManager.save(saveFile, true);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}

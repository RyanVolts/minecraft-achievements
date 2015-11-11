package com.SkyIsland.MinecraftAchievements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.MinecraftAchievements.Achievements.EquipArmorAchievement;
import com.SkyIsland.MinecraftAchievements.Achievements.KillPlayerAchievement;
import com.SkyIsland.MinecraftAchievements.Achievements.OpenChestAchievement;
import com.SkyIsland.MinecraftAchievements.Output.ReportWriter;
import com.SkyIsland.MinecraftAchievements.Players.PlayerManager;
import com.SkyIsland.MinecraftAchievements.Players.PlayerManager.PlayerRecord;

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
	
	/**
	 * Goes through and registers achievements.<br />
	 * Not a very good solution...
	 */
	private void registerAchievements() {
		Bukkit.getPluginManager().registerEvents(new EquipArmorAchievement(), this);
		
		Bukkit.getPluginManager().registerEvents(
				new KillPlayerAchievement("It Was An Accident! I Swear!", "Kill your first player", 10, 1), this);
		Bukkit.getPluginManager().registerEvents(
				new KillPlayerAchievement("Repeat Offender", "Kill 2 players", 15, 2), this);
		Bukkit.getPluginManager().registerEvents(
				new KillPlayerAchievement("Career Tribute", "Kill 5 players", 30, 5), this);

		Bukkit.getPluginManager().registerEvents(
				new OpenChestAchievement("Curious", "Open 1 Chest", 5, 1), this);
		Bukkit.getPluginManager().registerEvents(
				new OpenChestAchievement("Raider of the Lost Chests", "Open 20 Chest", 15, 20), this);
		
		//See sloppy init in PlayerManager#startTimers()
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
		
		if (cmd.getName().equalsIgnoreCase("activateplayer")) {
			return activatePlayer(sender, args);
		}
		
		if (cmd.getName().equalsIgnoreCase("printachievementstatus")) {
			return printStatus(sender, args);
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
	
	private boolean activatePlayer(CommandSender sender, String[] args) {
		
		if (args.length != 1) {
			return false;
		}
		
		if (args[0].equalsIgnoreCase("@a")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				activate(player);
			}
			sender.sendMessage(ChatColor.GREEN + "Activated " + Bukkit.getOnlinePlayers().size() + " players!");
		} else {
			@SuppressWarnings("deprecation")
			Player player = Bukkit.getPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(ChatColor.YELLOW + "Unable to locate player " + args[0]);
				return false;
			}
			
			activate(player);
			sender.sendMessage(ChatColor.GREEN + "Activated " + args[0] + "!");
		}
		
		return true;
	}
	
	private void activate(Player player) {
		playerManager.addActivePlayer(player);
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
	
	/**
	 * Prints general status of the session.
	 * @param sender
	 * @param args
	 * @return
	 */
	private boolean printStatus(CommandSender sender, String[] args) {
		
		if (args.length > 1) {
			return false;
		}
		
		if (args.length == 1) {
			//player lookup
			@SuppressWarnings("deprecation")
			Player player = Bukkit.getPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(ChatColor.YELLOW + "Unable to find player " + args[0]);
				return false;
			}
			
			printPlayerStatus(sender, player);
			return true;
		}
		
		//print session status
		sender.sendMessage("Players: " 
				+ ChatColor.DARK_GREEN + playerManager.getActivePlayers().size() + ChatColor.RESET + " active | " 
				+ ChatColor.DARK_BLUE + playerManager.getPlayers().size() + ChatColor.RESET + " total");
		
		if (!playerManager.getActivePlayers().isEmpty()) {
			sender.sendMessage("Active Players: ");
			
			String msg = "";
			Player cache;
			boolean color = true;
			
			for (UUID id : playerManager.getActivePlayers()) {
				cache = Bukkit.getPlayer(id);
				if (cache == null) {
					continue;
				}
				msg += (color ? ChatColor.DARK_PURPLE : ChatColor.DARK_RED);
				msg += cache.getName() + "  ";
				color = !color;
			}
			
			msg = msg.trim() + ChatColor.RESET;
			
			sender.sendMessage(msg);
		}
				
		
		return true;
	}
	
	private void printPlayerStatus(CommandSender sender, Player player) {
		
		if (!playerManager.hasPlayer(player.getUniqueId())) {
			sender.sendMessage(ChatColor.YELLOW + "No data for that player!");
			return;
		}
		
		sender.sendMessage("Player: " + ChatColor.DARK_BLUE + player.getName() + " - " +
				(playerManager.isActive(player) ? ChatColor.DARK_GREEN + " Active"
												: ChatColor.YELLOW + "Not Active")
				);
		sender.sendMessage("Id: " + ChatColor.AQUA + " (" + player.getUniqueId() + ")");
		
		if (playerManager.isActive(player)) {
			playerManager.updatePlayer(player);
		}
		
		PlayerRecord record = playerManager.getRecord(player.getUniqueId());
		
		sender.sendMessage("Score: " + ChatColor.GOLD + record.getScore());
		
		if (record.getAchievements().isEmpty()) {
			sender.sendMessage(ChatColor.GRAY + "No Achievements!");
		} else {
			sender.sendMessage("Achievements:");
			for (String achievement : record.getAchievements()) {
				sender.sendMessage("  -" + ChatColor.DARK_PURPLE + achievement);
			}
		}
		
		if (record.getStatisticsMap().isEmpty()) {
			sender.sendMessage(ChatColor.GRAY + "No tracked statistics yet!");
		} else {
			sender.sendMessage("Statistics:");
			Map<Statistic, Integer> stats = record.getStatisticsMap();
			for (Statistic stat : stats.keySet()) {
				sender.sendMessage(ChatColor.DARK_GRAY + stat.toString() + ChatColor.RESET
						+ ": " + ChatColor.DARK_GREEN + stats.get(stat));
			}
		}
	}
}

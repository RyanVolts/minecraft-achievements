package com.SkyIsland.MinecraftAchievements.Players;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;

/**
 * Stores information about each player -- metrics and which achievements they've unlocked
 * @author Skyler
 *
 */
public class PlayerManager implements Listener {
	
	/**
	 * Class used for storing any additional information about a player we may need.
	 * @author Skyler
	 *
	 */
	public static class PlayerRecord implements ConfigurationSerializable {
		
		public static final void registerAliases() {
			ConfigurationSerialization.registerClass(PlayerRecord.class, "PlayerRecord");
			ConfigurationSerialization.registerClass(PlayerRecord.class, "playerrecord");
			ConfigurationSerialization.registerClass(PlayerRecord.class, PlayerRecord.class.getName());
		}
		
		/**
		 * The statistics in vanilla minecraft that are stored with player records.
		 */
		private static enum vanillaStatistics {
			CHEST_OPENED(Statistic.CHEST_OPENED),
			PLAYER_KILLS(Statistic.PLAYER_KILLS),
			SPRINT_ONE_CM(Statistic.SPRINT_ONE_CM),
			WALK_ONE_CM(Statistic.WALK_ONE_CM);
			
			private Statistic statistic;
			
			private vanillaStatistics(Statistic stat) {
				this.statistic = stat;
			}
			
			public Statistic getStatistic() {
				return statistic;
			}
		}
		
		private String name;
		
		private List<String> achievements;
		
		private Map<Statistic, Integer> statistics;
		
		private PlayerRecord(Player player) {
			this.name = player.getName();
			achievements = new LinkedList<String>();
			statistics = new TreeMap<Statistic, Integer>();
		}
		
		private PlayerRecord(String name) {
			this.name = name;
			achievements = new LinkedList<String>();
			statistics = new TreeMap<Statistic, Integer>();
		}
		
		public List<String> getAchievements() {
			return achievements;
		}
		
		public boolean addAchievement(String achievement) {
			if (!achievements.contains(achievement)) {
				achievements.add(achievement);
				return true;
			}
			return false;
		}
		
		/**
		 * Checks whether a player already has an achievement
		 * @param achievement
		 * @return
		 */
		public boolean hasAchievement(String achievement) {
			return achievements.contains(achievement);
		}
		
		public String getName() {
			return this.name;
		}
		
		private void update(Player player) {
			if (name == null || name.trim().isEmpty()) {
				name = player.getName();
			}
			
			for (vanillaStatistics stat : vanillaStatistics.values()) {
				statistics.put(stat.getStatistic(), player.getStatistic(stat.getStatistic()));
			}
		}
		
		public Map<Statistic, Integer> getStatisticsMap() {
			return statistics;
		}

		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("achievements", achievements);
			map.put("display", name);
			
			Map<String, Integer> stats = new HashMap<String, Integer>();
			
			for (Statistic stat : statistics.keySet()) {
				stats.put(stat.name(), statistics.get(stat)); //convert our map to string keys
			}
			
			map.put("tracked-stats", stats);
			
			return map;
		}

	    @SuppressWarnings("unchecked")
		public static PlayerRecord valueOf(Map<String, Object> configMap) {
	    	
	    	if (!configMap.containsKey("display") || !configMap.containsKey("achievements")
	    			|| !configMap.containsKey("tracked-stats")) {
	    		MinecraftAchievementsPlugin.plugin.getLogger().warning(
	    				"Unable to load player record, as it's missing keys" +
	    				(configMap.containsKey("display") ? ": " + configMap.get("display")
	    				: "!")
	    				);
	    		return new PlayerRecord("");
	    	}
	    	
	    	String name = (String) configMap.get("display");
	    	
	        PlayerRecord record = new PlayerRecord(name);
	        
	        record.achievements = (List<String>) configMap.get("achievements");
	        
	        //load up stats from map of ordinals to values
	        Map<String, Integer> stats = (Map<String, Integer>) configMap.get("tracked-stats");
	        for (String key : stats.keySet()) {
	        	record.statistics.put(Statistic.valueOf(key), stats.get(key));
	        }
	        
	        return record;
	    }

		
	}
	
	private Map<UUID, PlayerRecord> records;
	
	/**
	 * Constructs an empty player manager with no player records.
	 */
	public PlayerManager() {
		this.records = new HashMap<UUID, PlayerRecord>();
		
		Bukkit.getPluginManager().registerEvents(this, MinecraftAchievementsPlugin.plugin);
	}
	
	/**
	 * Loads from the provided file.<br />
	 * <b>All information</b> held by this manager before the load will be erased and permanently lost!
	 * @param config
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void load(File loadFile) throws InvalidConfigurationException, FileNotFoundException, IOException {
		
		YamlConfiguration config = new YamlConfiguration();
		
		config.load(loadFile);
		
		records.clear();
		
		//Expects a list of:
		//id:
		//  record:
		
		UUID id;
		PlayerRecord record;
		ConfigurationSection section;
		
		for (String key : config.getKeys(false)) {
			section = config.getConfigurationSection(key);
			
			id = UUID.fromString(key);
			record = (PlayerRecord) section.get("record");
			
			if (record.getName() == null || record.getName().trim().isEmpty()) {
				MinecraftAchievementsPlugin.plugin.getLogger().warning("Skipping bad record...");
			}
			
			records.put(id, record);
		}
		
	}
	
	/**
	 * Saves the current manager's records to the provided file.<br />
	 * If the save is operating in overwrite mode, it will delete an existing file before creating it's new one.<br />
	 * Otherwise, it will just fail to save.
	 * @param config
	 * @param overwrite
	 * @throws IOException 
	 */
	public void save(File saveFile, boolean overwrite) throws IOException {
		if (saveFile.exists()) {
			if (overwrite) {
				saveFile.delete();
			} else {
				throw new IOException("The provided file already exists!");
			}
		}
		
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection section;
		
		for (Entry<UUID, PlayerRecord> e : records.entrySet()) {
			section = config.createSection(e.getKey().toString());
			section.set("record", e.getValue());
		}
		
		config.save(saveFile);
	}
	
	/**
	 * Fetches the record for the given player.
	 * @param playerID The player to look up
	 * @return A record for that player, or null if the manager doesn't know the player
	 */
	public PlayerRecord getRecord(UUID playerID) {
		return records.get(playerID);
	}
	
	/**
	 * Checks and returns whether the provided player has a record
	 * @param playerID
	 * @return
	 */
	public boolean hasPlayer(UUID playerID) {
		return records.containsKey(playerID);
	}
	
	/**
	 * Adds an empty record for the provided player
	 * @param playerID
	 * @return true if a new record was created, false otherwise
	 */
	public boolean addPlayer(Player player) {
		if (records.containsKey(player.getUniqueId())) {
			return false;
		}
		
		records.put(player.getUniqueId(), new PlayerRecord(player));
		
		return true;
	}
	
	public Set<UUID> getPlayers() {
		return records.keySet();
	}
	
	/**
	 * Adds the given achievement to the player's record.<br />
	 * If the player does not have a record, a new one will be created.
	 * @param playerID
	 * @param achievement
	 * @return true if a new record was created for the player. False if it already existed
	 */
	public boolean addAchievement(Player player, String achievement) {
		if (records.containsKey(player.getUniqueId())) {
			if (records.get(player.getUniqueId()).addAchievement(achievement)) {
			
				player.sendMessage("You've unlocked the achievement " + ChatColor.GREEN + "["
						+ achievement + "]" + ChatColor.RESET);
				
				
			}
			return false;
		}
		
		addPlayer(player);
		
		if (records.get(player.getUniqueId()).addAchievement(achievement)) {
			player.sendMessage("You've unlocked the achievement " + ChatColor.GREEN + "["
					+ achievement + "]" + ChatColor.RESET);
		}
	
		return true;
	}
	
	/**
	 * Calls the player record for updating by the manager.<br />
	 * This means that the player's statistics will be captured so they don't have to be online to fetch them.<br />
	 * For a list of captured statistics, see {@link PlayerRecord#vanillaStatistics}.
	 * @param player
	 * @return true if the player was added to the manager because of this call
	 */
	public boolean updatePlayer(Player player) {
		if (records.containsKey(player.getUniqueId())) {
			records.get(player.getUniqueId()).update(player);
			return false;
		}
		
		records.get(player.getUniqueId()).update(player);
		return true;
	}
	
	/**
	 * Clears the player database completely.
	 */
	public void clear() {
		records.clear();
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		updatePlayer(e.getPlayer());
	}
}

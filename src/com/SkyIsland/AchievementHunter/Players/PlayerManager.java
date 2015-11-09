package com.SkyIsland.AchievementHunter.Players;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

/**
 * Stores information about each player -- metrics and which achievements they've unlocked
 * @author Skyler
 *
 */
public class PlayerManager {
	
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
		
		private List<String> achievements;
		
		private PlayerRecord() {
			achievements = new LinkedList<String>();
		}
		
		public List<String> getAchievements() {
			return achievements;
		}
		
		public void addAchievement(String achievement) {
			achievements.add(achievement);
		}
		
		/**
		 * Checks whether a player already has an achievement
		 * @param achievement
		 * @return
		 */
		public boolean hasAchievement(String achievement) {
			return achievements.contains(achievement);
		}

		@Override
		public Map<String, Object> serialize() {
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("achievements", achievements);
			
			return map;
		}

	    @SuppressWarnings("unchecked")
		public static PlayerRecord valueOf(Map<String, Object> configMap) {
	        PlayerRecord record = new PlayerRecord();
	        
	        record.achievements = (List<String>) configMap.get("achievements");
	        
	        return record;
	    }

		
	}
	
	private Map<UUID, PlayerRecord> records;
	
	/**
	 * Constructs an empty player manager with no player records.
	 */
	public PlayerManager() {
		this.records = new HashMap<UUID, PlayerRecord>();
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
			
			if (!section.contains("record")) {
				throw new InvalidConfigurationException("Missing a key in player record: " + key);
			}
			
			id = UUID.fromString(key);
			record = (PlayerRecord) section.get("record");
			
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
			section.set("id", e.getValue());
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
	public boolean addPlayer(UUID playerID) {
		if (records.containsKey(playerID)) {
			return false;
		}
		
		records.put(playerID, new PlayerRecord());
		
		return true;
	}
	
	/**
	 * Adds the given achievement to the player's record.<br />
	 * If the player does not have a record, a new one will be created.
	 * @param playerID
	 * @param achievement
	 * @return true if a new record was created for the player. False if it already existed
	 */
	public boolean addAchievement(UUID playerID, String achievement) {
		if (records.containsKey(playerID)) {
			records.get(playerID).addAchievement(achievement);
			return false;
		}
		
		addPlayer(playerID);
		records.get(playerID).addAchievement(achievement);
		
		OfflinePlayer op = Bukkit.getOfflinePlayer(playerID);
		if (op.isOnline()) {
			((Player) op).sendMessage("You've unlocked the achievement " + ChatColor.GREEN + "["
					+ achievement + "]" + ChatColor.RESET);
		}
	
		return true;
	}
}

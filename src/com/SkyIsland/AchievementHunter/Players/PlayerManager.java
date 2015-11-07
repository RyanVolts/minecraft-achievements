package com.SkyIsland.AchievementHunter.Players;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
	public class PlayerRecord {
		
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
		
	}
	
	private Map<UUID, PlayerRecord> records;
	
	/**
	 * Constructs an empty player manager with no player records.
	 */
	public PlayerManager() {
		this.records = new HashMap<UUID, PlayerRecord>();
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
	
		return true;
	}
}

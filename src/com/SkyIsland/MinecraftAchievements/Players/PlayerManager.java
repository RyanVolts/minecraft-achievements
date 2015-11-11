package com.SkyIsland.MinecraftAchievements.Players;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;
import com.SkyIsland.MinecraftAchievements.Achievements.Achievement;
import com.SkyIsland.MinecraftAchievements.Achievements.SurviveAchievement;
import com.SkyIsland.MinecraftAchievements.Achievements.TimedAchievement;
import com.SkyIsland.MinecraftAchievements.Scheduler.Scheduler;

/**
 * Stores information about each player -- metrics and which achievements they've unlocked
 * @author Skyler
 * TODO We currently don't keep our own Statistics, and can't reset them!
 */
public class PlayerManager implements Listener {
	
	/**
	 * Constant as to whether 'active players' should only include people who haven't died yet.
	 */
	private static final boolean deactivatePlayerOnDeath = true;
	
	private static final String achievToken = "[!ACHIEVE!]";
	
	private static final String playerToken = "[!PLAYER!]";
	
	private static final String descToken = "[!DESC!]";
	
	private static final String awardString = "tellraw " + playerToken + " [\"\",{\"text\":\"You've unlocked the achievement \"},{\"text\":\"[" + achievToken + "]\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + descToken + "\"}]}}}]";
			
			
		
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
			TRAPPED_CHESTS_OPENED(Statistic.TRAPPED_CHEST_TRIGGERED),
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
		
		private int score;
		
		private Collection<TimedAchievement> timers;
		
		private PlayerRecord(Player player) {
			this.name = player.getName();
			achievements = new LinkedList<String>();
			statistics = new TreeMap<Statistic, Integer>();
			timers = new LinkedList<TimedAchievement>();
			score = 0;
		}
		
		private PlayerRecord(String name) {
			this.name = name;
			achievements = new LinkedList<String>();
			statistics = new TreeMap<Statistic, Integer>();
			timers = new LinkedList<TimedAchievement>();
			score = 0;
		}
		
		public List<String> getAchievements() {
			return achievements;
		}
		
		public int getScore() {
			return score;
		}
		
		public boolean addAchievement(Achievement achievement) {
			if (!achievements.contains(achievement.getName())) {
				achievements.add(achievement.getName());
				score += achievement.getPoint_Value();
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
			
			map.put("score", score);
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
	    			|| !configMap.containsKey("tracked-stats") || !configMap.containsKey("score")) {
	    		MinecraftAchievementsPlugin.plugin.getLogger().warning(
	    				"Unable to load player record, as it's missing keys" +
	    				(configMap.containsKey("display") ? ": " + configMap.get("display")
	    				: "!")
	    				);
	    		return new PlayerRecord("");
	    	}
	    	
	    	String name = (String) configMap.get("display");
	    	
	        PlayerRecord record = new PlayerRecord(name);
	        
	        record.score = (int) configMap.get("score");
	        
	        record.achievements = (List<String>) configMap.get("achievements");
	        
	        //load up stats from map of ordinals to values
	        Map<String, Integer> stats = (Map<String, Integer>) configMap.get("tracked-stats");
	        for (String key : stats.keySet()) {
	        	record.statistics.put(Statistic.valueOf(key), stats.get(key));
	        }
	        
	        return record;
	    }
	    
	    private void addTimer(TimedAchievement timer) {
	    	this.timers.add(timer);
	    }
	    
	    private void stopTimers() {
	    	Scheduler sched = Scheduler.getScheduler();
	    	for (TimedAchievement timer : timers) {
	    		sched.unregister(timer);
	    	}
	    }

		
	}
	
	private Map<UUID, PlayerRecord> records;
	
	private Set<UUID> activePlayers;
	
	/**
	 * Constructs an empty player manager with no player records.
	 */
	public PlayerManager() {
		this.records = new HashMap<UUID, PlayerRecord>();
		this.activePlayers = new HashSet<UUID>();
		
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
		records.get(player.getUniqueId()).update(player);
		
		return true;
	}
	
	/**
	 * Gets ALL players in the database, regardless of active status.
	 * @return
	 * @see #getActivePlayers()
	 */
	public Set<UUID> getPlayers() {
		return records.keySet();
	}
	
	/**
	 * Returns <b>only</b> active players.
	 * @return
	 * @see #getPlayers()
	 */
	public Set<UUID> getActivePlayers() {
		return activePlayers;
	}
	
	/**
	 * Checks whether a player is active and being tracked
	 * @param player
	 * @return
	 */
	public boolean isActive(Player player) {
		return activePlayers.contains(player.getUniqueId());
	}
	
	/**
	 * Adds the given achievement to the player's record.<br />
	 * If the player does not have a record, a new one will be created.
	 * @param playerID
	 * @param achievement
	 * @return true if a new record was created for the player. False if it already existed
	 */
	public boolean addAchievement(Player player, Achievement achievement) {
		if (records.containsKey(player.getUniqueId())) {
			if (records.get(player.getUniqueId()).addAchievement(achievement)) {

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getAchievementCommand(player, achievement));
				
			}
			return false;
		}
		
		addPlayer(player);
		
		if (records.get(player.getUniqueId()).addAchievement(achievement)) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getAchievementCommand(player, achievement));
		}
	
		return true;
	}
	
	/**
	 * Gets the achievement reward text command to be executed.
	 * @param player
	 * @param achievement
	 * @return
	 */
	private String getAchievementCommand(Player player, Achievement achievement) {
		String cmd = PlayerManager.awardString;
		cmd = cmd.replace(achievToken, achievement.getName())
			.replace(playerToken, player.getName())
			.replace(descToken, achievement.getDescription());
		
		return cmd;
	}
	
	/**
	 * Calls the player record for updating by the manager.<br />
	 * This means that the player's statistics will be captured so they don't have to be online to fetch them.<br />
	 * For a list of captured statistics, see {@link PlayerRecord#vanillaStatistics}.<br />
	 * <b>Note:</b> This does not check if the player is active before performing the update!
	 * @param player
	 * @return true if the player was added to the manager because of this call
	 */
	public boolean updatePlayer(Player player) {
		if (records.containsKey(player.getUniqueId())) {
			records.get(player.getUniqueId()).update(player);
			return false;
		}
		
		records.put(player.getUniqueId(), new PlayerRecord(player));
		records.get(player.getUniqueId()).update(player);
		return true;
	}
	
	/**
	 * Clears the player database completely.
	 */
	public void clear() {
		records.clear();
		activePlayers.clear();
	}
	
	public void addActivePlayer(Player player) {
		addPlayer(player);
		
		if (!activePlayers.contains(player.getUniqueId())) {
			activePlayers.add(player.getUniqueId());
			startTimers(player.getUniqueId());
			clearPlayer(player);
			updatePlayer(player);
		}
		
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!PlayerManager.deactivatePlayerOnDeath) {
			return;
		}
		
		if (activePlayers.contains(e.getEntity().getUniqueId())) {
			updatePlayer(e.getEntity());
			activePlayers.remove(e.getEntity().getUniqueId());
			haltTimers(e.getEntity().getUniqueId());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (activePlayers.contains(e.getPlayer().getUniqueId())) {
			updatePlayer(e.getPlayer());
			activePlayers.remove(e.getPlayer().getUniqueId());
			haltTimers(e.getPlayer().getUniqueId());
		}
	}
	
	/**
	 * Internal helper method for setting times to start for the specified player.<br />
	 * This is expected to be called when the player is activated, but performs no checks.
	 * @param record
	 */
	private void startTimers(UUID playerID) {
		PlayerRecord record = records.get(playerID);
		
		if (record == null) {
			return;
		}
		
		record.addTimer(new SurviveAchievement(playerID, "Alive", "Survive 1 Minute", 10, 60));
		record.addTimer(new SurviveAchievement(playerID, "Staying Alive", "Survive 5 Minutes", 15, 300));
		record.addTimer(new SurviveAchievement(playerID, "Still Alive", "Survive 10 Minutes", 25, 600));
	}
	
	/**
	 * Halts all timers for the player.<br />
	 * This is expected to be called when the player is being deactivated, but performs no checks.
	 * @param record
	 */
	private void haltTimers(UUID playerID) {
		PlayerRecord record = records.get(playerID);
		
		if (record == null) {
			return;
		}
		
		record.stopTimers();
	}
	
	public void onTimer(UUID playerID, TimedAchievement timer) {
		PlayerRecord record = records.get(playerID);
		if (record == null) {
			MinecraftAchievementsPlugin.plugin.getLogger().warning("Encountered a bad timer lookup for id:"
					+ playerID.toString());
			return;
		}
		
		Player player = Bukkit.getPlayer(playerID);
		
		if (player == null) {
			MinecraftAchievementsPlugin.plugin.getLogger().warning("Offline Player lookup:"
					+ playerID.toString());			
			return;
		}
		
		if (record.timers.contains(timer)) {
			record.timers.remove(timer);
			addAchievement(player, timer);
		}
	}
	
	private void clearPlayer(Player player) {
		for (PlayerRecord.vanillaStatistics stat : PlayerRecord.vanillaStatistics.values()) {
			player.decrementStatistic(stat.getStatistic(), 
					player.getStatistic(stat.getStatistic()));
		}
	}
}

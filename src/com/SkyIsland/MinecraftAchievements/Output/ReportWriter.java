package com.SkyIsland.MinecraftAchievements.Output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;
import com.SkyIsland.MinecraftAchievements.Players.PlayerManager;

/**
 * Our default report writer.<br />
 * This writer prints out all achievements from a player as well as some base statistics, which include<br />
 * those defined in 
 * {@link com.SkyIsland.MinecraftAchievements.Players.PlayerManager.PlayerRecord#vanillaStatistics
 * vanillaStatistics}
 * @author Skyler
 *
 */
public final class ReportWriter {
	
	/**
	 * Prints out a report to the given file.
	 * @param output The file to write out to
	 * @param overwrite True if the writer should remove any existing files before writing out.<br />
	 * 			Note that when set to <i>not</i> overwrite and the file already exists, this method will fail.
	 * @return False if overwrite is false and the file exists. True otherwise;
	 */
	public static boolean printReport(File output, boolean overwrite) throws IOException {
		if (output.exists()) {
			return false;
		}
		
		updatePlayers();
		
		PrintWriter writer = new PrintWriter(output);
		
		writer.println(getTimeStamp(output.getName()));
		
		PlayerManager manager = MinecraftAchievementsPlugin.plugin.getPlayerManager();
		
		for (UUID id : manager.getPlayers()) {
			writer.println(manager.getRecord(id).getName() + " (" + id + "):");
			for (String achievement : manager.getRecord(id).getAchievements()) {
				writer.println("  -" + achievement);
			}

			for (String line : getStatistics(manager.getRecord(id).getStatisticsMap())) {
				writer.println(line);
			}
			
		}
		
		
		writer.close();
		
		return true;
	}
	
	private static String getTimeStamp(String reportName) {
		DateFormat fmt = new SimpleDateFormat("hh:mm:ss - MMMM d, YYYY");
		return "Report " + reportName + " - Generated " + fmt.format(new Date());
	}
	
	private static List<String> getStatistics(Map<Statistic, Integer> stats) {
		List<String> statsList = new LinkedList<String>();
		
		for(Statistic stat : stats.keySet()) {
			statsList.add(stat.toString() + ": " + stat);
		}
		
		return statsList;
	}
	
	/**
	 * Goes through players and attempts to update them (if they're online)
	 */
	private static void updatePlayers() {
		PlayerManager manager = MinecraftAchievementsPlugin.plugin.getPlayerManager();
		OfflinePlayer cache;
		
		for (UUID id : manager.getPlayers()) {
			cache = Bukkit.getOfflinePlayer(id);
			if (cache.isOnline()) {
				//update their record
				manager.updatePlayer(cache.getPlayer());
			}
		}
	}
}

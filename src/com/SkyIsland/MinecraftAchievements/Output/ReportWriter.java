package com.SkyIsland.MinecraftAchievements.Output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;
import com.SkyIsland.MinecraftAchievements.Players.PlayerManager;

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
		
		PrintWriter writer = new PrintWriter(output);
		
		writer.println(getTimeStamp(output.getName()));
		
		PlayerManager manager = MinecraftAchievementsPlugin.plugin.getPlayerManager();
		
		for (UUID id : manager.getPlayers()) {
			writer.println(manager.getRecord(id).getName() + " (" + id + "):");
			for (String achievement : manager.getRecord(id).getAchievements()) {
				writer.println("  -" + achievement);
			}
			
		}
		
		
		writer.close();
		
		return true;
	}
	
	private static String getTimeStamp(String reportName) {
		DateFormat fmt = new SimpleDateFormat("hh:mm:ss - MMMM d, YYYY");
		return "Report " + reportName + " - Generated " + fmt.format(new Date());
	}
}

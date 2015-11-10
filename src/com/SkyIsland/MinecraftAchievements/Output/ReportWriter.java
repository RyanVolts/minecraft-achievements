package com.SkyIsland.MinecraftAchievements.Output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
		PlayerManager manager = MinecraftAchievementsPlugin.plugin.getPlayerManager();
		for (UUID id : manager.getPlayers()) {
			
		}
		
		
		writer.close();
		
		return true;
	}
	
}

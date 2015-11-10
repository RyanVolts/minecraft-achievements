package com.SkyIsland.MinecraftAchievements.Scheduler;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import com.SkyIsland.MinecraftAchievements.MinecraftAchievementsPlugin;

public final class Scheduler {
	
	private static final int ticksPerSecond = 20;
	
	private static Scheduler scheduler;
	
	private Map<Tickable, Reminder> map;
	
	private class Reminder extends BukkitRunnable {
		
		private Object key;
		
		private Tickable owner;
		
		private Reminder(Tickable owner, Object key) {
			this.owner = owner;
			this.key = key;
		}

		@Override
		public void run() {
			scheduler.notify(this);
		}
		
		private Object getKey() {
			return key;
		}
		
		private Tickable getOwner() {
			return owner;
		}
	}
	
	
	/**
	 * Returns the scheduler that can be used to registered {@link Tickable} objects
	 * @return
	 */
	public static Scheduler getScheduler() {
		if (scheduler == null) {
			scheduler = new Scheduler();
		}
		
		return scheduler;
	}
	
	private Scheduler() {
		map = new HashMap<Tickable, Reminder>();
	}
	
	/**
	 * Internal reminder mechanism that allows the scheduler to know 
	 * @param reminder
	 */
	private void notify(Reminder reminder) {
		map.remove(reminder.getOwner());
		reminder.getOwner().tick(reminder.getKey());
	}
	
	/**
	 * Schedules the provided tickable object to be reminded in (<i>seconds</i>) seconds via the {@link Tickable#tick(Object)}
	 * method.<br />
	 * Note that the object provided as a 'reference' object is passed back to the tickable object, possibly as a way to
	 * distinguish between alert events.
	 * @param tickable The instance to 'tick' when the time is up
	 * @param reference An object that can be identified and acted upon when the instance if 'ticked'
	 * @param seconds How many seconds to remind the instance after. <b>Please Note:</b> values that
	 * are not divisible by .05 will be rounded to the nearest .05 (a server tick).
	 * @return True if there was already a scheduled event for this tickable instance that was overwritten, false otherwise
	 */
	public boolean schedule(Tickable tickable, Object reference, double seconds) {
		if (tickable == null || seconds < .0001) {
			return false;
		}

		boolean exists = map.containsKey(tickable);
		
		Reminder reminder = new Reminder(tickable, reference);
		
		map.put(tickable, reminder);
		
		long ticks = Math.round(seconds * Scheduler.ticksPerSecond);
		
		reminder.runTaskLater(MinecraftAchievementsPlugin.plugin, ticks);
		
		return exists;
		
		
	}
	
	/**
	 * Attempts to unregister the tickable instance.
	 * @param tickable
	 * @return Whether or not this was successful, including whther there was something waiting
	 */
	public boolean unregister(Tickable tickable) {
		if (map.containsKey(tickable)) {
			map.get(tickable).cancel();
			return map.remove(tickable) != null;
		}
		
		return false;
	}
	
	
}

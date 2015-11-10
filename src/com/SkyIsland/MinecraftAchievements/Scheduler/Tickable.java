package com.SkyIsland.MinecraftAchievements.Scheduler;

/**
 * Tickable instances can be registered with the scheduler to receive a notification after some amount of time.<br />
 * @author Skyler
 *
 */
public interface Tickable {
	
	/**
	 * Called when the scheduler is trying to remind the registered Tickable object of something.<br />
	 * The reference passed back is the same provided when registered.<br />
	 * Receipt of this method indicates that the object is no longer registered with the scheduler.
	 * @param reference
	 */
	public void tick(Object reference);
	
	/**
	 * Checks whether this method is equal to the passed.<br />
	 * Tickable objects NEED to be able to tell if another object is the same as them, for lookup in a map.
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o);
}

package me.crashcringle.matrix;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class LocationsManager {
	
	private Map<String, Location> targetLocations = new HashMap<String, Location>();
	
	private EnderEyeChanger plugin;
	
	public LocationsManager(EnderEyeChanger plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean addTargetLocation(String name, Location loc)
	{
		if (targetLocations.containsKey(name.toLowerCase())) return false; // If it already exists then return
		targetLocations.put(name.toLowerCase(), loc);
		return true;
	}
	
	public boolean removeTargetLocation(String name)
	{
		return targetLocations.remove(name.toLowerCase()) != null;
	}
	
	public Location getNearestTargetLocation(Location loc)
	{
		Location closest = null;
		double closedist = -1, d;
		for (Location target : targetLocations.values())
		{
			if (loc.getWorld().getName() != target.getWorld().getName()) continue;
			if (closedist == -1)
			{
				closedist = target.distanceSquared(loc);
				closest = target;
			} else if ((d = target.distanceSquared(loc)) < closedist) {
				closest = target;
				closedist = d;
			}
		}
		return closest;
	}
	
	public void load(ConfigurationSection locations)
	{
		int i = 0;
		for (String locName : locations.getKeys(false))
		{
			double x, y, z;
			World world;
			ConfigurationSection c = locations.getConfigurationSection(locName);
			x = c.getDouble("x", 0);
			y = c.getDouble("y", 0);
			z = c.getDouble("z", 0);
			world = plugin.getServer().getWorld(c.getString("world", "world"));
			if (world == null)
			{
				plugin.getLogger().warning("Could not load target location '" + locName + "': Unknown world name!");
				continue;
			}
			addTargetLocation(locName, new Location(world, x, y, z));
			i++;
		}
		plugin.getLogger().info("Loaded " + i + " ender eye waypoint locations.");
	}
	
	public void save(ConfigurationSection c)
	{
		for (String locName : targetLocations.keySet())
		{
			ConfigurationSection location = c.createSection(locName.toLowerCase());
			Location loc = targetLocations.get(locName);
			location.set("x", loc.getX());
			location.set("y", loc.getY());
			location.set("z", loc.getZ());
			location.set("world", loc.getWorld().getName());
		}
	}
	
	public Map<String, Location> getAllTargets()
	{
		return targetLocations;
	}

}

package me.crashcringle.matrix;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderEyeChanger extends JavaPlugin {
	
	private LocationsManager locMan;
	private EEConfiguration config;
	
	public void onEnable()
	{
		locMan = new LocationsManager(this);
		config = new EEConfiguration(this);
		
		config.loadConfig(); // Load the config
		
		// Register player interact events
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		getLogger().info("EnderEyeChanger enabled!");
	}
	
	public void onDisable()
	{
		config.saveConfig();
		getLogger().info("EnderEyeChanger disabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender.getName().contains("CrashCringle")) {
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.GREEN + "All loaded ender eye waypoints:");
				for (Entry<String, Location> e : locMan.getAllTargets().entrySet())
				{
					sender.sendMessage(ChatColor.GRAY + e.getKey() + " [" + e.getValue().getWorld().getName() + "]: " + ChatColor.AQUA + "x: " + e.getValue().getBlockX() + ", y: " + e.getValue().getBlockY() + ", z: " + e.getValue().getBlockZ());
				}
				if (config.useStrongholdLocation()) sender.sendMessage(ChatColor.RED + "Ender Eyes are set to point towards stronghold locations.");
				return true;
			}
			if (args[0].equalsIgnoreCase("stronghold")) 
			{
				config.setUseStrongholdLocation(true);
				sender.sendMessage(ChatColor.AQUA + "Ender eyes will now point to default stronghold locations.");
				return true;
			}
			if (args[0].equalsIgnoreCase("waypoint")) 
			{
				// Check if waypoints exist
				if (locMan.getAllTargets().size() == 0) // No waypoints exist so we can't point to them
				{
					sender.sendMessage(ChatColor.RED + "No waypoints exist. Create one with /endereye add <name> first.");
					return true;
				}
				// Set useStrongHoldLocation to false
				config.setUseStrongholdLocation(false);
				sender.sendMessage(ChatColor.AQUA + "Ender eyes will now point towards set waypoints.");
				return true;
			}
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) 
			{
				// Print help message
				sender.sendMessage(ChatColor.YELLOW + "===== EnderEyeChanger version " + this.getDescription().getVersion() + " by Javacow =====");
				sender.sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + "Allows control over the mysterious Eye of Ender.");
				sender.sendMessage(ChatColor.GREEN + "/endereye" + ChatColor.GOLD + "  Displays info about current waypoints.");
				sender.sendMessage(ChatColor.GREEN + "/endereye waypoint" + ChatColor.GOLD + "  Sets ender eyes to point to set waypoints.");
				sender.sendMessage(ChatColor.GREEN + "/endereye stronghold" + ChatColor.GOLD + "  Sets ender eyes to point to strongholds.");
				sender.sendMessage(ChatColor.GREEN + "/endereye add <name>" + ChatColor.GOLD + "  Adds a waypoint with a unique name. Ender eyes will automatically point towards the closest waypoint.");
				sender.sendMessage(ChatColor.GREEN + "/endereye remove <name>" + ChatColor.GOLD + "  Removes an existing waypoint. If no other waypoints exist, ender eyes will point towards strongholds.");
				return true;
			}
			if (!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
				return true;
			}
			if (args[0].equalsIgnoreCase("add"))
			{
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "You must specify a unique name for this waypoint!");
					return true;
				}
				Location loc = ((Player) sender).getLocation();
				if (!locMan.addTargetLocation(args[1], loc))
				{
					sender.sendMessage(ChatColor.RED + "That waypoint already exists. Try using a different name or do /endereye for a list of all waypoints.");
					return true;
				}
				
				config.setUseStrongholdLocation(false);
				sender.sendMessage(ChatColor.AQUA + "Waypoint '" + args[1] + "' added.");
				return true;
			}
			if (args[0].equalsIgnoreCase("remove"))
			{
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "You must specify a unique name for this waypoint!");
					return true;
				}
				if (locMan.removeTargetLocation(args[1])) sender.sendMessage(ChatColor.AQUA + "Waypoint '" + args[1] + "' removed.");
				else sender.sendMessage(ChatColor.RED + "That waypoint doesn't exist. Do /endereye for a list of waypoints.");
				if (locMan.getAllTargets().size() == 0) config.setUseStrongholdLocation(false);
				return true;
			}
		}
			return false;
	}
	
	public LocationsManager getLocationManager()
	{
		return locMan;
	}
	
	public boolean useStrongholdLocation()
	{
		return config.useStrongholdLocation();
	}
	
	public EEConfiguration getConfiguration()
	{
		return config;
	}
}

package me.crashcringle.matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class EEConfiguration {
	
	private EnderEyeChanger plugin;
	
	private String path_config = "config.yml";
	private String path_netherEnd = "allow-nether-end";
	private String path_useStronghold = "use-stronghold-location";
	private String path_locations = "target-locations";
	
	private File configFile;
	private YamlConfiguration config;
	
	private boolean useStronghold = true;
	private boolean allowNetherEnd = false;
	
	public EEConfiguration(EnderEyeChanger plugin)
	{
		this.plugin = plugin;
		
		if (!plugin.getDataFolder().exists()) // If the data folder does not exist yet...
		{
			plugin.getLogger().info("Config does not exist yet, creating defaults...");
			plugin.getDataFolder().mkdirs();
		}
		
		configFile = new File(plugin.getDataFolder(), path_config); // Load config_file (config.yml) from plugin folder
		
		if (!configFile.exists()) // If files don't exist then copy default files from plugin.jar file
		{
			copy(plugin.getResource(path_config), configFile);
			plugin.getLogger().info("Created default configuration file.");
		}
	}
	
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void loadConfig()
	{
		config = YamlConfiguration.loadConfiguration(configFile);
		
		// Load config
		useStronghold = config.getBoolean(path_useStronghold, true);
		allowNetherEnd = config.getBoolean(path_netherEnd, false);
		
		// Print informative message about where ender eyes should lead to
		if (useStronghold) plugin.getLogger().info("Ender eyes will point towards the nearest stronghold.");
		else plugin.getLogger().info("Ender eyes will point towards the nearest defined location to where they were thrown.");
		
		ConfigurationSection locations = config.getConfigurationSection(path_locations);
		if (locations == null)
		{
			plugin.getLogger().warning("Waypoints section missing from config, using stronghold locations instead!");
			useStronghold = true;
			locations = config.createSection(path_locations);
			return;
		}
		plugin.getLocationManager().load(locations);
	}
	
	public void saveConfig()
	{
		// Save config
		config.set(path_useStronghold, useStronghold);
		config.set(path_netherEnd, allowNetherEnd);
		
		// Save locations
		ConfigurationSection locations = config.getConfigurationSection(path_locations);
		if (locations == null) locations = config.createSection(path_locations);
		plugin.getLocationManager().save(locations);
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			plugin.getLogger().warning("An error occured while saving config.");
		}
	}
	
	public boolean getAllowNetherEnd()
	{
		return allowNetherEnd;
	}
	
	public void setAllowNetherEnd(boolean allow)
	{
		allowNetherEnd = allow;
	}
	
	public boolean useStrongholdLocation()
	{
		return useStronghold;
	}
	
	public void setUseStrongholdLocation(boolean use)
	{
		useStronghold = use;
	}
}
package steeveeo.NightSky;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NightSky extends JavaPlugin
{
	//Control Vars
	public PluginManager pluginManager;
	private NightSkyManager skyMan = new NightSkyManager(this);
	
	//Start Logger
	public Logger log = Logger.getLogger("Minecraft");
	
	//Configurable Vars
	public int starFrequency = 3500;
	public int daysPerPhaseChange = 1;
	public String defaultMoonDirURL = "http://dl.dropbox.com/u/39096583/Minecraft/NightSky/";
	
	//Config Handler
	public NightSkyConfigHandler config = new NightSkyConfigHandler(this);
	
	public void onEnable()
	{
		pluginManager = this.getServer().getPluginManager();
		
		//Setup Spout (we only are able to run if Spout is installed)
		if(pluginManager.getPlugin("Spout") != null)
		{
			getServer().getScheduler().scheduleSyncRepeatingTask(this, skyMan, 100, 20);
			
			//Load Configs
			config.configCheck();
			
			//Load Spout Stuff
			SpoutLink.init();

			log.info("[NightSky] - Version 0.1 Started.");
		}
		else
		{
			log.severe("[NightSky] - Unable to start, Spout not installed!");
			
			pluginManager.disablePlugin(this);
		}
	}
	
	public void onDisable()
	{
		log.info("[NightSky] - Version 0.1 Stopped.");
	}

}

package steeveeo.NightSky;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class SpoutLink
{
	private static NightSky plugin = (NightSky) Bukkit.getServer().getPluginManager().getPlugin("NightSky");
	public static NightSkySpoutLoader spoutListener = new NightSkySpoutLoader(plugin);
    public static void init()
    {
    	plugin.pluginManager.registerEvent(Event.Type.CUSTOM_EVENT, spoutListener, Event.Priority.Normal, plugin);
    }
}

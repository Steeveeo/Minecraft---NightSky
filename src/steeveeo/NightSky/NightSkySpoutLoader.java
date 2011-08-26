package steeveeo.NightSky;

import org.bukkit.World;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.player.SkyManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import steeveeo.NightSky.MoonPhase.PhaseType;

public class NightSkySpoutLoader extends SpoutListener
{
	//Constructor
	private NightSky plugin;
	public NightSkySpoutLoader(NightSky instance)
	{
		plugin = instance;
	}
	
	//On Spout Player join, load up all player-based stuff
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event)
	{
		SpoutPlayer player = event.getPlayer();
		World world = player.getWorld();
		SkyManager sky = SpoutManager.getSkyManager();
		
		sky.setStarFrequency(player, plugin.starFrequency);
		
		//If this is a New Moon, don't display
		if(NightSkyManager.getPhase(world) == PhaseType.NEW_MOON)
		{
			sky.setMoonVisible(player, false);
		}
		//Else grab from URL
		else
		{
			String URL = NightSkyManager.getPhaseURL(world, NightSkyManager.getPhase(world));
			sky.setMoonTextureUrl(player, URL);
			sky.setMoonVisible(player, true);
		}
	}
}

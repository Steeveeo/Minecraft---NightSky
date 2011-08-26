package steeveeo.NightSky;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SkyManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import steeveeo.NightSky.MoonPhase.PhaseType;
import steeveeo.NightSky.NightSkyConfigHandler.NightSkyWorldData;

public class NightSkyManager implements Runnable
{
	//Control Vars (SO MANY MAPS!)
	private static HashMap<World,Integer> daysPassed = new HashMap<World,Integer>();
	private static HashMap<World,Integer> curTime = new HashMap<World,Integer>();
	private static HashMap<World,Integer> curPhase = new HashMap<World,Integer>();
	private static HashMap<World,Integer> daysPerPhase = new HashMap<World,Integer>();
	private static HashMap<World,String> moonURLDir = new HashMap<World,String>();
	private static HashMap<World,World> syncedTo = new HashMap<World,World>();
	private List<World> worldList;
	
	private NightSkyWorldTeleportListener teleListener;
	
	//Constructor
	private static NightSky plugin;
	public NightSkyManager(NightSky instance)
	{
		plugin = instance;
		
	}
	
	//Day Ranges
	//--0 Night
	//--1 Dawn
	//--2 Day
	//--3 Dusk
	public static int getTimeOfDay(long time)
	{
		//Night
		if(time > 14000 && time < 22500)
		{
			return 0;
		}
		//Dawn
		else if(time >= 22500 && time <= 24000)
		{
			return 1;
		}
		//Day
		else if(time >= 0 && time < 12000)
		{
			return 2;
		}
		//Dusk
		else if(time >= 12000 && time <= 14000)
		{
			return 3;
		}
		//Warning, bad logic, assume day
		else
		{
			return 2;
		}
	}
	
	//Return phase for other classes to read
	public static PhaseType getPhase(World world)
	{
		if(curPhase.get(world) == null)
		{
			curPhase.put(world, 0);
		}
		
		return MoonPhase.getPhase(curPhase.get(world));
	}
	
	//Return World's specific URL
	public static String getPhaseURL(World world, PhaseType phase)
	{
		if(moonURLDir.get(world) != null)
		{
			return moonURLDir.get(world) + "/MoonPhase" + curPhase.get(world) + ".png";
		}
		else
		{
			return plugin.defaultMoonDirURL + "/MoonPhase" + curPhase.get(world) + ".png";
		}
	}
	
	//Return World's base URL directory
	public static String getURLDir(World world)
	{
		if(moonURLDir.get(world) != null)
		{
			return moonURLDir.get(world);
		}
		else
		{
			return plugin.defaultMoonDirURL;
		}
	}
	
	//Return World's days since last phase change
	public static int getDaysPassed(World world)
	{
		if(daysPassed.get(world) != null)
		{
			return daysPassed.get(world);
		}
		else
		{
			return 0;
		}
	}
	
	//Return World's required days for phase change
	public static int getDaysPerPhase(World world)
	{
		if(daysPerPhase.get(world) != null)
		{
			return daysPerPhase.get(world);
		}
		else
		{
			return plugin.daysPerPhaseChange;
		}
	}
	
	//Return the world that the given world is synced to
	public static World getSyncedWorld(World world)
	{
		return syncedTo.get(world);
	}
	
	//Update Check
	public void run()
	{
		//Doing this here, since worlds only load after the server starts.
		if(worldList == null)
		{
			worldList = plugin.getServer().getWorlds();
		}
		if(teleListener == null)
		{
			//Register event
			teleListener = new NightSkyWorldTeleportListener();
			plugin.pluginManager.registerEvent(Event.Type.PLAYER_TELEPORT, teleListener, Event.Priority.Monitor, plugin);
		}
		
		//Loop through all worlds and update as necessary
		for(World world : worldList)
		{
			long worldTime = world.getTime();
			int timeOfDay = getTimeOfDay(worldTime);
			
			//Update world if anything is null
			if(curTime.get(world) == null || daysPassed.get(world) == null || curPhase.get(world) == null)
			{
				NightSkyWorldData data = plugin.config.loadWorld(world);
				
				//Read in data to world records
				curTime.put(world, getTimeOfDay(world.getTime()));
				daysPassed.put(world, data.daysPassed);
				curPhase.put(world, data.curPhase);
				daysPerPhase.put(world, data.daysPerPhase);
				moonURLDir.put(world, data.moonDir);
				syncedTo.put(world, data.syncedTo);
			}
			
			//Compare against old data, tick timers up if needed
			boolean syncedWorldUpdate = false;
			if(curTime.get(world) != 2 && timeOfDay == 2)
			{
				//Independent updating
				if(syncedTo.get(world) == null)
				{
					//Update Records
					daysPassed.put(world, daysPassed.get(world) + 1);
					plugin.config.saveWorld(world);
				}
				//Sync up with other world
				else
				{
					//Update from other world
					curPhase.put(world, curPhase.get(syncedTo.get(world)));
					daysPassed.put(world, daysPassed.get(syncedTo.get(world)));
					syncedWorldUpdate = true;
				}
				curTime.put(world, timeOfDay);
			}
			else if(curTime.get(world) != timeOfDay)
			{
				curTime.put(world, timeOfDay);
			}
			
			//If we have enough days passed for a new phase, apply change
			if(daysPassed.get(world) >= daysPerPhase.get(world) || syncedWorldUpdate)
			{
				//Independent Updating
				if(syncedTo.get(world) == null)
				{
					//Update Records
					curPhase.put(world, ((curPhase.get(world) + 1) % 8));
					daysPassed.put(world, 0);
					plugin.config.saveWorld(world);
				}
				
				//Notify all Spout users
				List<Player> players = world.getPlayers();
				SkyManager sky = SpoutManager.getSkyManager();
				for(Player player : players)
				{
					SpoutPlayer ply = SpoutManager.getPlayer(player);
					if(ply.isSpoutCraftEnabled())
					{
						//If this is a New Moon, don't display
						if(curPhase.get(world) == 4)
						{
							sky.setMoonVisible(ply, false);
						}
						//Else grab from URL
						else
						{
							String URL = getPhaseURL(world, MoonPhase.getPhase(curPhase.get(world)));
							sky.setMoonTextureUrl(ply, URL);
							sky.setMoonVisible(ply, true);
						}
					}
				}
			}
		}
	}
	
	//Handler to make sure players travelling across worlds get the right moon
	public class NightSkyWorldTeleportListener extends PlayerListener
	{
		public void onPlayerTeleport(PlayerTeleportEvent event)
		{
			Location from = event.getFrom();
			Location to = event.getTo();
			Player player = event.getPlayer();
			SkyManager sky = SpoutManager.getSkyManager();
			if(from.getWorld() != to.getWorld())
			{
				//Send player new moon data
				World world = to.getWorld();
				SpoutPlayer ply = SpoutManager.getPlayer(player);
				if(ply.isSpoutCraftEnabled())
				{
					//If this is a New Moon, don't display
					if(curPhase.get(world) == 4)
					{
						sky.setMoonVisible(ply, false);
					}
					//Else grab from URL
					else
					{
						String URL = getPhaseURL(world, MoonPhase.getPhase(curPhase.get(world)));
						sky.setMoonTextureUrl(ply, URL);
						sky.setMoonVisible(ply, true);
					}
				}
			}
		}
	}
}

package steeveeo.NightSky;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class util
{
	//Grab a random string from a given array.
	public static String randomStringFromList(String[] array)
	{
		String randomString = "";
		
		//Generate random number
		Random generator = new Random();
		int index = generator.nextInt(array.length);
		
		//Get from list
		randomString = array[index];
		
		return randomString;
	}
	
	//Break a string into an array by a delimiter
	public static String[] explode(String in, char delim)
	{
		LinkedList<String> out = new LinkedList<String>();
		char[] input = in.toCharArray();
		
		char inChar;
		String entry = "";
		for(int index = 0; index < in.length(); index++)
		{
			inChar = input[index];
			
			//EOF
			if(inChar == '\0')
			{
				break;
			}
			
			//End Line
			if(inChar == delim)
			{
				out.add(entry);
				entry = "";
			}
			//Else add to current entry
			else
			{
				entry = entry + inChar;
			}
		}
		out.add(entry);
		
		//Empty to array
		String[] output = new String[out.size()];
		for(int index = 0; index < out.size(); index++)
		{
			output[index] = out.get(index);
		}
		
		return output;
	}
	
	//Get player(s) by name stub (accepts wildcard '*' for everyone)
	public static List<Player> findPlayerByName(Server server, String name)
	{
		List<Player> targets = new ArrayList<Player>();
		if(name.equals("*"))
		{
			Player[] onlinePlayers = server.getOnlinePlayers();
			for(int ii = 0; ii < onlinePlayers.length; ii++)
			{
				targets.add(onlinePlayers[ii]);
			}
		}
		else
		{
			targets = server.matchPlayer(name);
		}
		
		return targets;
	}
	
	public static boolean blockIsInteractable(Block block)
	{
		Material[] blacklist = new Material[]{
			Material.BED,
			Material.BURNING_FURNACE,
			Material.CAKE_BLOCK,
			Material.CHEST,
			Material.DIODE_BLOCK_ON,
			Material.DIODE_BLOCK_OFF,
			Material.DISPENSER,
			Material.FURNACE,
			Material.JUKEBOX,
			Material.LEVER,
			Material.SIGN, //For Essentials and Sign Trading stuff
			Material.STONE_BUTTON,
			Material.TRAP_DOOR,
			Material.WALL_SIGN, //Again, for Sign-using stuff compatability
			Material.WOOD_DOOR,
			Material.WOODEN_DOOR,
			Material.WORKBENCH
		};
		Material blockMat = block.getType();
		
		for(int ii = 0; ii < blacklist.length; ii++)
		{
			//Did we find that this block is interactable?
			if(blockMat == blacklist[ii])
			{
				return true;
			}
		}
		
		return false;
	}
	
	//Clamp a number in a range
	public static int clamp(int base, int low, int high)
	{
		int output = base;
		
		output = Math.min(output, high);
		output = Math.max(output, low);
		
		return output;
	}
	public static float clamp(float base, float low, float high)
	{
		float output = base;
		
		output = Math.min(output, high);
		output = Math.max(output, low);
		
		return output;
	}
	public static double clamp(double base, double low, double high)
	{
		double output = base;
		
		output = Math.min(output, high);
		output = Math.max(output, low);
		
		return output;
	}
	public static long clamp(long base, long low, long high)
	{
		long output = base;
		
		output = Math.min(output, high);
		output = Math.max(output, low);
		
		return output;
	}
}

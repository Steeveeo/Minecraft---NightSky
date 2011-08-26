package steeveeo.NightSky;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.World;
import org.bukkit.util.config.Configuration;

//Credit for this goes to JayJay110 on the Bukkit Forums!
//http://forums.bukkit.org/threads/tutorial-create-a-configuration-file-with-yaml.15975/
public class NightSkyConfigHandler
{	
	private static NightSky plugin;
	private String directory = "plugins" + File.separator +"NightSky";
	private File file;
    public NightSkyConfigHandler(NightSky instance)
    {
        plugin = instance;
        
        file = new File(directory + File.separator + "config.yml");
    }


    public void configCheck(){
        new File(directory).mkdir();

        if(!file.exists()){
            try {
                file.createNewFile();
                addDefaults();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

            loadkeys();
        }
    }
    public void write(File writeFile, String root, Object x){
        Configuration config = load(writeFile);
        config.setProperty(root, x);
        config.save();
    }
    public Boolean readBoolean(File readFile, String root){
        Configuration config = load(readFile);
        return config.getBoolean(root, true);
    }
	public Double readDouble(File readFile, String root){
        Configuration config = load(readFile);
        return config.getDouble(root, 0);
    }
	public int readInt(File readFile, String root){
        Configuration config = load(readFile);
        return config.getInt(root, 0);
    }
    public List<String> readStringList(File readFile, String root){
        Configuration config = load(readFile);
        return config.getKeys(root);
    }
    public String readString(File readFile, String root){
        Configuration config = load(readFile);
        return config.getString(root);
    }
    private Configuration load(File readFile){

        try {
            Configuration config = new Configuration(readFile);
            config.load();
            return config;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private void addDefaults(){
        plugin.log.info("[NightSky] Config File Not Found, Generating...");
        
        write(file, "Star Frequency", plugin.starFrequency);
        write(file, "Default Days Between Moon Phases", plugin.daysPerPhaseChange);
        write(file, "Default Moon URL Directory", plugin.defaultMoonDirURL);
        
        loadkeys();
        
        plugin.log.info("[NightSky] Config File Generation Complete.");
    }
    private void loadkeys()
    {
        plugin.starFrequency = readInt(file, "Star Frequency");
        plugin.daysPerPhaseChange = readInt(file, "Default Days Between Moon Phases");
        plugin.defaultMoonDirURL = readString(file, "Default Moon URL Directory");
    }
    
    
    //World Data
    public void saveWorld(World world)
    {
    	//Directory Data
    	String worldDir = directory + File.separator + "Worlds";
    	String worldName = world.getName();
    	File worldCfg = new File(worldDir + File.separator + worldName + ".yml");
    	
    	//Make sure this directory exists
    	new File(worldDir).mkdir();
    	
    	//Get World Data
    	int curPhase = MoonPhase.getPhase(NightSkyManager.getPhase(world));
    	int daysPassed = NightSkyManager.getDaysPassed(world);
    	int daysPerPhase = NightSkyManager.getDaysPerPhase(world);
    	String moonDir = NightSkyManager.getURLDir(world);
    	World syncedWorld = NightSkyManager.getSyncedWorld(world);
    	
    	//Save data
    	write(worldCfg, "Moon Image URL Directory", moonDir);
    	write(worldCfg, "Current Phase", curPhase);
    	write(worldCfg, "Days Passed", daysPassed);
    	write(worldCfg, "Days Between Phases", daysPerPhase);
    	if(syncedWorld != null)
    	{
    		write(worldCfg, "Synced To World", syncedWorld.getName());
    	}
    	else
    	{
    		write(worldCfg, "Synced To World", "n/a");
    	}
    	
    }
    public NightSkyWorldData loadWorld(World world)
    {
    	String worldDir = directory + File.separator + "Worlds";
    	String worldName = world.getName();
    	File worldCfg = new File(worldDir + File.separator + worldName + ".yml");
    	
    	//Make sure target directory has been created
    	new File(worldDir).mkdir();
    	
    	//Config doesn't exist? Make one
    	if(!worldCfg.exists())
    	{
    		try
			{
				worldCfg.createNewFile();
				saveWorld(world);
			}
    		catch (IOException e)
			{
				e.printStackTrace();
			}
    	}
    	
    	//Read in data
    	NightSkyWorldData out = new NightSkyWorldData();
    	out.curPhase = util.clamp(readInt(worldCfg, "Current Phase"), 0, 7);
    	out.daysPassed = readInt(worldCfg, "Days Passed");
    	out.daysPerPhase = Math.max(readInt(worldCfg, "Days Between Phases"), 1);
    	out.moonDir = readString(worldCfg, "Moon Image URL Directory");
    	//--Get synced world, if any
    	String syncedWorldName = readString(worldCfg, "Synced To World");
    	if(syncedWorldName == null || syncedWorldName.equalsIgnoreCase("n/a"))
    	{
    		out.syncedTo = null;
    	}
    	else
    	{
    		World syncedWorld = plugin.getServer().getWorld(syncedWorldName);
    		if(syncedWorld != null)
    		{
    			out.syncedTo = syncedWorld;
    		}
    		else
    		{
    			plugin.log.warning("[NightSky] - Could not link " + world.getName() + " to \"" + syncedWorldName + "\". Defaulting to unlinked.");
    			out.syncedTo = null;
    		}
    	}
    	
    	return out;
    }
    
    //Hurr, using classes as structs, trolling the Java peeps
    //with mah C-based lunacy.
    public class NightSkyWorldData
    {
    	public int curPhase;
    	public int daysPassed;
    	public int daysPerPhase;
    	public String moonDir;
    	public World syncedTo;
    }
}

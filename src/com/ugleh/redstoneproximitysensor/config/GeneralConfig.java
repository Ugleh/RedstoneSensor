package com.ugleh.redstoneproximitysensor.config;

import com.ugleh.redstoneproximitysensor.RedstoneProximitySensor;

import com.ugleh.redstoneproximitysensor.util.Mobs;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GeneralConfig extends YamlConfiguration {
    private boolean use_particles = true;
    private boolean update_checker = true;
    private boolean use_sqlite = false;
    private RedstoneProximitySensor plugin;
    private File file;
    private int max_range = 20;
    private int default_range = 5;
    private boolean default_inverted = false;
    private boolean default_owner_only_edit = true;
    private List<String> supportedEntities = new ArrayList<>();
    private HashMap<String, Boolean> default_triggers = new HashMap<>();
    public HashMap<String, Integer> permissionLimiters = new HashMap<>();

    public GeneralConfig(RedstoneProximitySensor plugin) {
        this.plugin = plugin;
        reloadConfig();


    }
    private void grabSettings() {
        update_checker = plugin.getConfig().getBoolean("config.update_checker", true);
        use_sqlite = plugin.getConfig().getBoolean("config.use_sqlite", false);
        use_particles = plugin.getConfig().getBoolean("sensor_config.use_particles", true);
        max_range = plugin.getConfig().getInt("sensor_config.max_range", 20);
        default_range = plugin.getConfig().getInt("sensor_config.default_range", 5);
        default_inverted = plugin.getConfig().getBoolean("sensor_config.default_inverted", false);
        default_owner_only_edit = plugin.getConfig().getBoolean("sensor_config.default_owner_only_edit", true);

        for (String key : plugin.getConfig().getConfigurationSection("sensor_config.default_triggers").getKeys(true)) {
            if(plugin.getConfig().isBoolean("sensor_config.default_triggers."  + key)) {
                default_triggers.put(key, plugin.getConfig().getBoolean("sensor_config.default_triggers."  + key ));
            }
        }

        for(Mobs mob : Mobs.values()) {
        	supportedEntities.add(mob.getEntityTypeName());
        }
        grabLimitPermissions();

    }
    
    private void grabLimitPermissions()
    {
    	//If it is missing, add a default permission of infinite count to the list.
    	if(!plugin.getConfig().isSet("limiter"))
    	{
        	permissionLimiters.put("limiter.default", -1);
        	plugin.getServer().getPluginManager().addPermission(new Permission("limiter.default", PermissionDefault.TRUE));
        	plugin.getConfig().set("limiter.default.default", true);
        	plugin.getConfig().set("limiter.default.amount", -1);
        	plugin.saveConfig();
    		return;
    	}
        for (String key : plugin.getConfig().getConfigurationSection("limiter").getKeys(true)) {
    		//Check if subkey "default" exists, if it does grab the PermissionDefault of it, if not set to DEFAULT_PERMISSION.
        	PermissionDefault pd = Permission.DEFAULT_PERMISSION;
            	if(plugin.getConfig().isSet("limiter."  + key + ".default"))
            	{
            		pd = PermissionDefault.getByName(plugin.getConfig().getString("limiter."  + key + ".default"));
            		if (pd == null)
            			throw new IllegalArgumentException("'default' key in RedstoneProximitySensor Config contained unknown value");
            	}

            	if(plugin.getServer().getPluginManager().getPermission("limiter."  + key) == null)
            	    plugin.getServer().getPluginManager().addPermission(new Permission("limiter."  + key, pd));
            	
            	//Check if subkey "amount" exists, if it does grab the amount, if not set it to -1 (infinite)
            	int limiterAmount = -1;
            	if(plugin.getConfig().isSet("limiter."  + key + ".amount")) {
            		limiterAmount = plugin.getConfig().getInt("limiter."  + key + ".amount");
            	}
        	permissionLimiters.put("limiter."  + key, limiterAmount);
        }
        
        List<Map.Entry<String, Integer> > list = new LinkedList<>(permissionLimiters.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        
        permissionLimiters = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) { 
        	permissionLimiters.put(aa.getKey(), aa.getValue()); 
        }
        
        if(permissionLimiters.isEmpty())
        {
        	permissionLimiters.put("limiter.default", -1);
        	plugin.getServer().getPluginManager().addPermission(new Permission("limiter.default", Permission.DEFAULT_PERMISSION));
        }
    }

    public void reloadConfig() {
        try {
            if (!plugin.getDataFolder().exists()) {
                boolean createdDirs = plugin.getDataFolder().mkdirs();
                if(!createdDirs) {
                    plugin.getLogger().warning("RedstoneProximitySensor directory could not be created.");
                }
            }
            file = new File(plugin.getDataFolder(), "config.yml");
            if (!file.exists()) {
                plugin.getLogger().info("Config.yml not found, creating!");
                plugin.saveDefaultConfig();
            }
            createDefaults();
            grabSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDefaults() {
        //TODO: Get list of available triggers and create defaults for the flags.
/*
        plugin.getConfig().addDefault("rps.max_range", 20);
        plugin.getConfig().addDefault("rps.use-particles", true);
        plugin.getConfig().addDefault("rps.update-checker", true);
        plugin.getConfig().addDefault("rps.use_sqlite", false);
        plugin.getConfig().addDefault("rps.defaultRange", 5);
        plugin.getConfig().addDefault("rps.defaultownerOnlyEdit", true);
        plugin.getConfig().addDefault("rps.default_inverted", false);
        plugin.getConfig().addDefault("rps.defaultownerOnlyTrigger", false);
        plugin.getConfig().addDefault("rps.defaultPlayerEntityTrigger", true);
        plugin.getConfig().addDefault("rps.defaultHostileEntityTrigger", false);
        plugin.getConfig().addDefault("rps.defaultPeacefulEntityTrigger", false);
        plugin.getConfig().addDefault("defaultNeutralEntityTrigger", false);
        plugin.getConfig().addDefault("rps.defaultDroppedItemsTrigger", false);
        plugin.getConfig().addDefault("rps.defaultInvisibleEntityTrigger", false);
        plugin.getConfig().addDefault("rps.defaultVehcileEntityTrigger", false);
        plugin.getConfig().addDefault("rps.defaultProjectileEntityTrigger", false);
*/

        //plugin.getConfig().options().copyDefaults(false);
        //plugin.saveConfig();
    }

    public boolean isSupportedEntity(EntityType entityType) {
    	return supportedEntities.contains(entityType.name());
    }

    public int getMaxRange() {
        return max_range;
    }

    public int getDefaultRange() {
        return default_range;
    }

    public boolean isDefaultInverted() {
        return default_inverted;
    }

    public RedstoneProximitySensor getPlugin() {
        return plugin;
    }

    public boolean isParticlesEnabled() {
        return use_particles;
    }

    public boolean isUpdateCheckerEnabled() {
        return update_checker;
    }

    public boolean isSqliteEnabled() {
        return use_sqlite;
    }

    public List<String> getSupportedEntities() {
        return supportedEntities;
    }

    public HashMap<String, Boolean> getDefaultTriggers() {
        return default_triggers;
    }

    public HashMap<String, Integer> getPermissionLimiters() {
        return permissionLimiters;
    }

    public boolean isDefaultOwnerOnlyEdit() {
        return default_owner_only_edit;
    }
}
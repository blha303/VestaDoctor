package tk.blha303;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VestaDoctor extends JavaPlugin {
	
	private static final Logger log = Logger.getLogger("Minecraft");
	public static VestaDoctor plugin;
	public static Permission permission = null;
    public static Economy econ = null;
	
    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }
    
	@Override
	public void onEnable() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			log.severe(String.format("[%s] Disabled. Vault is missing!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
        getServer().getPluginManager().registerEvents(new myListener(), this);
		saveConfig();
		setupPermissions();
		setupEconomy();
        log.info(String.format("[%s] Enabled version %s", getDescription().getName(), getDescription().getVersion()));
	}
	
    // http://stackoverflow.com/a/2275030
    public boolean contains(String haystack, String needle) {
    	  haystack = haystack == null ? "" : haystack;
    	  needle = needle == null ? "" : needle;
    	  return haystack.toLowerCase().contains(needle.toLowerCase());
    }
    
    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
    public class myListener implements Listener{
    	
    	@EventHandler
    	public void rightClick(PlayerInteractEntityEvent event) {
    		if (event.getRightClicked() instanceof Player) { 
    			Player player = event.getPlayer();
    			Player other = (Player) event.getRightClicked();
    			if (permission.has(other, getConfig().getString("node"))) {
    				econ.bankWithdraw(player.getName(), getConfig().getInt("medicalfees"));
    				econ.bankDeposit(other.getName(), getConfig().getInt("medicalfees"));
    				player.setHealth(getConfig().getInt("healamount"));
    				player.sendMessage(String.format(getConfig().getString("pheal"), other.getDisplayName()));
    				other.sendMessage(String.format(getConfig().getString("oheal"), player.getDisplayName(), getConfig().getInt("medicalfees")));
    				other.setFoodLevel(other.getFoodLevel() - getConfig().getInt("ofood"));
    			}
    		}
    	}
    	
    }
    
}

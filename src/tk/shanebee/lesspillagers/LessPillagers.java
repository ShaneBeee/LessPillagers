package tk.shanebee.lesspillagers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class LessPillagers extends JavaPlugin implements Listener {

	private int MAX_PILLAGERS;

	@Override
	public void onEnable() {
		log("Loading config!");
		saveDefaultConfig();
		MAX_PILLAGERS = getConfig().getInt("settings.max-pillagers-per-outpost");
		log("Max Pillagers = " + MAX_PILLAGERS);
		log("&aLoaded successfully");
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = ((Player) sender);
			player.sendMessage("Pillagers: " + nearby(player));
		}
		return true;
	}

	private int nearby(Entity entity) {
		List<Entity> entities = entity.getNearbyEntities(100, 100, 100);
		int pillager = 0;
		for (Entity e : entities) {
			if (e.getType() == EntityType.PILLAGER) {
				pillager++;
			}
		}
		return pillager;
	}

	@EventHandler
	private void onPillagerSpawn(CreatureSpawnEvent event) {
		if (event.getEntityType() == EntityType.PILLAGER) {
			if (nearby(event.getEntity()) >= MAX_PILLAGERS) {
				event.setCancelled(true);
			}
		}
	}

	private void log(String message) {
		String prefix = "&7[&3LessPillagers&7] ";
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
	}

}

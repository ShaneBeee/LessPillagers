package tk.shanebee.lesspillagers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LessPillagers extends JavaPlugin implements Listener {

    private int MAX_PILLAGERS, RADIUS_X, RADIUS_Y, RADIUS_Z;

    @Override
    public void onEnable() {
        reloadConfig();

        this.getServer().getPluginManager().registerEvents(this, this);
        log("&aLoaded successfully");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Maybe not necessary, but to be sure unloading all event listeners (especially for plugman reload for example)
        HandlerList.unregisterAll((JavaPlugin) this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "pillager-count":
                if (sender instanceof Player) {
                    Player player = ((Player) sender);
                    player.sendMessage("Pillagers: " + nearby(player));
                } else
                    sender.sendMessage("Don't worry console, you're safe: No pillagers near you!");
                return true;

            case "pillager-reload":
                reloadConfig();
                scm(sender,"&aConfig reloaded.");
                return true;
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        log("Loading config...");
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        ConfigurationSection settings = getConfig().getConfigurationSection("settings");

        MAX_PILLAGERS = settings.getInt("max-pillagers-per-outpost");
        RADIUS_X = Math.max(settings.getInt("pillagers-check-radius.x"), 1);
        RADIUS_Y = Math.max(settings.getInt("pillagers-check-radius.y"), 1);
        RADIUS_Z = Math.max(settings.getInt("pillagers-check-radius.z"), 1);

        log("Max Pillagers: " + MAX_PILLAGERS);
        log("Radius: X=" + RADIUS_X + " / Y=" + RADIUS_Y + " / Z=" + RADIUS_Z);
    }

    @EventHandler
    private void onPillagerSpawn(CreatureSpawnEvent event) {
        if (!event.isCancelled() && event.getEntityType() == EntityType.PILLAGER && nearby(event.getEntity()) >= MAX_PILLAGERS)
            event.setCancelled(true);
    }

    private void scm(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3LessPillagers&7] " + message));
    }
    private void log(String message) {
        scm(Bukkit.getConsoleSender(), message);
    }

    private int nearby(Entity entity) {
        int pillager = 0;

        for (Entity e : entity.getNearbyEntities(RADIUS_X, RADIUS_Y, RADIUS_Z))
            if (e.getType() == EntityType.PILLAGER) pillager++;

        return pillager;
    }

}

package gg.aexert.tNTCartGuard;

import me.chancesd.pvpmanager.player.CombatPlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class TNTCartGuard extends JavaPlugin {

    private static TNTCartGuard instance;

    @Override
    public void onEnable() {
        instance = this;

        if (!getServer().getPluginManager().isPluginEnabled("PvPManager")) {
            getLogger().severe("PvPManager not found! Disabling TNTCartGuard.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new TNTCartListener(this), this);
        getLogger().info("TNTCartGuard enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TNTCartGuard disabled.");
    }

    public static TNTCartGuard getInstance() {
        return instance;
    }
}
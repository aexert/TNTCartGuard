package gg.aexert.tNTCartGuard;

import me.chancesd.pvpmanager.player.CombatPlayer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class TNTCartListener implements Listener {

    private final TNTCartGuard plugin;

    public TNTCartListener(TNTCartGuard plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTNTMinecartPlace(PlayerInteractEvent event) {
        Player placer = event.getPlayer();
        ItemStack item = event.getItem();

        // Check if the item being used is a TNT Minecart
        if (item == null || item.getType() != Material.TNT_MINECART) return;

        // Check if the placer themselves has PvP off
        CombatPlayer placerCombat = CombatPlayer.get(placer);
        if (placerCombat != null && !placerCombat.hasPvPEnabled()) {
            event.setCancelled(true);
            placer.sendMessage("§cYou cannot place TNT Minecarts while your PvP is disabled.");
            return;
        }

        // Check nearby players: if any have PvP off, block placement
        Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
        for (Player nearby : onlinePlayers) {
            if (nearby.equals(placer)) continue;
            if (nearby.getWorld() != placer.getWorld()) continue;

            // Only check players within a relevant radius (e.g. 16 blocks)
            if (nearby.getLocation().distance(placer.getLocation()) > 16) continue;

            CombatPlayer nearbyPlayer = CombatPlayer.get(nearby);
            if (nearbyPlayer != null && !nearbyPlayer.hasPvPEnabled()) {
                event.setCancelled(true);
                placer.sendMessage("§cYou cannot place TNT Minecarts near a player with PvP disabled.");
                return;
            }
        }
    }
}
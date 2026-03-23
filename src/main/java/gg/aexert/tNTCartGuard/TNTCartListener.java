package gg.aexert.tNTCartGuard;

import me.chancesd.pvpmanager.player.CombatPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TNTCartListener implements Listener {

    private final TNTCartGuard plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 500; // 500ms = half a second

    public TNTCartListener(TNTCartGuard plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTNTMinecartPlace(PlayerInteractEvent event) {
        Player placer = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.TNT_MINECART) return;

        // Cooldown check — skip heavy logic if fired too recently
        long now = System.currentTimeMillis();
        Long last = cooldowns.get(placer.getUniqueId());
        if (last != null && now - last < COOLDOWN_MS) {
            event.setCancelled(true); // still cancel during cooldown
            return;
        }
        cooldowns.put(placer.getUniqueId(), now);

        // Check if the placer has PvP off
        CombatPlayer placerCombat = CombatPlayer.get(placer);
        if (placerCombat != null && !placerCombat.hasPvPEnabled()) {
            event.setCancelled(true);
            placer.sendMessage("§cYou cannot place TNT Minecarts while your PvP is disabled.");
            return;
        }

        // Check nearby players
        Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
        for (Player nearby : onlinePlayers) {
            if (nearby.equals(placer)) continue;
            if (nearby.getWorld() != placer.getWorld()) continue;
            if (nearby.getLocation().distance(placer.getLocation()) > 24) continue;

            CombatPlayer nearbyPlayer = CombatPlayer.get(nearby);
            if (nearbyPlayer != null && !nearbyPlayer.hasPvPEnabled()) {
                event.setCancelled(true);
                placer.sendMessage("§cYou cannot place TNT Minecarts near a player with PvP disabled.");
                return;
            }
        }
    }
}

package io.github.aleksandarharalanov.pvptweaks.listener;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import static io.github.aleksandarharalanov.pvptweaks.PvPTweaks.getConfig;
import static io.github.aleksandarharalanov.pvptweaks.util.ColorUtil.translate;
import static io.github.aleksandarharalanov.pvptweaks.util.LoggerUtil.logWarning;
import static org.bukkit.Bukkit.getServer;

public class EntityDamageListener extends EntityListener {

    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        boolean isBowFixEnabled = getConfig().getBoolean("worldguard-fixes.bow-bypass", true);
        if (!isBowFixEnabled) return;

        Entity victim = event.getEntity();
        if (!(victim instanceof Player)) return;

        Player shooter = getShooter(event);
        if (shooter == null) return;

        WorldGuardPlugin worldGuard = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null || !worldGuard.isEnabled()) {
            logWarning("[PvPTweaks] PvP bow fix requires WorldGuard, but it is missing or disabled.");
            return;
        }

        RegionManager regionManager = worldGuard.getGlobalRegionManager().get(shooter.getWorld());
        Vector point = BukkitUtil.toVector(victim.getLocation());
        if (!regionManager.getApplicableRegions(point).allows(DefaultFlag.PVP)) {
            shooter.sendMessage(translate("&4You are in a no-PvP area."));
            event.setCancelled(true);
        }
    }

    private static Player getShooter(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) event;
            Entity damager = damageByEntityEvent.getDamager();
            if (damager instanceof Arrow) {
                Entity shooter = ((Arrow) damager).getShooter();
                if (shooter instanceof Player) return (Player) shooter;
            }
        }
        return null;
    }
}

package io.github.aleksandarharalanov.pvptweaks;

import io.github.aleksandarharalanov.pvptweaks.listener.EntityDamageListener;
import io.github.aleksandarharalanov.pvptweaks.util.ConfigUtil;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static io.github.aleksandarharalanov.pvptweaks.util.LoggerUtil.logInfo;
import static io.github.aleksandarharalanov.pvptweaks.util.UpdateUtil.checkForUpdates;

public class PvPTweaks extends JavaPlugin {

    private static ConfigUtil config;

    @Override
    public void onEnable() {
        checkForUpdates(this, "https://api.github.com/repos/AleksandarHaralanov/PvPTweaks/releases/latest");

        config = new ConfigUtil(this, "config.yml");
        config.loadConfig();

        PluginManager pM = getServer().getPluginManager();
        final EntityDamageListener eDL = new EntityDamageListener();
        pM.registerEvent(Event.Type.ENTITY_DAMAGE, eDL, Event.Priority.Normal, this);

        logInfo(String.format("[%s] v%s Enabled.", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        logInfo(String.format("[%s] v%s Disabled.", getDescription().getName(), getDescription().getVersion()));
    }

    public static ConfigUtil getConfig() {
        return config;
    }
}
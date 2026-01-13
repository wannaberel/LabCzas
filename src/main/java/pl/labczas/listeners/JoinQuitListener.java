package pl.labczas.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.labczas.LabCzas;

public class JoinQuitListener implements Listener {
    
    private final LabCzas plugin;
    
    public JoinQuitListener(LabCzas plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getTimeManager().onPlayerJoin(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTimeManager().onPlayerQuit(event.getPlayer());
    }
}

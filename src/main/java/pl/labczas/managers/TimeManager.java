package pl.labczas.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pl.labczas.LabCzas;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeManager {
    
    private final LabCzas plugin;
    private final DataManager dataManager;
    private BukkitTask timeTask;
    
    // Czas sesji graczy (UUID -> minuty w aktualnej sesji)
    private final Map<UUID, Long> sessionStart;
    
    public TimeManager(LabCzas plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
        this.sessionStart = new HashMap<>();
    }
    
    public void startTimeCounter() {
        int saveInterval = plugin.getConfig().getInt("settings.save-interval", 1);
        
        // Zadanie wykonywane co minutę (1200 ticków = 60 sekund)
        timeTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                // Dodaj 1 minutę do czasu gracza
                dataManager.addPlayerTime(player.getUniqueId(), 1.0);
            }
            
            // Zapis danych zgodnie z interwałem
            if (Bukkit.getCurrentTick() % (saveInterval * 1200) == 0) {
                dataManager.saveData();
            }
        }, 1200L, 1200L); // Start po 1 minucie, powtarzaj co minutę
    }
    
    public void stopTimeCounter() {
        if (timeTask != null) {
            timeTask.cancel();
        }
    }
    
    public void onPlayerJoin(Player player) {
        sessionStart.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    public void onPlayerQuit(Player player) {
        sessionStart.remove(player.getUniqueId());
        // Zapisz dane gracza
        dataManager.saveData();
    }
    
    public void saveAllPlayers() {
        dataManager.saveData();
        plugin.getLogger().info("Zapisano dane wszystkich graczy.");
    }
    
    public String getFormattedTime(UUID uuid) {
        double minutes = dataManager.getPlayerTime(uuid);
        return dataManager.formatTime(minutes);
    }
    
    public double getPlayerTimeInHours(UUID uuid) {
        return dataManager.minutesToHours(dataManager.getPlayerTime(uuid));
    }
}

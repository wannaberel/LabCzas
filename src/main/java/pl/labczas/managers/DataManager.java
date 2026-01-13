package pl.labczas.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.labczas.LabCzas;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {
    
    private final LabCzas plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Double> playerTime;
    
    public DataManager(LabCzas plugin) {
        this.plugin = plugin;
        this.playerTime = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        
        loadData();
    }
    
    private void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Nie można utworzyć pliku playerdata.yml!");
                e.printStackTrace();
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        if (dataConfig.contains("players")) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                double time = dataConfig.getDouble("players." + uuidString + ".time", 0.0);
                playerTime.put(uuid, time);
            }
        }
        
        plugin.getLogger().info("Wczytano dane " + playerTime.size() + " graczy.");
    }
    
    public void saveData() {
        for (Map.Entry<UUID, Double> entry : playerTime.entrySet()) {
            String uuidString = entry.getKey().toString();
            dataConfig.set("players." + uuidString + ".time", entry.getValue());
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Nie można zapisać pliku playerdata.yml!");
            e.printStackTrace();
        }
    }
    
    public double getPlayerTime(UUID uuid) {
        return playerTime.getOrDefault(uuid, 0.0);
    }
    
    public void setPlayerTime(UUID uuid, double time) {
        playerTime.put(uuid, time);
    }
    
    public void addPlayerTime(UUID uuid, double time) {
        double current = getPlayerTime(uuid);
        setPlayerTime(uuid, current + time);
    }
    
    public boolean removePlayerTime(UUID uuid, double time) {
        double current = getPlayerTime(uuid);
        if (current >= time) {
            setPlayerTime(uuid, current - time);
            return true;
        }
        return false;
    }
    
    public String formatTime(double minutes) {
        String format = plugin.getConfig().getString("settings.time-format", "HOURS");
        
        if (format.equalsIgnoreCase("HOURS")) {
            double hours = minutes / 60.0;
            return String.format("%.2f", hours);
        } else {
            return String.format("%.0f", minutes);
        }
    }
    
    public double minutesToHours(double minutes) {
        return minutes / 60.0;
    }
    
    public double hoursToMinutes(double hours) {
        return hours * 60.0;
    }
}

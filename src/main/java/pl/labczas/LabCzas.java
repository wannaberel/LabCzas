package pl.labczas;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.labczas.commands.CzasCommand;
import pl.labczas.commands.CzasAdminCommand;
import pl.labczas.listeners.InventoryListener;
import pl.labczas.listeners.JoinQuitListener;
import pl.labczas.managers.DataManager;
import pl.labczas.managers.TimeManager;
import pl.labczas.managers.ShopManager;

public class LabCzas extends JavaPlugin {
    
    private static LabCzas instance;
    private DataManager dataManager;
    private TimeManager timeManager;
    private ShopManager shopManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Tworzenie plików konfiguracyjnych
        saveDefaultConfig();
        
        // Inicjalizacja managerów
        this.dataManager = new DataManager(this);
        this.timeManager = new TimeManager(this);
        this.shopManager = new ShopManager(this);
        
        // Rejestracja komend
        getCommand("czas").setExecutor(new CzasCommand(this));
        getCommand("czasadmin").setExecutor(new CzasAdminCommand(this));
        
        // Rejestracja listenerów
        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(this), this);
        
        // Uruchomienie licznika czasu
        timeManager.startTimeCounter();
        
        getLogger().info("LabCzas został włączony!");
        getLogger().info("Wersja: " + getDescription().getVersion());
    }
    
    @Override
    public void onDisable() {
        // Zapisanie danych wszystkich graczy
        if (timeManager != null) {
            timeManager.stopTimeCounter();
            timeManager.saveAllPlayers();
        }
        
        getLogger().info("LabCzas został wyłączony!");
    }
    
    public void reloadPluginConfig() {
        reloadConfig();
        shopManager.reloadShop();
        getLogger().info("Konfiguracja została przeładowana!");
    }
    
    // Gettery
    public static LabCzas getInstance() {
        return instance;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public TimeManager getTimeManager() {
        return timeManager;
    }
    
    public ShopManager getShopManager() {
        return shopManager;
    }
    
    public String getPrefix() {
        return getConfig().getString("settings.prefix", "&8[&6LabCzas&8]&7")
            .replace("&", "§");
    }
    
    public String getMessage(String path) {
        return (getPrefix() + " " + getConfig().getString("messages." + path, "Brak wiadomości"))
            .replace("&", "§");
    }
}

package pl.labczas.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.labczas.LabCzas;

import java.util.Arrays;

public class AdminGUI {
    
    private final LabCzas plugin;
    private final Player player;
    private final Inventory inventory;
    
    public AdminGUI(LabCzas plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        
        String title = plugin.getConfig().getString("admin-gui.title", "&8× &4Panel Admina &8×")
            .replace("&", "§");
        int size = plugin.getConfig().getInt("admin-gui.size", 54);
        
        this.inventory = Bukkit.createInventory(null, size, title);
        
        setupGUI();
    }
    
    private void setupGUI() {
        addItem(10, Material.ENDER_EYE, "&a&lPrzeładuj Config", 
            "&7Kliknij aby przeładować", "&7konfigurację pluginu");
        
        addItem(12, Material.CHEST, "&e&lPodgląd Sklepu", 
            "&7Kliknij aby zobaczyć", "&7jak wygląda sklep");
        
        addItem(14, Material.DIAMOND, "&b&lZarządzaj Przedmiotami", 
            "&7Edytuj przedmioty w sklepie", "&7poprzez config.yml");
        
        addItem(16, Material.BOOK, "&6&lStatystyki", 
            "&7Liczba graczy: &eDB",
            "&7Przedmiotów w sklepie: &e" + plugin.getShopManager().getShopItems().size());
        
        addItem(28, Material.CLOCK, "&c&lZarządzaj Czasem Graczy", 
            "&7Dodaj/usuń czas graczom", "&7(W przyszłej wersji)");
        
        addItem(30, Material.WRITABLE_BOOK, "&d&lZapisz Dane", 
            "&7Ręcznie zapisz dane", "&7wszystkich graczy");
        
        addItem(32, Material.PAPER, "&f&lPomoc", 
            "&7Edytuj przedmioty w pliku:", 
            "&econfig.yml &7w sekcji", 
            "&eshop-items",
            "",
            "&7Po zmianach użyj opcji",
            "&aPrzeładuj Config");
        
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, glass);
            }
        }
    }
    
    private void addItem(int slot, Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(name.replace("&", "§"));
            
            if (lore.length > 0) {
                meta.setLore(Arrays.stream(lore)
                    .map(line -> line.replace("&", "§"))
                    .toList());
            }
            
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            
            item.setItemMeta(meta);
        }
        
        inventory.setItem(slot, item);
    }
    
    public void open() {
        player.openInventory(inventory);
    }
    
    public Inventory getInventory() {
        return inventory;
    }
}

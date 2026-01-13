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
import pl.labczas.managers.ShopManager;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {
    
    private final LabCzas plugin;
    private final Player player;
    private final Inventory inventory;
    
    public ShopGUI(LabCzas plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        
        String title = plugin.getConfig().getString("gui.title", "&8× &6Sklep za Czas &8×")
            .replace("&", "§");
        int size = plugin.getConfig().getInt("gui.size", 54);
        
        this.inventory = Bukkit.createInventory(null, size, title);
        
        setupGUI();
    }
    
    private void setupGUI() {
        if (plugin.getConfig().getBoolean("gui.decoration.enabled", true)) {
            addDecoration();
        }
        
        addBalanceItem();
        addShopItems();
    }
    
    private void addDecoration() {
        String materialName = plugin.getConfig().getString("gui.decoration.material", "BLACK_STAINED_GLASS_PANE");
        Material material = Material.getMaterial(materialName);
        String name = plugin.getConfig().getString("gui.decoration.name", " ").replace("&", "§");
        List<Integer> slots = plugin.getConfig().getIntegerList("gui.decoration.slots");
        
        ItemStack decoration = new ItemStack(material);
        ItemMeta meta = decoration.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            decoration.setItemMeta(meta);
        }
        
        for (int slot : slots) {
            if (slot < inventory.getSize()) {
                inventory.setItem(slot, decoration);
            }
        }
    }
    
    private void addBalanceItem() {
        int slot = plugin.getConfig().getInt("gui.balance-item.slot", 4);
        String materialName = plugin.getConfig().getString("gui.balance-item.material", "CLOCK");
        Material material = Material.getMaterial(materialName);
        String name = plugin.getConfig().getString("gui.balance-item.name", "&6&lTwój Czas")
            .replace("&", "§");
        List<String> loreTemplate = plugin.getConfig().getStringList("gui.balance-item.lore");
        boolean glow = plugin.getConfig().getBoolean("gui.balance-item.glow", false);
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(name);
            
            String playerTime = plugin.getTimeManager().getFormattedTime(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            for (String line : loreTemplate) {
                lore.add(line.replace("&", "§").replace("{time}", playerTime));
            }
            meta.setLore(lore);
            
            if (glow) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            
            item.setItemMeta(meta);
        }
        
        inventory.setItem(slot, item);
    }
    
    private void addShopItems() {
        for (ShopManager.ShopItem shopItem : plugin.getShopManager().getShopItems().values()) {
            ItemStack item = new ItemStack(shopItem.getMaterial(), shopItem.getAmount());
            ItemMeta meta = item.getItemMeta();
            
            if (meta != null) {
                meta.setDisplayName(shopItem.getName());
                
                List<String> lore = new ArrayList<>();
                for (String line : shopItem.getLore()) {
                    lore.add(line.replace("&", "§")
                        .replace("{price}", String.format("%.2f", shopItem.getPrice())));
                }
                meta.setLore(lore);
                
                if (shopItem.hasGlow()) {
                    meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                
                item.setItemMeta(meta);
            }
            
            inventory.setItem(shopItem.getSlot(), item);
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
    
    public Inventory getInventory() {
        return inventory;
    }
}

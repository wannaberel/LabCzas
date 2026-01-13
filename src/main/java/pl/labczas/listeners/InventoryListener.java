package pl.labczas.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import pl.labczas.LabCzas;
import pl.labczas.gui.AdminGUI;
import pl.labczas.gui.ShopGUI;
import pl.labczas.managers.ShopManager;

public class InventoryListener implements Listener {
    
    private final LabCzas plugin;
    
    public InventoryListener(LabCzas plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        String title = event.getView().getTitle();
        
        // Sprawdź czy to GUI sklepu
        String shopTitle = plugin.getConfig().getString("gui.title", "&8× &6Sklep za Czas &8×")
            .replace("&", "§");
        
        if (title.equals(shopTitle)) {
            event.setCancelled(true);
            handleShopClick(player, event.getSlot());
            return;
        }
        
        // Sprawdź czy to GUI admina
        String adminTitle = plugin.getConfig().getString("admin-gui.title", "&8× &4Panel Admina &8×")
            .replace("&", "§");
        
        if (title.equals(adminTitle)) {
            event.setCancelled(true);
            handleAdminClick(player, event.getSlot());
            return;
        }
    }
    
    private void handleShopClick(Player player, int slot) {
        // Sprawdź czy to slot balansu
        int balanceSlot = plugin.getConfig().getInt("gui.balance-item.slot", 4);
        if (slot == balanceSlot) {
            // Odśwież GUI
            player.closeInventory();
            ShopGUI gui = new ShopGUI(plugin, player);
            gui.open();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            return;
        }
        
        // Sprawdź czy to przedmiot ze sklepu
        ShopManager.ShopItem shopItem = plugin.getShopManager().getShopItem(slot);
        if (shopItem != null) {
            boolean success = plugin.getShopManager().purchaseItem(player, shopItem);
            
            if (success) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                // Odśwież GUI
                player.closeInventory();
                ShopGUI gui = new ShopGUI(plugin, player);
                gui.open();
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    }
    
    private void handleAdminClick(Player player, int slot) {
        switch (slot) {
            case 10: // Przeładuj config
                plugin.reloadPluginConfig();
                player.sendMessage(plugin.getMessage("reload-success"));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.closeInventory();
                break;
                
            case 12: // Podgląd sklepu
                player.closeInventory();
                ShopGUI shopGUI = new ShopGUI(plugin, player);
                shopGUI.open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                break;
                
            case 14: // Zarządzaj przedmiotami
                player.sendMessage(plugin.getPrefix() + " §eEdytuj przedmioty w §6config.yml §ew sekcji §6shop-items");
                player.sendMessage(plugin.getPrefix() + " §eNastępnie użyj §a/czasadmin reload");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                break;
                
            case 16: // Statystyki
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                break;
                
            case 28: // Zarządzaj czasem graczy
                player.sendMessage(plugin.getPrefix() + " §cFunkcja w przyszłej wersji!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                break;
                
            case 30: // Zapisz dane
                plugin.getTimeManager().saveAllPlayers();
                player.sendMessage(plugin.getMessage("config-saved"));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                break;
                
            case 32: // Pomoc
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                break;
        }
    }
}

package pl.labczas.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.labczas.LabCzas;

import java.util.*;

public class ShopManager {
    
    private final LabCzas plugin;
    private Map<Integer, ShopItem> shopItems;
    
    public ShopManager(LabCzas plugin) {
        this.plugin = plugin;
        loadShopItems();
    }
    
    public void loadShopItems() {
        shopItems = new HashMap<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("shop-items");
        
        if (section == null) {
            plugin.getLogger().warning("Nie znaleziono sekcji 'shop-items' w config.yml!");
            return;
        }
        
        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            if (itemSection == null || !itemSection.getBoolean("enabled", true)) continue;
            
            int slot = itemSection.getInt("slot");
            Material material = Material.getMaterial(itemSection.getString("material", "STONE"));
            int amount = itemSection.getInt("amount", 1);
            String name = itemSection.getString("name", "&7Item").replace("&", "§");
            List<String> lore = itemSection.getStringList("lore");
            boolean glow = itemSection.getBoolean("glow", false);
            double price = itemSection.getDouble("price", 1.0);
            String rewardType = itemSection.getString("reward-type", "ITEM");
            
            ShopItem shopItem = new ShopItem(key, slot, material, amount, name, lore, glow, 
                                            price, rewardType, itemSection);
            shopItems.put(slot, shopItem);
        }
        
        plugin.getLogger().info("Załadowano " + shopItems.size() + " przedmiotów do sklepu.");
    }
    
    public void reloadShop() {
        shopItems.clear();
        loadShopItems();
    }
    
    public Map<Integer, ShopItem> getShopItems() {
        return shopItems;
    }
    
    public ShopItem getShopItem(int slot) {
        return shopItems.get(slot);
    }
    
    public boolean purchaseItem(Player player, ShopItem item) {
        UUID uuid = player.getUniqueId();
        double playerTime = plugin.getTimeManager().getPlayerTimeInHours(uuid);
        double price = item.getPrice();
        
        if (playerTime < price) {
            double needed = price - playerTime;
            String msg = plugin.getMessage("not-enough-time")
                .replace("{needed}", String.format("%.2f", needed));
            player.sendMessage(msg);
            return false;
        }
        
        boolean success = plugin.getDataManager().removePlayerTime(uuid, 
                         plugin.getDataManager().hoursToMinutes(price));
        
        if (!success) {
            player.sendMessage(plugin.getMessage("purchase-error"));
            return false;
        }
        
        giveReward(player, item);
        
        String msg = plugin.getMessage("purchase-success")
            .replace("{item}", item.getName())
            .replace("{price}", String.format("%.2f", price));
        player.sendMessage(msg);
        
        plugin.getDataManager().saveData();
        
        return true;
    }
    
    private void giveReward(Player player, ShopItem item) {
        switch (item.getRewardType().toUpperCase()) {
            case "ITEM":
                giveItemReward(player, item);
                break;
            case "COMMAND":
                executeCommandReward(player, item);
                break;
            case "MONEY":
                giveMoneyReward(player, item);
                break;
        }
    }
    
    private void giveItemReward(Player player, ShopItem item) {
        ConfigurationSection rewardSection = item.getConfig().getConfigurationSection("reward-item");
        if (rewardSection == null) return;
        
        Material material = Material.getMaterial(rewardSection.getString("material", "STONE"));
        int amount = rewardSection.getInt("amount", 1);
        
        ItemStack rewardItem = new ItemStack(material, amount);
        ItemMeta meta = rewardItem.getItemMeta();
        
        if (meta != null) {
            if (rewardSection.contains("display-name")) {
                meta.setDisplayName(rewardSection.getString("display-name").replace("&", "§"));
            }
            
            if (rewardSection.contains("lore")) {
                List<String> lore = new ArrayList<>();
                for (String line : rewardSection.getStringList("lore")) {
                    lore.add(line.replace("&", "§"));
                }
                meta.setLore(lore);
            }
            
            if (rewardSection.contains("enchantments")) {
                ConfigurationSection enchants = rewardSection.getConfigurationSection("enchantments");
                for (String enchantName : enchants.getKeys(false)) {
                    Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());
                    if (enchant != null) {
                        int level = enchants.getInt(enchantName);
                        meta.addEnchant(enchant, level, true);
                    }
                }
            }
            
            rewardItem.setItemMeta(meta);
        }
        
        player.getInventory().addItem(rewardItem);
    }
    
    private void executeCommandReward(Player player, ShopItem item) {
        List<String> commands = item.getConfig().getStringList("reward-commands");
        for (String command : commands) {
            String finalCommand = command.replace("{player}", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
        }
    }
    
    private void giveMoneyReward(Player player, ShopItem item) {
        double money = item.getConfig().getDouble("reward-money", 0);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
            "eco give " + player.getName() + " " + money);
    }
    
    public static class ShopItem {
        private final String id;
        private final int slot;
        private final Material material;
        private final int amount;
        private final String name;
        private final List<String> lore;
        private final boolean glow;
        private final double price;
        private final String rewardType;
        private final ConfigurationSection config;
        
        public ShopItem(String id, int slot, Material material, int amount, String name, 
                       List<String> lore, boolean glow, double price, String rewardType,
                       ConfigurationSection config) {
            this.id = id;
            this.slot = slot;
            this.material = material;
            this.amount = amount;
            this.name = name;
            this.lore = lore;
            this.glow = glow;
            this.price = price;
            this.rewardType = rewardType;
            this.config = config;
        }
        
        public String getId() { return id; }
        public int getSlot() { return slot; }
        public Material getMaterial() { return material; }
        public int getAmount() { return amount; }
        public String getName() { return name; }
        public List<String> getLore() { return lore; }
        public boolean hasGlow() { return glow; }
        public double getPrice() { return price; }
        public String getRewardType() { return rewardType; }
        public ConfigurationSection getConfig() { return config; }
    }
}

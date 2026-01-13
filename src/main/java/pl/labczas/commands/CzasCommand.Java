package pl.labczas.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.labczas.LabCzas;
import pl.labczas.gui.ShopGUI;

public class CzasCommand implements CommandExecutor {
    
    private final LabCzas plugin;
    
    public CzasCommand(LabCzas plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("labczas.use")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        // Otwórz GUI sklepu
        ShopGUI gui = new ShopGUI(plugin, player);
        gui.open();
        
        return true;
    }
}

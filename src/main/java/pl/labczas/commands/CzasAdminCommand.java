package pl.labczas.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.labczas.LabCzas;
import pl.labczas.gui.AdminGUI;

public class CzasAdminCommand implements CommandExecutor {
    
    private final LabCzas plugin;
    
    public CzasAdminCommand(LabCzas plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("labczas.admin")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        // Jeśli są argumenty
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadPluginConfig();
                player.sendMessage(plugin.getMessage("reload-success"));
                return true;
            }
        }
        
        // Otwórz GUI admina
        AdminGUI gui = new AdminGUI(plugin, player);
        gui.open();
        
        return true;
    }
}

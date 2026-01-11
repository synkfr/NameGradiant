package com.gradientcolor.namegradient.commands;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.gui.GradientMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GradientCommand implements CommandExecutor {

    private final NameGradient plugin;

    public GradientCommand(NameGradient plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("namegradient.use")) {
            player.sendMessage(plugin.getMessagesConfig().getMessage("no_permission"));
            return true;
        }

        // Open the gradient selection menu
        GradientMenu menu = new GradientMenu(plugin, player, 0);
        menu.open();

        return true;
    }
}

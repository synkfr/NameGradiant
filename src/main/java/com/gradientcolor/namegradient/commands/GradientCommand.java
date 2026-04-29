package com.gradientcolor.namegradient.commands;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.gui.GradientMenu;
import com.gradientcolor.namegradient.model.Gradient;
import com.gradientcolor.namegradient.util.GradientHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GradientCommand implements CommandExecutor, TabCompleter {

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

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "menu":
                handleMenu(player);
                break;
            case "create":
                handleCreate(player, args);
                break;
            default:
                sendUsage(player);
                break;
        }

        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage("§7Usage:");
        player.sendMessage("§7/gradient menu §8- §fOpen the gradient menu");
        if (player.hasPermission("namegradient.create")) {
            player.sendMessage("§7/gradient create <id> <name> <startColour> [endColour] §8- §fCreate a custom color");
        }
    }

    private void handleMenu(Player player) {
        if (!player.hasPermission("namegradient.use")) {
            player.sendMessage(plugin.getMessagesConfig().getMessage("no_permission"));
            return;
        }
        new GradientMenu(plugin, player, 0).open();
    }

    private void handleCreate(Player player, String[] args) {
        if (!player.hasPermission("namegradient.create")) {
            player.sendMessage(plugin.getMessagesConfig().getMessage("no_permission"));
            return;
        }

        if (args.length < 4) {
            player.sendMessage("§cUsage: /gradient create <id> <name> <startColour> [endColour]");
            return;
        }

        try {
            int id = Integer.parseInt(args[1]);
            String name = args[2];
            String startColor = args[3];
            String endColor = (args.length >= 5) ? args[4] : startColor;

            // Validate hex colors (simple check)
            if (!startColor.startsWith("#") || startColor.length() != 7) {
                player.sendMessage("§cInvalid start colour format! Use #RRGGBB");
                return;
            }
            if (!endColor.startsWith("#") || endColor.length() != 7) {
                player.sendMessage("§cInvalid end colour format! Use #RRGGBB");
                return;
            }

            Gradient gradient = new Gradient(id, name, startColor, endColor, null, null, 0);
            plugin.getPlayerDataManager().addCustomGradient(player.getUniqueId(), gradient);
            
            player.sendMessage("§aSuccessfully created custom color: " + name);
            player.sendMessage("§7Start: " + startColor + " §8| §7End: " + endColor);

        } catch (NumberFormatException e) {
            player.sendMessage("§cID must be an integer!");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(Arrays.asList("menu"));
            if (sender.hasPermission("namegradient.create")) {
                subs.add("create");
            }
            return subs.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

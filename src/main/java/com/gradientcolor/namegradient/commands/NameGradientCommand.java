package com.gradientcolor.namegradient.commands;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.config.okaeri.MessagesConfig;
import com.gradientcolor.namegradient.model.Gradient;
import com.gradientcolor.namegradient.util.GradientHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NameGradientCommand implements CommandExecutor, TabCompleter {

    private final NameGradient plugin;

    public NameGradientCommand(NameGradient plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
            case "clear":
                handleClear(sender, args);
                break;
            case "change":
                handleChange(sender, args);
                break;
            default:
                sendUsage(sender);
                break;
        }

        return true;
    }

    private MessagesConfig messages() {
        return plugin.getMessagesConfig();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(messages().getMessage("args_incorrect"));
        sender.sendMessage("§7Usage:");
        sender.sendMessage("§7/namegradient reload §8- §fReload configuration");
        sender.sendMessage("§7/namegradient clear <player> §8- §fClear a player's gradient");
        sender.sendMessage("§7/namegradient change <player> <gradient_id> §8- §fChange a player's gradient");
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("namegradient.reload")) {
            sender.sendMessage(messages().getMessage("no_permission"));
            return;
        }

        try {
            plugin.reload();
            sender.sendMessage(messages().getMessage("config_reload_success"));
        } catch (Exception e) {
            sender.sendMessage(messages().getMessage("config_reload_error"));
            e.printStackTrace();
        }
    }

    private void handleClear(CommandSender sender, String[] args) {
        if (!sender.hasPermission("namegradient.others.clear")) {
            sender.sendMessage(messages().getMessage("no_permission"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(messages().getMessage("args_incorrect"));
            return;
        }

        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(messages().getMessage("target_offline"));
            return;
        }

        // Check if sender is trying to clear their own gradient
        if (sender instanceof Player && ((Player) sender).getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(messages().getMessage("attempt_clear_own"));
            return;
        }

        // Clear the target's gradient
        plugin.getPlayerDataManager().clearPlayerGradient(target.getUniqueId());
        GradientHelper.updatePlayerName(plugin, target);

        sender.sendMessage(messages().getMessage("clear_success"));
        target.sendMessage(messages().getMessage("clear_target_success"));
    }

    private void handleChange(CommandSender sender, String[] args) {
        if (!sender.hasPermission("namegradient.others.change")) {
            sender.sendMessage(messages().getMessage("no_permission"));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(messages().getMessage("args_incorrect"));
            return;
        }

        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(messages().getMessage("target_offline"));
            return;
        }

        // Check if sender is trying to change their own gradient
        if (sender instanceof Player && ((Player) sender).getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(messages().getMessage("attempt_change_own"));
            return;
        }

        int gradientId;
        try {
            gradientId = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(messages().getMessage("args_incorrect"));
            return;
        }

        Gradient gradient = plugin.getGradientsConfig().getGradient(gradientId);
        if (gradient == null) {
            sender.sendMessage(messages().getMessage("gradient_nonexistent"));
            return;
        }

        // Check if target has permission to use this gradient
        if (!gradient.hasPermission(target)) {
            sender.sendMessage(messages().getMessage("target_permission_error"));
            return;
        }

        // Set the target's gradient
        plugin.getPlayerDataManager().setPlayerGradient(target.getUniqueId(), gradientId);
        GradientHelper.updatePlayerName(plugin, target);

        sender.sendMessage(messages().getMessage("change_success"));
        target.sendMessage(messages().getMessage("change_target_success"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();
            if (sender.hasPermission("namegradient.reload")) {
                subCommands.add("reload");
            }
            if (sender.hasPermission("namegradient.others.clear")) {
                subCommands.add("clear");
            }
            if (sender.hasPermission("namegradient.others.change")) {
                subCommands.add("change");
            }
            return subCommands.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("change")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("change")) {
            Collection<Integer> gradientIds = plugin.getGradientsConfig().getGradientIds();
            return gradientIds.stream()
                    .map(String::valueOf)
                    .filter(id -> id.startsWith(args[2]))
                    .collect(Collectors.toList());
        }

        return completions;
    }
}

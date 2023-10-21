package pl.lumberjack.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.lumberjack.LumberJack;
import pl.lumberjack.data.DataHandler;

public class Commands implements CommandExecutor {

    private final LumberJack plugin = LumberJack.getInstance();
    private final DataHandler dataHandler = plugin.getDataHandler();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(!sender.hasPermission(plugin.getPermissionManager().getPermission("lumberjack.manage"))) {
                sender.sendMessage("§cYou don't have permission!");
                return true;
            }
        }
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("reload")) {
                dataHandler.loadConfig();
                sender.sendMessage("§aReloaded!");
                return true;
            }
        }
        sender.sendMessage("§2<---------- §6LumberJack §2---------->");
        sender.sendMessage(" ");
        sender.sendMessage("§6/lj reload - §2Reload configuration");
        sender.sendMessage("§6Max tree height: §2" + dataHandler.getTreeMaxHeight());
        sender.sendMessage("§6Wood destroy interval: §2" + dataHandler.getWoodDestroyInterval() + " ticks");
        sender.sendMessage("§6Leaves destroy interval: §2" + dataHandler.getLeavesDestroyInterval() + " ticks");
        sender.sendMessage("§6Wood range: §2" + dataHandler.getWoodRange());
        sender.sendMessage("§6Leaves range: §2" + dataHandler.getLeavesRange());
        sender.sendMessage("§6Required leaves: §2" + dataHandler.getRequiredLeaves());
        sender.sendMessage("§6Permission to use: §2" + dataHandler.getPermission());
        String axes = dataHandler.getAxes().get(0).toString();
        int i = 0;
        for(Material axe : dataHandler.getAxes()) {
            if(i == 0) {
                i++;
                continue;
            }
            axes += "§e, §2" + axe.toString();
        }
        sender.sendMessage("§6Axes: §2" + axes);
        sender.sendMessage("");
        sender.sendMessage("§2<---------- §6LumberJack §2---------->");
        return true;
    }
}

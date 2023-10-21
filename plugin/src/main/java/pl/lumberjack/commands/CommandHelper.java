package pl.lumberjack.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.lumberjack.LumberJack;

import java.util.ArrayList;
import java.util.List;

public class CommandHelper implements TabCompleter {

    private final LumberJack plugin = LumberJack.getInstance();

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if(sender.hasPermission(plugin.getPermissionManager().getPermission("lumberjack.manage"))) {
            completions.add("reload");
        }
        return completions;
    }

}

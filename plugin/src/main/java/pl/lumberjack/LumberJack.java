package pl.lumberjack;

import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import pl.lumberjack.commands.CommandHelper;
import pl.lumberjack.commands.Commands;
import pl.lumberjack.data.DataHandler;
import pl.lumberjack.events.Events;
import pl.lumberjack.permissions.PermissionManager;

@Getter
public final class LumberJack extends JavaPlugin {

    private static LumberJack main;
    private PermissionManager permissionManager;
    private DataHandler dataHandler;

    @Override
    public void onEnable() {
        main = this; // Set main instance
        this.dataHandler = new DataHandler();
        dataHandler.loadConfig(); // Load config
        this.permissionManager = new PermissionManager(); // Register permissions
        getServer().getPluginManager().registerEvents(new Events(), this); // Register events
        PluginCommand command = getCommand("lumberjack");
        if(command != null) {
            command.setExecutor(new Commands()); // Register command
            command.setTabCompleter(new CommandHelper()); // Register command helper (tab completer)
        }
        getLogger().info("Loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Bye!");
    }

    public static LumberJack getInstance() {
        return main;
    }

}

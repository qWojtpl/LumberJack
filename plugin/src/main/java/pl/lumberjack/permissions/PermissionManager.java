package pl.lumberjack.permissions;

import lombok.Getter;
import org.bukkit.permissions.Permission;
import pl.lumberjack.LumberJack;

import java.util.HashMap;

@Getter
public class PermissionManager {

    private final HashMap<String, Permission> permissions = new HashMap<>();

    public PermissionManager() {
        registerPermission(LumberJack.getInstance().getDataHandler().getPermission(), "Use LumberJack feature");
        registerPermission("lumberjack.manage", "Manage LumberJack plugin");
    }

    public void registerPermission(String permission, String description) {
        permissions.put(permission, new Permission(permission, description));
    }

    public Permission getPermission(String permission) {
        return permissions.getOrDefault(permission, null);
    }

}

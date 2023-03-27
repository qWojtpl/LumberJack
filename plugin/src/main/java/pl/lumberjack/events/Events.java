package pl.lumberjack.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.lumberjack.LumberJack;

public class Events implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if(event.isCancelled()) return;
        if(!event.getPlayer().hasPermission(LumberJack.getInstance().getPermissionManager().
                getPermission(LumberJack.getInstance().getDataHandler().getPermission()))) return; // Check if player has permission
        if(!event.getBlock().getType().toString().toLowerCase().contains("_log")) return; // Check if block is log
        boolean found = false; // Check if found axe
        for(Material axe : LumberJack.getInstance().getDataHandler().getAxes()) {
            if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(axe)) {
                found = true;
                break;
            }
        }
        if(!found) return;
        event.setCancelled(true); // Cancel event
        Location loc = event.getBlock().getLocation(); // Get location of block
        String block = event.getBlock().getType().toString(); // Get log
        block = block.toLowerCase().replace("_log", "_leaves"); // Get this log leaves
        Material leavesMaterial = Material.getMaterial(block.toUpperCase()); // Get leaves (as material)
        new TreeDestroy(event.getPlayer(), event.getBlock().getType(), leavesMaterial, loc, event); // Create tree destroy
    }
}

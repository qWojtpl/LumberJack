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
        boolean isNether = false;
        if(!event.getBlock().getType().toString().toLowerCase().contains("_log")) { // Check if block is a log
            if(event.getBlock().getType().equals(Material.CRIMSON_STEM) || event.getBlock().getType().equals(Material.WARPED_STEM)) {
                isNether = true;
            } else {
                return;
            }
        }
        boolean found = false; // Check if found axe
        for(Material axe : LumberJack.getInstance().getDataHandler().getAxes()) {
            if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(axe)) {
                found = true;
                break;
            }
        }
        if(!found) return;
        Location loc = event.getBlock().getLocation(); // Get location of block
        Material leavesMaterial;
        String block;
        if(!isNether) {
            block = event.getBlock().getType().toString(); // Get log
            block = block.toLowerCase().replace("_log", "_leaves"); // Get this log leaves
            leavesMaterial = Material.getMaterial(block.toUpperCase()); // Get leaves (as material)
            if(leavesMaterial == null) return;
        } else {
            if(event.getBlock().getType().equals(Material.WARPED_STEM)) {
                leavesMaterial = Material.WARPED_WART_BLOCK;
            } else if(event.getBlock().getType().equals(Material.CRIMSON_STEM)) {
                leavesMaterial = Material.NETHER_WART_BLOCK;
            } else {
                return;
            }
        }
        event.setCancelled(true); // Cancel event
        new TreeDestroy(event.getPlayer(), event.getBlock().getType(), leavesMaterial, loc, event, isNether); // Create tree destroy
    }
}

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
                getPermission(LumberJack.getInstance().getDataHandler().getPermission()))) return;
        if(!event.getBlock().getType().toString().toLowerCase().contains("_log")) return;
        boolean found = false;
        for(Material axe : LumberJack.getInstance().getDataHandler().getAxes()) {
            if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(axe)) {
                found = true;
                break;
            }
        }
        if(!found) return;
        event.setCancelled(true);
        Location loc = event.getBlock().getLocation();
        String block = event.getBlock().getType().toString();
        block = block.toLowerCase().replace("_log", "_leaves");
        Material leavesMaterial = Material.getMaterial(block.toUpperCase());
        new TreeDestroy(event.getPlayer(), event.getBlock().getType(), leavesMaterial, loc, event);
    }
}

package pl.lumberjack.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.lumberjack.LumberJack;

import java.util.ArrayList;
import java.util.List;

public class TreeDestroy {

    private final Player player;
    private final Material material;
    private final Material leavesMaterial;
    private final BlockBreakEvent event;
    private int woodTask;
    private int leavesTask;
    private final List<Location> woods = new ArrayList<>();
    private final List<Location> leaves = new ArrayList<>();

    public TreeDestroy(Player player, Material material, Material leavesMaterial, Location location, BlockBreakEvent event) {
        this.player = player;
        this.material = material;
        this.leavesMaterial = leavesMaterial;
        this.event = event;
        checkBlock(location);
    }

    public void checkBlock(Location location) {
        Location startLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        int totalLeaves = 0;
        int totalWood = 0;
        int requiredLeaves = LumberJack.getInstance().getDataHandler().getRequiredLeaves();
        for(int i = 0; i < LumberJack.getInstance().getDataHandler().getTreeMaxHeight(); i++) {
            location = new Location(startLocation.getWorld(), startLocation.getX(), startLocation.getY()+i, startLocation.getZ());
            double startX = location.getX();
            double startZ = location.getZ();
            int range = LumberJack.getInstance().getDataHandler().getWoodRange();
            for(int j = range * -1; j <= range; j++) {
                for(int k = range * -1; k <= range; k++) {
                    location.setX(startX + j);
                    location.setZ(startZ + k);
                    if (location.getBlock().getType().equals(material)) {
                        woods.add(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()));
                        totalWood++;
                    }
                }
            }
            if(leavesMaterial != null) {
                range = LumberJack.getInstance().getDataHandler().getLeavesRange();
                for (int j = range * -1; j <= range; j++) {
                    for (int k = range * -1; k <= range; k++) {
                        location.setX(startX + j);
                        location.setZ(startZ + k);
                        if (location.getBlock().getType().equals(leavesMaterial)) {
                            leaves.add(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()));
                            totalLeaves++;
                        }
                    }
                }
            } else {
                totalLeaves = requiredLeaves;
            }
        }
        if(totalLeaves >= requiredLeaves) {
            ItemStack axe = player.getInventory().getItemInMainHand();
            if (!axe.getItemMeta().isUnbreakable() && axe.getType().getMaxDurability() != 0) {
                int leavesReduction = LumberJack.getInstance().getDataHandler().getLeavesReduction();
                int itemDurability = axe.getType().getMaxDurability() - axe.getDurability(); // Get item durability (eg. 1500)
                int woodLeavesUse = totalWood + (totalLeaves / leavesReduction);
                int uses = axe.getDurability() + woodLeavesUse; // Get item uses + new uses
                if (itemDurability <= woodLeavesUse) { // Check if durability can handle wood and leaves
                    if(itemDurability <= totalWood) { // If not check if can handle only wood
                        event.setCancelled(false); // If not, cancel event
                        return;
                    } else { // If can handle only wood, remove leaves from uses
                        leaves.clear();
                        uses -= (totalLeaves / leavesReduction);
                    }
                }
                int newUses = uses;
                int enchantmentLevel = axe.getItemMeta().getEnchantLevel(Enchantment.DURABILITY);
                if (enchantmentLevel > 0) {
                    for(int i = 0; i < woodLeavesUse; i++) {
                        int chance = 100 / (enchantmentLevel + 1);
                        int random = (int) Math.floor(Math.random() * (100 + 1));
                        if (random < chance) {
                            newUses--;
                        }
                    }
                }
                axe.setDurability((short) newUses);
            }
            destroyTree();
        } else {
            event.setCancelled(false);
        }
    }

    public void destroyTree() {
        woodTask = LumberJack.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(LumberJack.getInstance(), () -> {
            if(woods.size() == 0) {
                LumberJack.getInstance().getServer().getScheduler().cancelTask(woodTask);
                return;
            }
            Location location = woods.get(0);
            if(location.getBlock().getType().equals(material)) {
                location.getBlock().setType(Material.AIR);
                ItemStack is = new ItemStack(material);
                is.setAmount(1);
                if (hasSpace()) {
                    player.getInventory().addItem(is);
                } else {
                    location.getWorld().dropItem(location, is);
                }
                player.playSound(location, Sound.BLOCK_WOOD_BREAK, 1.0F, 1.0F);
            }
            woods.remove(0);
        }, 0, LumberJack.getInstance().getDataHandler().getWoodDestroyInterval());
        leavesTask = LumberJack.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(LumberJack.getInstance(), () -> {
            if(leaves.size() == 0) {
                LumberJack.getInstance().getServer().getScheduler().cancelTask(leavesTask);
                return;
            }
            Location location = leaves.get(0);
            if(location.getBlock().getType().equals(leavesMaterial)) {
                location.getBlock().breakNaturally();
                player.playSound(location, Sound.BLOCK_GRASS_BREAK, 1.0F, 1.0F);
            }
            leaves.remove(0);
        }, 0, LumberJack.getInstance().getDataHandler().getLeavesDestroyInterval());
    }

    private boolean hasSpace() {
        Inventory playerInventory = player.getInventory();
        for(int i = 0; i < 36; i++) {
            if(player.getInventory().getItem(i) != null) {
                if(playerInventory.getItem(i).equals(Material.AIR)) {
                    return true;
                } else if(playerInventory.getItem(i).getType().equals(material)) {
                    if(playerInventory.getItem(i).getAmount() < 64) {
                        return true;
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    }

}

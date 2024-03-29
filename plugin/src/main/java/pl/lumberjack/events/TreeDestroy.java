package pl.lumberjack.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
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
    private boolean silkTouch;
    private final boolean isNether;
    private final List<Location> woods = new ArrayList<>();
    private final List<Location> leaves = new ArrayList<>();

    public TreeDestroy(Player player, Material material, Material leavesMaterial, Location location, BlockBreakEvent event, boolean nether) {
        this.player = player;
        this.material = material;
        this.leavesMaterial = leavesMaterial;
        this.event = event;
        this.isNether = nether;
        checkBlock(location);
    }

    public void checkBlock(Location location) {
        Location startLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        int totalLeaves = 0;
        int totalWood = 0;
        int requiredLeaves = LumberJack.getInstance().getDataHandler().getRequiredLeaves();
        int height = LumberJack.getInstance().getDataHandler().getTreeMaxHeight();
        int woodRange = LumberJack.getInstance().getDataHandler().getWoodRange();
        int leavesRange = LumberJack.getInstance().getDataHandler().getLeavesRange();
        for(int i = 0; i < height; i++) {
            location = new Location(startLocation.getWorld(), startLocation.getX(), startLocation.getY()+i, startLocation.getZ());
            if(!location.getBlock().getType().equals(material)) {
                height -= 2;
            }
            double startX = location.getX();
            double startZ = location.getZ();
            for(int j = -woodRange; j <= woodRange; j++) {
                for(int k = -woodRange; k <= woodRange; k++) {
                    location.setX(startX + j);
                    location.setZ(startZ + k);
                    if (location.getBlock().getType().equals(material)) {
                        woods.add(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()));
                        totalWood++;
                    }
                }
            }
            for(int j = -leavesRange; j <= leavesRange; j++) {
                for(int k = -leavesRange; k <= leavesRange; k++) {
                    location.setX(startX + j);
                    location.setZ(startZ + k);
                    Material leavesBlock = location.getBlock().getType();
                    if(leavesBlock.equals(leavesMaterial) ||
                            (material.equals(Material.OAK_LOG) && (leavesBlock.equals(Material.AZALEA_LEAVES)
                                    || leavesBlock.equals(Material.FLOWERING_AZALEA_LEAVES)))
                            || (isNether && leavesBlock.equals(Material.SHROOMLIGHT))) {
                        leaves.add(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()));
                        totalLeaves++;
                    }
                }
            }
        }
        if(totalLeaves >= requiredLeaves) {
            int leavesReduction = LumberJack.getInstance().getDataHandler().getLeavesReduction();
            ItemStack axe = player.getInventory().getItemInMainHand();
            if(axe.getItemMeta() != null) {
                if(axe.getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH) > 0) {
                    leavesReduction = 1;
                    silkTouch = true;
                }
                if(isNether) {
                    leavesReduction = 1;
                }
                if(!axe.getItemMeta().isUnbreakable() && axe.getType().getMaxDurability() != 0) {
                    int itemDurability = axe.getType().getMaxDurability() - axe.getDurability(); // Get item durability (eg. 1500)
                    int woodLeavesUse = totalWood + (totalLeaves / leavesReduction);
                    if(itemDurability <= woodLeavesUse) { // Check if durability can handle wood and leaves
                        if(itemDurability <= totalWood) { // If not - check if it can handle only wood
                            event.setCancelled(false); // If not, cancel event
                            return;
                        } else { // If it can handle only wood, remove leaves from uses
                            leaves.clear();
                            woodLeavesUse -= (totalLeaves / leavesReduction);
                        }
                    }
                    int uses = axe.getDurability() + woodLeavesUse; // Get item uses + new uses
                    int enchantmentLevel = axe.getItemMeta().getEnchantLevel(Enchantment.DURABILITY);
                    if(enchantmentLevel > 0) {
                        for(int i = 0; i < woodLeavesUse; i++) {
                            int chance = 100 / (enchantmentLevel + 1);
                            int random = (int) Math.floor(Math.random() * (100 + 1));
                            if(random < chance) {
                                uses--;
                            }
                        }
                    }
                    axe.setDurability((short) uses);
                }
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
                if(hasSpace(material)) {
                    player.getInventory().addItem(is);
                } else {
                    if(location.getWorld() != null) {
                        location.getWorld().dropItem(location, is);
                    }
                }
                if(player.isOnline()) {
                    if(isNether) {
                        player.playSound(location, Sound.BLOCK_STEM_BREAK, 1.0F, 1.0F);
                    } else {
                        player.playSound(location, Sound.BLOCK_WOOD_BREAK, 1.0F, 1.0F);
                    }
                }
            }
            woods.remove(0);
        }, 0, LumberJack.getInstance().getDataHandler().getWoodDestroyInterval());
        leavesTask = LumberJack.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(LumberJack.getInstance(), () -> {
            if(leaves.size() == 0) {
                LumberJack.getInstance().getServer().getScheduler().cancelTask(leavesTask);
                return;
            }
            Location location = leaves.get(0);
            Material leavesBlock = location.getBlock().getType();
            if(leavesBlock.equals(leavesMaterial) || ((material.equals(Material.OAK_LOG) &&
                    (leavesBlock.equals(Material.AZALEA_LEAVES) || leavesBlock.equals(Material.FLOWERING_AZALEA_LEAVES))))
                    || (isNether && leavesBlock.equals(Material.SHROOMLIGHT))) {
                if(isNether) {
                    if(hasSpace(leavesBlock)) {
                        if(player.isOnline()) {
                            location.getBlock().setType(Material.AIR);
                            player.getInventory().addItem(new ItemStack(leavesBlock));
                        } else {
                            location.getBlock().breakNaturally();
                        }
                    } else {
                        location.getBlock().breakNaturally();
                    }
                } else {
                    if(silkTouch) {
                        location.getBlock().setType(Material.AIR);
                        if(hasSpace(leavesBlock)) {
                            player.getInventory().addItem(new ItemStack(leavesBlock));
                        } else {
                            if(location.getWorld() != null) {
                                location.getWorld().dropItem(location, new ItemStack(leavesBlock));
                            }
                        }
                    } else {
                        location.getBlock().breakNaturally();
                    }
                }
                if(player.isOnline()) {
                    if(isNether) {
                        player.playSound(location, Sound.BLOCK_WART_BLOCK_BREAK, 1.0F, 1.0F);
                    } else {
                        player.playSound(location, Sound.BLOCK_GRASS_BREAK, 1.0F, 1.0F);
                    }
                }
            }
            leaves.remove(0);
        }, 0, LumberJack.getInstance().getDataHandler().getLeavesDestroyInterval());
    }

    private boolean hasSpace(Material m) {
        if(!player.isOnline()) return false;
        for(int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if(item != null) {
                if(item.getType().equals(Material.AIR)) {
                    return true;
                } else if(item.getType().equals(m)) {
                    if(item.getAmount() < 64) {
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

package pl.lumberjack.data;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.lumberjack.LumberJack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DataHandler {

    private int treeMaxHeight = 60;
    private int woodDestroyInterval = 2;
    private int leavesDestroyInterval = 1;
    private int woodRange = 2;
    private int leavesRange = 5;
    private int requiredLeaves = 10;
    private int leavesReduction = 2;
    private final List<Material> axes = new ArrayList<>();
    private String permission = "lumberjack.use";

    public void loadConfig() {
        axes.clear();
        File configFile = new File(LumberJack.getInstance().getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            LumberJack.getInstance().saveResource("config.yml", false);
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(configFile);
        this.treeMaxHeight = yml.getInt("config.maxTreeHeight");
        this.woodDestroyInterval = yml.getInt("config.woodDestroyInterval");
        this.leavesDestroyInterval = yml.getInt("config.leavesDestroyInterval");
        this.woodRange = yml.getInt("config.woodRange");
        this.leavesRange = yml.getInt("config.leavesRange");
        this.requiredLeaves = yml.getInt("config.requiredLeaves");
        this.leavesReduction = yml.getInt("config.leavesReduction");
        this.permission = yml.getString("config.permission");
        List<String> pot_axes = yml.getStringList("config.axes");
        if(pot_axes.size() == 0) {
            pot_axes.add("WOODEN_AXE");
        }
        for(String axe : pot_axes) {
            if(Material.getMaterial(axe) == null) {
                LumberJack.getInstance().getLogger().severe("Not found axe material: " + axe);
            } else {
                axes.add(Material.getMaterial(axe));
            }
        }
    }

}

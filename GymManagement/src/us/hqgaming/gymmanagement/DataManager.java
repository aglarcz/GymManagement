package us.hqgaming.gymmanagement;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import us.hqgaming.gymmanagement.utils.Log;

public class DataManager {

    private FileConfiguration data;
    private static DataManager instance = new DataManager();
    private File dfile;

    public FileConfiguration getData() {
        return this.data;
    }

    public static DataManager getInstance() {
        return instance;
    }

    public void setupData(Plugin plugin) {
        this.dfile = new File(plugin.getDataFolder(), "badges.yml");

        if (!this.dfile.exists()) {
            try {
                this.dfile.createNewFile();
            } catch (IOException e) {
                Log.severe(ChatColor.RED + "Could not create badge_data.yml!");
            }
        }

        this.data = YamlConfiguration.loadConfiguration(this.dfile);

        Log.info("Badge data backends have been established, ready for use!");

    }

    public void saveData() throws IOException {
        this.data.save(this.dfile);

    }
}

package us.hqgaming.gymmanagement;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.hqgaming.gymmanagement.commands.CommandHandler;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.gym.GymAccount;
import us.hqgaming.gymmanagement.gym.Gym;
import us.hqgaming.gymmanagement.managers.DataManager;
import us.hqgaming.gymmanagement.managers.EventManager;
import us.hqgaming.gymmanagement.managers.MenuManager;
import us.hqgaming.gymmanagement.managers.UpdateManager;
import us.hqgaming.gymmanagement.utils.Log;

import java.io.*;
import java.util.Set;

public final class GymManagement extends JavaPlugin {

    private static boolean GUI;
    private static String prefix;
    private static GymManagement instance;

    private int GYM_SIZE;
    private int BADGE_SIZE;

    private static CommandHandler commandHandler;
    private static UpdateManager updater;
    private static MenuManager menu;


    public static GymManagement getInstance() {
        return instance;
    }

    public void onEnable() {
        try {

            instance = this;
            saveDefaultConfig();
            getDataManager().setupData(this);
            loadConfiguration();
            registerGyms();
            createManagers();

        } catch (Exception ex) {
            Log.severe("Plugin load exception");
            ex.printStackTrace();
        }
    }

    public void onDisable() {
        GymAccount.saveAccounts();
    }

    public void loadConfiguration() {
        GYM_SIZE = getConfig().getInt("Gym-Size");
        BADGE_SIZE = getConfig().getInt("Badge-Size");
        GUI = getConfig().getBoolean("Graphical-User-Interface");
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig()
                .getString("Prefix"));
    }


    public void createManagers() throws Exception {
        menu = new MenuManager();
        updater = new UpdateManager(this);
        commandHandler = new CommandHandler(this);
        getServer().getPluginManager().registerEvents(new EventManager(), this);
    }

    public void reloadManagers() throws Exception {
        menu = null;
        updater = null;
        commandHandler = null;
        createManagers();
    }

    public void registerGyms() throws Exception {
        Set<String> gyms = getConfig().getConfigurationSection("Gyms")
                .getKeys(false);

        for (String name : gyms) {

            Gym gym = new Gym(name);
            File rules = (File) getDataManager().get(3, gym);
            if (rules.length() != 0) gym.decipher(rules);

            gym.setBadge(
                    getConfig().getString("Gyms." + name + ".Badge-Name"),
                    getConfig().getInt("Gyms." + name + ".Badge-Item"));
            gym.setChatName(getConfig().getString(
                    "Gyms." + name + ".Gym-Name"));
            gym.setRunCommandClosed(getConfig().getBoolean(
                    "Gyms." + name + ".Command-Closed"));
            gym.setCommand(getConfig().getString(
                    "Gyms." + name + ".Command"));
            gym.setItemid(getConfig()
                    .getInt("Gyms." + name + ".Gym-Item"));

        }
    }

    public void helpMessage(CommandType commandType, Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&cGym Management by &b&nXenoJava.com&r"));
        player.sendMessage(ChatColor.RED
                + "/gyms - Displays all gyms.");
        if (commandType == CommandType.BADGE) {
            player.sendMessage(ChatColor.RED
                    + "/badge give {player} <Optional: (gym name)> - Gives players your gym badge.");
            player.sendMessage(ChatColor.RED
                    + "/badge remove {player} <Optional: (gym name)> - Takes your gym badge away from players.");
            player.sendMessage(ChatColor.RED
                    + "/badge see {player} - Displays player's badges.");
        } else if (commandType == CommandType.GYM) {
            player.sendMessage(ChatColor.RED
                    + "/gym open <Optional: (gym name)> - Opens gym.");
            player.sendMessage(ChatColor.RED
                    + "/gym close <Optional: (gym name)> - Closes gym.");
            player.sendMessage(ChatColor.RED
                    + "/gym hold <Optional: (gym name)> - Holds gym.");
            player.sendMessage(ChatColor.RED
                    + "/gym addleader (player) (gym name) - Makes a player gym leader.");
            player.sendMessage(ChatColor.RED
                    + "/gym removeleader (player) (gym name) - Removes player's gym");
            player.sendMessage(ChatColor.RED
                    + "/gym sethome (gym name) - Sets gym home.");
            player.sendMessage(ChatColor.RED
                    + "/gym deletehome (gym name) - Deletes gym home.");
            player.sendMessage(ChatColor.RED
                    + "/gym home <Optional: (gym name)> - Teleports you to gym home.");
            player.sendMessage(ChatColor.RED
                    + "/gym stats [Gym | Player] - Displays gym's statistics.");
            player.sendMessage(ChatColor.RED
                    + "/gym leaders - Lists online gym leaders.");
        }

    }

    public static UpdateManager getUpdater() {
        return updater;
    }

    public static MenuManager getMenu() {
        return menu;
    }

    public File getDataFile() {
        return getFile();
    }

    public int getGymSize() {
        return GYM_SIZE;
    }

    public int getBadgeSize() {
        return BADGE_SIZE;
    }

    public static boolean GUI() {
        return GUI;
    }

    public static String getPrefix() {
        return prefix;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public static DataManager getDataManager() {
        return DataManager.getInstance();
    }


}

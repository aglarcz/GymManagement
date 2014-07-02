package us.hqgaming.gymmanagement;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import us.hqgaming.gymmanagement.badge.Badge;
import us.hqgaming.gymmanagement.badge.BadgeAccount;
import us.hqgaming.gymmanagement.badge.GiveBadge;
import us.hqgaming.gymmanagement.badge.RemoveBadge;
import us.hqgaming.gymmanagement.badge.SeeBadge;
import us.hqgaming.gymmanagement.commands.CommandManager;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.commands.SubCommand;
import us.hqgaming.gymmanagement.commands.SubCommand.PluginDependent;
import us.hqgaming.gymmanagement.gym.CloseGym;
import us.hqgaming.gymmanagement.gym.Gym;
import us.hqgaming.gymmanagement.gym.LeadersGym;
import us.hqgaming.gymmanagement.gym.OpenGym;
import us.hqgaming.gymmanagement.utils.Log;
import us.hqgaming.gymmanagement.utils.Updater;

/**
 * --- Gym Management ---
 *
 * @version 1.5
 * @author Xeno
 */
@SuppressWarnings("deprecation")
public final class GymManagement extends JavaPlugin implements Listener {

    private static ArrayList<BadgeAccount> badgeAccounts = new ArrayList<BadgeAccount>();
    private static ArrayList<Gym> gyms = new ArrayList<Gym>();
    public static ArrayList<ItemStack> items = new ArrayList<ItemStack>();
    public ArrayList<SubCommand> subCmds = new ArrayList<SubCommand>();

    public static String prefix;
    public static Inventory gymMenu;
    public static int config_version;
    public static boolean UPDATE_CHECK;
    public static String latestUpdate = "null";

    private int GYM_SLOTS;
    private int BADGE_SLOTS;
    private CommandManager cmdmanager;

    public void onEnable() {
        try {
            saveDefaultConfig();
            this.getServer().getPluginManager().registerEvents(this, this);

            loadConfiguration();
            getDataManager().setupData(this);
            registerGyms();
            registerPermissions();
            registerCommands();
            updateGymMenu();

            if (config_version != 3) {
                updateConfiguration();
            }

            if (UPDATE_CHECK) {
                getServer().getScheduler().runTaskTimerAsynchronously(this,
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            updateCheck();
                                        }
                                    }, 0, 20 * 60 * 60 * 3);
            }

        } catch (Exception ex) {
            Log.severe("Plugin load exception");
            ex.printStackTrace();
        }

    }

    public void onDisable() {
        saveBadges(this.getDataManager());
    }

    public void loadConfiguration() {
        config_version = getConfig().getInt("Config-Version");
        UPDATE_CHECK = getConfig().getBoolean("Update-Check");
        GYM_SLOTS = getConfig().getInt("Gym-Slots");
        BADGE_SLOTS = getConfig().getInt("Badge-Slots");
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig()
                            .getString("Chat-Prefix"));
    }

    public void updateConfiguration() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = this.getResource("config.yml");
            File configFile = new File(this.getDataFolder() + "/config.yml");
            Files.copy(configFile, new File(this.getDataFolder() + "/config.old.yml"));
            outputStream = new FileOutputStream(configFile);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            Log.severe("Your configuration file has been updated");

        } catch (IOException e) {
            e.printStackTrace();
            Log
                                .severe(
                                                    "Could not update config.yml!");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void saveBadges(DataManager data) {
        for (BadgeAccount account : badgeAccounts) {
            try {
                account.save(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadBadges() {
        badgeAccounts.clear();

        for (Player p : Bukkit.getOnlinePlayers()) {
            this.getBadgeAccount(p.getName());
        }
    }

    public void registerCommands() throws Exception {
        cmdmanager = new CommandManager(this);
        for (Entry<String, Map<String, Object>> set : this.getDescription()
                            .getCommands().entrySet()) {
            getCommand(set.getKey()).setExecutor(this.getCommandManager());
        }

        this.registerSubCommand(GiveBadge.class);
        this.registerSubCommand(RemoveBadge.class);
        this.registerSubCommand(SeeBadge.class);
        this.registerSubCommand(OpenGym.class);
        this.registerSubCommand(CloseGym.class);
        this.registerSubCommand(LeadersGym.class);
    }

    private void registerSubCommand(Class<? extends SubCommand> clazz) throws Exception {
        Constructor ctor = null;
        SubCommand cmd;

        if (clazz.isAnnotationPresent(PluginDependent.class)) {
            ctor = clazz.getDeclaredConstructor(this.getClass());
            ctor.setAccessible(true);
            cmd = (SubCommand) ctor.newInstance(this);

        } else {
            cmd = (SubCommand) clazz.newInstance();
        }

        this.subCmds.add(cmd);
    }

    public File getDataFile() {
        return getFile();
    }

    private void updateCheck() {
        Log.info("Running updater ..");

        Updater updater = new Updater(this, 79053, getFile(),
                            Updater.UpdateType.NO_DOWNLOAD, false);

        Updater.UpdateResult result = updater.getResult();
        switch (result) {
            case NO_UPDATE:
                Log.info("No update was found.");
                break;
            case UPDATE_AVAILABLE:
                latestUpdate = updater.getLatestName();
                Log.info("------------------------------------");
                Log.info(latestUpdate + " is now available!");
                Log.info("Run '/gymmanagement update' to update now.");
                Log.info("------------------------------------");
                break;
            case DISABLED:
                Log.info("Update checking has been disabled in the updater config.");
                break;
            case FAIL_APIKEY:
                Log.warning("The API key you have provided is incorrect!");
                break;
            default:
                break;
        }
    }

    public void registerPermissions() {
        for (Gym gym : gyms) {
            Permission perm = new Permission("gym."
                                + gym.getGymName().toLowerCase());
            perm.setDefault(PermissionDefault.OP);
            Bukkit.getServer().getPluginManager().addPermission(perm);
            Log.info("Gym Permission: " + perm.getName()
                                + " has been registered!");
        }
    }

    public void registerGyms() {

        new GymMenu(this);
        gymMenu = Bukkit.createInventory(null, GYM_SLOTS, "Gyms");

        for (String gymName : getConfig().getConfigurationSection("Gyms")
                            .getKeys(false)) {

            Gym gym = new Gym(gymName);
            gym.setBadge(
                                getConfig().getString("Gyms." + gymName + ".Badge-Name"),
                                getConfig().getInt("Gyms." + gymName + ".Badge-Item"));
            gym.setChatName(getConfig().getString(
                                "Gyms." + gymName + ".Gym-Name"));
            gym.setRunCommandIfClosed(getConfig().getBoolean(
                                "Gyms." + gymName + ".Run-On-Click-Command-If-Gym-Closed"));
            gym.setItemClickCommandName(getConfig().getString(
                                "Gyms." + gymName + ".On-Item-Click-Command-Name"));
            gym.setGymItemID(getConfig()
                                .getInt("Gyms." + gymName + ".Gym-Item"));

        }

    }

    public static ArrayList<BadgeAccount> getBadgeAccounts() {
        return badgeAccounts;
    }

    public DataManager getDataManager() {
        return DataManager.getInstance();
    }

    public int getGymSlots() {
        return GYM_SLOTS;
    }

    public int getBadgeSlots() {
        return BADGE_SLOTS;
    }

    public String getPrefix() {
        return prefix;
    }

    public static List<Gym> getGyms() {
        return gyms;
    }

    public CommandManager getCommandManager() {
        return cmdmanager;
    }

    public Badge getBadge(String badge_name) {
        for (Gym gym : GymManagement.getGyms()) {
            if (gym.getBadge().getName().equalsIgnoreCase(badge_name)) {
                return gym.getBadge();
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getName();

        this.getBadgeAccount(name);

        if (!isGymLeader(e.getPlayer())) {
            return;
        }

        Gym gym = getPlayerGym(e.getPlayer());

        for (Player p : Bukkit.getOnlinePlayers()) {
            ChatManager.messagePlayer(p,
                                "&e" + name + " &a(" + gym.getChatName()
                                + " Leader&a)&c has logged in!");
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleUpdate(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        if (UPDATE_CHECK && !latestUpdate.equals("null")
                            || p.hasPermission("gymmanagement.update")) {
            Bukkit.getScheduler().runTaskLater(this, new BukkitRunnable() {
                @Override
                public void run() {
                    p.sendMessage("§f[§6GymManagement§f] §7" + latestUpdate
                                        + " §eis now available!");
                    p.sendMessage("§f[§6GymManagement§f] §ehttp://dev.bukkit.org/bukkit-plugins/gymmanagement/");
                    p.sendMessage("§f[§6GymManagement§f] §eRun §7/gymmanagement update §eto update now.");
                }
            }, 110L);
        }
    }

    @EventHandler
    public void onBadgesClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Inventory inventory = e.getInventory();
        if (inventory.getTitle().contains("Badges")) {
            e.setCancelled(true);
            return;
        }
    }

    public boolean isGymLeader(Player player) {
        int counter = 0;
        for (Gym gym : gyms) {
            if (player.hasPermission("gym." + gym.getGymName())) {
                counter++;
            }
        }
        if (counter > 1 || counter == 0) {
            return false;
        }
        return true;
    }

    public boolean isGym(String gymname) {
        for (Gym gym : gyms) {
            if (gym.getGymName().equalsIgnoreCase(gymname)) {
                return true;
            }
        }
        return false;
    }

    public void helpMessage(CommandType commandType, Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&cGym Management by &b&nXenoJava.com&r"));
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
                                + "/gym leaders - Lists online gym leaders.");
        }

    }

    public void clearInventory(Inventory inventory) {
        for (ItemStack i : inventory.getContents()) {
            if (i != null) {
                inventory.remove(i);
            }
        }
    }

    public void updateGymMenu() {
        clearInventory(gymMenu);
        items.clear();

        for (Gym gym : gyms) {

            String itemName = ChatColor.translateAlternateColorCodes('&',
                                gym.getChatName());

            int itemID = getConfig().getInt(
                                "Gyms." + gym.getGymName() + ".Gym-Item");

            if (itemID == 0) {
                itemID += gym.getBadge().getID();
            }

            List<String> oldLores = getConfig().getStringList(
                                "Gyms." + gym.getGymName() + ".Item-Lore");
            List<String> lores = new ArrayList<String>();
            List<String> tempLores = new ArrayList<String>();

            for (int i = 0; i < oldLores.size(); i++) {
                if (oldLores.get(i).contains("{BADGE}")) {

                    tempLores.add(oldLores.get(i).replace("{BADGE}",
                                        gym.getBadge().getName().toUpperCase()));

                } else if (oldLores.get(i).contains("{STATUS}")) {
                    tempLores.add(oldLores.get(i).replace("{STATUS}",
                                        gym.isOpen() ? "&a&lOPEN" : "&4&lCLOSED"));
                } else {
                    tempLores.add(oldLores.get(i));
                }
            }

            for (String s : tempLores) {
                lores.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            ItemStack item = new ItemStack(itemID);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(itemName);
            itemMeta.setLore(lores);
            item.setItemMeta(itemMeta);
            items.add(item);
        }

        for (ItemStack i : items) {
            gymMenu.addItem(i);
        }
    }

    public Gym getPlayerGym(Player player) {
        if (!this.isGymLeader(player)) {
            return null;
        }
        for (Gym gym : gyms) {
            if (player.hasPermission("gym." + gym.getGymName())) {
                return gym;
            }
        }
        return null;
    }

    public boolean isBadge(String s) {
        for (Gym gym : gyms) {
            if (gym.getBadge().getName().equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public Gym getGym(String gymname) {
        for (Gym gym : gyms) {
            if (gym.getGymName().equalsIgnoreCase(gymname)) {
                return gym;
            }
        }
        return null;
    }

    public boolean hasOnlineLeaders() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isGymLeader(p)) {
                return true;
            }
        }
        return false;
    }

    public Inventory getBadgeInventory(BadgeAccount account) {
        Inventory inventory = Bukkit.createInventory(
                            Bukkit.getPlayer(account.getHolderName()),
                            this.getBadgeSlots(), account.getHolderName() + "'s Badges");
        for (String badge_name : account.getBadges()) {
            if (!this.isBadge(badge_name)) {
                continue;
            }
            ItemStack item = new ItemStack(this.getBadge(badge_name).getID());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.RED + badge_name);
            item.setItemMeta(meta);

            inventory.addItem(item);
        }
        return inventory;
    }

    public BadgeAccount getBadgeAccount(String name) {
        BadgeAccount account = null;
        for (BadgeAccount a : badgeAccounts) {
            if (a.getHolderName().equalsIgnoreCase(name)) {
                account = a;
            }
        }
        if (account == null) {
            account = new BadgeAccount(name);
        }
        account.load(this.getDataManager());
        return account;

    }

}

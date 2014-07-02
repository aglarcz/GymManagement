package us.hqgaming.gymmanagement.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import us.hqgaming.gymmanagement.ChatManager;
import us.hqgaming.gymmanagement.GymManagement;
import static us.hqgaming.gymmanagement.GymManagement.config_version;
import us.hqgaming.gymmanagement.GymMenu;
import us.hqgaming.gymmanagement.badge.BadgeAccount;
import us.hqgaming.gymmanagement.gym.Gym;
import us.hqgaming.gymmanagement.utils.Log;
import us.hqgaming.gymmanagement.utils.Updater;

public class CommandManager implements CommandExecutor {

    GymManagement plugin;

    public CommandManager(GymManagement plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, Command cmd,
                        String label, String[] args) {

        if (label.equalsIgnoreCase("badges")) {

            if (!(sender instanceof Player)) {
                Bukkit.getLogger().info("Command can only be ran in-game.");
                return true;
            }

            Player player = (Player) sender;

            plugin.getBadgeAccount(player.getName());

            player.openInventory(plugin.getBadgeInventory(plugin
                                .getBadgeAccount(player.getName())));

        } else if (label.equalsIgnoreCase("gyms")) {
            if (!(sender instanceof Player)) {
                Bukkit.getLogger().info("Command can only be ran in-game.");
                return true;
            }

            Player player = (Player) sender;

            GymMenu.showMenu(player);

        } else if (label.equalsIgnoreCase("gymmanagement") || label.equalsIgnoreCase("gmanage") || label.equalsIgnoreCase("gymm") || label.equalsIgnoreCase("gmanagement")) {

            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/gymmanagement help");
                sender.sendMessage(ChatColor.RED + "/gymmanagement reload");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {

                if (!sender.hasPermission("gymmanagement.reload")) {
                    ChatManager.messageSender(sender,
                                        "&cYou do not have permission to reload "
                                        + plugin.getDescription().getFullName());
                    return true;
                }

                for (Permission perm : Bukkit.getPluginManager()
                                    .getPermissions()) {
                    for (Gym gym : GymManagement.getGyms()) {
                        if (perm.getName().equalsIgnoreCase(
                                            "gym." + gym.getGymName())) {

                            Bukkit.getPluginManager().removePermission(perm);
                            Log.info(perm.getName() + " has been unregistered");
                        }
                    }
                }

                for (BadgeAccount account : GymManagement.getBadgeAccounts()) {
                    try {
                        account.save(plugin.getDataManager());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                GymManagement.getGyms().clear();
                GymManagement.items.clear();
                plugin.reloadBadges();
                plugin.reloadConfig();
                plugin.loadConfiguration();
                plugin.registerGyms();
                plugin.registerPermissions();
                plugin.updateGymMenu();

                if (config_version != 3) {
                    plugin.updateConfiguration();
                }

                sender.sendMessage(ChatColor.GREEN
                                    + plugin.getDescription().getFullName()
                                    + " has been reloaded!");
                return true;
            } else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&6▇▇▇▇▇▇▇▇ " + "&cGym Management" + " &6▇▇▇▇▇▇▇▇▇"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&6/gymmanagement help &a- &cSends you here :D"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&6/gymmanagement reload &a- &cReloads entire plugin."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&6/gymmanagement update &a- &cUpdates plugin"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&6/gyms &a- &cDisplays gym menu."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&6/badges &a- &cDisplays your gym badges."));
                sender.sendMessage(ChatColor
                                    .translateAlternateColorCodes('&',
                                                        "&6/gym open <Optional: (gym name)> &a- &cOpens a gym."));
                sender.sendMessage(ChatColor
                                    .translateAlternateColorCodes('&',
                                                        "&6/gym close <Optional: (gym name)> &a- &cCloses a gym."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&6/gym leaders &a- &cDisplays online gym leaders."));
                sender.sendMessage(ChatColor
                                    .translateAlternateColorCodes('&',
                                                        "&6/badge give (name) <Optional: (gym name)> &a- &cGives a player gym badge."));
                sender.sendMessage(ChatColor
                                    .translateAlternateColorCodes(
                                                        '&',
                                                        "&6/badge remove (name) <Optional: (gym name)> &a- &cRemoves a player gym badge."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&6/badge see (name) - &cDisplays player's badges."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&6▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&aWritten by &c&nwww.XenoJava.com"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&aProvided by &c&nwww.HQGaming.us"));
                return true;
            } else if (args[0].equalsIgnoreCase("update")) {

                if (!sender.hasPermission("gymmanagement.update")) {
                    ChatManager.messageSender(sender,
                                        "&cYou do not have permission to update "
                                        + plugin.getDescription().getFullName());
                }

                update(sender);

                return true;
            }

            sender.sendMessage(ChatColor.RED + "/gymmanagement help");
            sender.sendMessage(ChatColor.RED + "/gymmanagement update");
            sender.sendMessage(ChatColor.RED + "/gymmanagement reload");

        } else if (label.equalsIgnoreCase("gym")
                            || label.equalsIgnoreCase("badge")) {
            if (!(sender instanceof Player)) {
                Log.info("Command can only be ran in-game.");
                return true;
            }

            Player player = (Player) sender;
            if (args.length == 0) {
                plugin.helpMessage(CommandType.getCommandType(label), player);
                return true;
            }

            /*
             * Converting to sub command
             */
            ArrayList<String> a = new ArrayList<String>(Arrays.asList(args));
            a.remove(0);

            for (SubCommand c : plugin.subCmds) {
                if (c.getName().equalsIgnoreCase(args[0])
                                    && c.getCommandType().name().equalsIgnoreCase(label)) {
                    try {
                        /*
                         *  When a command is run it can throw an exception
                         */
                        c.runCommand(sender, a.toArray(new String[a.size()]));

                    } catch (Exception e) {

                        sender.sendMessage(ChatColor.RED
                                            + "An error has occurred.");

                        e.printStackTrace();
                    }
                    return true;
                }
            }

            plugin.helpMessage(CommandType.getCommandType(label), player);
        }
        return false;
    }

    private void update(final CommandSender sender) {
        if (GymManagement.UPDATE_CHECK) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sender.sendMessage(plugin.getPrefix()
                                        + "§eRunning updater ..");

                    Updater updater = new Updater(plugin, 79053, plugin
                                        .getDataFile(),
                                        Updater.UpdateType.NO_VERSION_CHECK, true);

                    switch (updater.getResult()) {
                        case FAIL_DBO:
                            sender.sendMessage(plugin.getPrefix()
                                                + "§cUpdater failed! (Could not contact dev.bukkit.org)");
                            break;
                        case FAIL_DOWNLOAD:
                            sender.sendMessage(plugin.getPrefix()
                                                + "§cUpdater failed! (Failed to download file)");
                            break;
                        case SUCCESS:
                            sender.sendMessage(plugin.getPrefix()
                                                + "§eDownload complete! (§7"
                                                + updater.getLatestName() + "§e)");
                            sender.sendMessage(plugin.getPrefix()
                                                + "§eRestart the server to apply the update.");
                            break;
                        default:
                            break;
                    }
                }
            }).start();
        } else {
            sender.sendMessage(plugin.getPrefix()
                                + "§cThe updater has not been enabled in the config!");
        }
    }
}

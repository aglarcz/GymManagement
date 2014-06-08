package us.hqgaming.gymmanagement.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import us.hqgaming.gymmanagement.GymManagement;
import us.hqgaming.gymmanagement.GymMenu;
import us.hqgaming.gymmanagement.gym.Gym;

public class CommandManager implements CommandExecutor {

	GymManagement plugin;

	public CommandManager(GymManagement plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player = (Player) sender;

		if (label.equalsIgnoreCase("badges")) {

			// Creates badge account if player does not have one
			if (!plugin.hasBadgeAccount(player.getName())) {
				plugin.createBadgeAccount(player.getName());
			}

			plugin.openBadgeInventory(player);

		} else if (label.equalsIgnoreCase("gyms")) {

			GymMenu.showMenu(player);

		} else if (label.equalsIgnoreCase("gymmanagement")) {

			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "/gymmanagement help");
				player.sendMessage(ChatColor.RED + "/gymmanagement reload");
				return true;
			}

			if (args[0].equalsIgnoreCase("reload")) {

				if (!player.hasPermission("gymmanagement.reload")) {
					player.sendMessage(ChatColor.RED
							+ "You do not have permission to reload "
							+ plugin.getDescription().getFullName());
					return true;
				}

				for (Permission perm : Bukkit.getPluginManager()
						.getPermissions()) {

					for (Gym gym : plugin.getGyms()) {

						if (perm.getName().equalsIgnoreCase(
								"gym." + gym.getGymName())) {

							Bukkit.getPluginManager().removePermission(perm);
							Bukkit.getLogger().info(
									perm.getName() + " has been unregistered");

						}
					}
				}

				plugin.getGyms().clear();
				GymManagement.items.clear();
				plugin.getDataManager().saveData(plugin.getBadgeAccounts());
				plugin.reloadConfig();
				plugin.loadData();
				plugin.loadConfiguration();
				plugin.registerGyms();
				plugin.registerPermissions();
				plugin.updateGymMenu();

				player.sendMessage(ChatColor.GREEN
						+ plugin.getDescription().getFullName()
						+ " has been reloaded!");
				return true;
			} else if (args[0].equalsIgnoreCase("help")) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&6▇▇▇▇▇▇▇▇ " + "&cGym Management" + " &6▇▇▇▇▇▇▇▇▇"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&6/gymmanagement help &a- &cSends you here :D"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&6/gymmanagement reload &a- &cReloads entire plugin."));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&6/gyms &a- &cDisplays gym menu."));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&6/badges &a- &cDisplays your gym badges."));
				player.sendMessage(ChatColor
						.translateAlternateColorCodes('&',
								"&6/gym open <Optional: (gym name)> &a- &cOpens a gym."));
				player.sendMessage(ChatColor
						.translateAlternateColorCodes('&',
								"&6/gym close <Optional: (gym name)> &a- &cCloses a gym."));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&6/gym leaders &a- &cDisplays online gym leaders."));
				player.sendMessage(ChatColor
						.translateAlternateColorCodes('&',
								"&6/badge give (name) <Optional: (gym name)> &a- &cGives a player gym badge."));
				player.sendMessage(ChatColor
						.translateAlternateColorCodes(
								'&',
								"&6/badge remove (name) <Optional: (gym name)> &a- &cRemoves a player gym badge."));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&6/badge see (name) - &cDisplays player's badges."));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&6▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇▇"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&aWritten by &c&nwww.XenoJava.com"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&aFrom &c&nwww.HQGaming.us"));
				return true;
			}

			player.sendMessage(ChatColor.RED + "/gymmanagement help");
			player.sendMessage(ChatColor.RED + "/gymmanagement reload");

		} else if (label.equalsIgnoreCase("badge")) {
			if (args.length == 0) {
				/*
				 * Checks if arguments are equal to 0 and if they are it will
				 * display all available commands.
				 */

				plugin.helpMessage(CommandType.BADGE, player);

				return true;
			}
			/*
			 * Example of inheriting from a class
			 */

			ArrayList<String> a = new ArrayList<String>(Arrays.asList(args));
			a.remove(0);

			for (PixelmonCommand c : plugin.cmds) {
				if (c.getName().equalsIgnoreCase(args[0])
						&& c.getCommandType() == CommandType.BADGE) {
					try {
						c.runCommand(sender, a.toArray(new String[a.size()]));
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED
								+ "An error has occurred.");
						e.printStackTrace();
					}
					return true;
				}
			}

			plugin.helpMessage(CommandType.BADGE, player);
		} else if (label.equalsIgnoreCase("gym")) {
			if (args.length == 0) {
				/*
				 * Checks if arguments are equal to 0 and if they are it will
				 * display all available commands.
				 */

				plugin.helpMessage(CommandType.GYM, player);

				return true;
			}
			/*
			 * Example of inheriting from a class
			 */

			ArrayList<String> a = new ArrayList<String>(Arrays.asList(args));
			a.remove(0);

			for (PixelmonCommand c : plugin.cmds) {
				if (c.getName().equalsIgnoreCase(args[0])
						&& c.getCommandType() == CommandType.GYM) {
					try {
						c.runCommand(sender, a.toArray(new String[a.size()]));
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED
								+ "An error has occurred.");
						e.printStackTrace();
					}
					return true;
				}
			}

			plugin.helpMessage(CommandType.GYM, player);
		}
		return false;
	}
}
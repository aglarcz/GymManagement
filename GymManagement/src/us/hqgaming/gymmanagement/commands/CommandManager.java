package us.hqgaming.gymmanagement.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hqgaming.gymmanagement.GymManagement;
import us.hqgaming.gymmanagement.GymMenu;

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
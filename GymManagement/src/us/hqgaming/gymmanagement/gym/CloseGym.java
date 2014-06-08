package us.hqgaming.gymmanagement.gym;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hqgaming.gymmanagement.ChatManager;
import us.hqgaming.gymmanagement.GymManagement;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.commands.PixelmonCommand;

public class CloseGym extends PixelmonCommand {

	private final GymManagement plugin;

	public CloseGym(GymManagement plugin) {
		super("close", CommandType.GYM);
		this.plugin = plugin;
	}

	public void runCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			System.out.println("Only players are able to run this command.");
			return;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("gym.close")) {
			ChatManager.messagePlayer(player,
					"&cYou do not have permission to execute this command.");
			return;
		}

		Gym gym = null;
		if (args.length >= 1) {
			if (plugin.isGymLeader(player)) {
				if (args[0].equalsIgnoreCase(plugin.getPlayerGym(player)
						.getGymName())) {
					gym = plugin.getPlayerGym(player);
				} else {
					if (player.hasPermission("gym.authority")) {
						if (!plugin.isGym(args[0])) {
							ChatManager.messagePlayer(player, ChatColor.RED
									+ args[0] + " is not a gym.");
							return;
						} else {
							gym = plugin.getGym(args[0]);
						}
					} else {
						ChatManager
								.messagePlayer(player,
										"&cYou do not have permission to access other gyms");
						return;
					}
				}
			} else {
				if (player.hasPermission("gym.authority")) {
					if (!plugin.isGym(args[0])) {
						ChatManager.messagePlayer(player, ChatColor.RED
								+ args[0] + " is not a gym.");
						return;
					} else {
						gym = plugin.getGym(args[0]);
					}
				} else {
					ChatManager
							.messagePlayer(player,
									"&cYou do not have permission to access other gyms");
					return;
				}
			}
		} else {
			if (plugin.isGymLeader(player)) {
				gym = plugin.getPlayerGym(player);
			}
		}

		if (gym == null) {
			ChatManager
					.messagePlayer(player,
							"&cYou are not a gym leader! Try /gym {argument} {gymname}");
			return;
		}

		if (!gym.isOpen()) {
			ChatManager.messagePlayer(player, "&cThis gym is already closed!");
			return;
		}

		gym.setStatus(false);
		plugin.updateGymMenu();

		for (Player p : Bukkit.getOnlinePlayers()) {
			ChatManager.messagePlayer(p, "&c" + player.getName()
					+ " &ahas closed the " + gym.getChatName() + "&a!");
		}
	}
}

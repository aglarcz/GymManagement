package us.hqgaming.gymmanagement.badge;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hqgaming.gymmanagement.ChatManager;
import us.hqgaming.gymmanagement.GymManagement;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.commands.PixelmonCommand;
import us.hqgaming.gymmanagement.gym.Gym;

public class RemoveBadge extends PixelmonCommand {

	private final GymManagement plugin;

	public RemoveBadge(GymManagement plugin) {
		super("remove", CommandType.BADGE);
		this.plugin = plugin;
	}

	public void runCommand(CommandSender sender, String[] args)
			throws IOException {

		if (!(sender instanceof Player)) {
			System.out.println("Only players are able to run this command.");
			return;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("badge.remove")) {
			ChatManager.messagePlayer(player,
					"&cYou do not have permission to execute this command.");
			return;
		}

		if (args.length < 1) {
			plugin.helpMessage(CommandType.BADGE, player);
			return;
		}

		Gym gym = null;
		if (args.length >= 2) {
			if (plugin.isGymLeader(player)) {
				if (args[1].equalsIgnoreCase(plugin.getPlayerGym(player)
						.getGymName())) {
					gym = plugin.getPlayerGym(player);
				} else {
					if (player.hasPermission("gym.authority")) {
						if (!plugin.isGym(args[1])) {
							ChatManager.messagePlayer(player, ChatColor.RED
									+ args[1] + " is not a gym.");
							return;
						} else {
							gym = plugin.getGym(args[1]);
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
					if (!plugin.isGym(args[1])) {
						ChatManager.messagePlayer(player, ChatColor.RED
								+ args[1] + " is not a gym.");
						return;
					} else {
						gym = plugin.getGym(args[1]);
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
							"&cYou are not a gym leader! Try /badge give/remove/see {player} {gymname}");
			return;
		}

		Badge badge = gym.getBadge();
		Player other = Bukkit.getPlayer(args[0]);

		if (other == null) {
			ChatManager.messagePlayer(player, "&c" + args[0]
					+ " is not online is not online");
			return;
		}

		BadgeAccount account = plugin.getBadgeAccount(other.getName());

		if (!account.hasBadge(badge)) {
			ChatManager.messagePlayer(player, "&c" + args[0]
					+ " does not have your gym badge");
			return;
		}

		account.removeBadge(badge);

		account.save(plugin.getDataManager());

		ChatManager.messagePlayer(player, ChatColor.GREEN + "You have removed "
				+ other.getName() + "'s " + ChatColor.RED
				+ badge.getBadgeName().toUpperCase() + ChatColor.GREEN
				+ " badge!");
		ChatManager.messagePlayer(player, ChatColor.GREEN + player.getName()
				+ " has removed your " + ChatColor.RED
				+ badge.getBadgeName().toUpperCase() + ChatColor.GREEN
				+ " badge!");
	}
}

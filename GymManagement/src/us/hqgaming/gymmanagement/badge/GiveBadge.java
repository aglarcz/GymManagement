package us.hqgaming.gymmanagement.badge;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hqgaming.gymmanagement.Chat;
import us.hqgaming.gymmanagement.GymManagement;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.commands.PixelmonCommand;
import us.hqgaming.gymmanagement.gym.Gym;

public class GiveBadge extends PixelmonCommand {

	private final GymManagement plugin;

	public GiveBadge(GymManagement plugin) {
		super("give", CommandType.BADGE);
		this.plugin = plugin;
	}

	public void runCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			System.out.println("Only players are able to run this command.");
			return;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("Badge.give")) {
			player.sendMessage(ChatColor.RED
					+ "You do not have permission to execute this command.");
			return;
		}

		if (!plugin.isGymLeader(player)) {
			player.sendMessage(ChatColor.RED + "You are not a gym leader.");
			return;
		}

		if (args.length < 1) {
			plugin.helpMessage(CommandType.BADGE, player);
			return;
		}

		Gym gym = plugin.getPlayerGym(player);
		Badge badge = gym.getBadge();
		Player other = Bukkit.getPlayer(args[0]);

		if (plugin.getBadges(other).contains(badge)) {
			player.sendMessage(ChatColor.RED
					+ "This player already has your gym badge.");
			return;
		}

		plugin.getBadges(other).add(badge);
		player.sendMessage(ChatColor.GREEN + "You have given "
				+ other.getName() + " the " + ChatColor.RED
				+ badge.getBadgeName().toUpperCase() + ChatColor.GREEN
				+ " badge!");
		other.sendMessage(ChatColor.GREEN + player.getName()
				+ " has given you the " + ChatColor.RED
				+ badge.getBadgeName().toUpperCase() + ChatColor.GREEN
				+ " badge!");
		for (Player p : Bukkit.getOnlinePlayers()) {
			Chat.messagePlayer(p, "&e" + other.getName() + "&a has earned the "
					+ ChatColor.RED + badge.getBadgeName().toUpperCase()
					+ ChatColor.GREEN + " badge!");
		}
		plugin.getDataManager().flushData(plugin.getBadgeAccounts());

	}
}

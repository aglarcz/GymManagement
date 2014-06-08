package us.hqgaming.gymmanagement.badge;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hqgaming.gymmanagement.GymManagement;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.commands.PixelmonCommand;

public class SeeBadge extends PixelmonCommand {

	private final GymManagement plugin;

	public SeeBadge(GymManagement plugin) {
		super("see", CommandType.BADGE);
		this.plugin = plugin;
	}

	public void runCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			System.out.println("Only players are able to run this command.");
			return;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("badge.see")) {
			player.sendMessage(ChatColor.RED
					+ "You do not have permission to execute this command.");
			return;
		}

		if (args.length < 1) {
			plugin.helpMessage(CommandType.BADGE, player);
			return;
		}

		Player other = Bukkit.getPlayer(args[0]);

		plugin.openBadgeInventory(player, other);
	}
}

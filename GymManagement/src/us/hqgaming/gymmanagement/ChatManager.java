package us.hqgaming.gymmanagement;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatManager {

	public static void messagePlayer(Player p, String message) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				GymManagement.prefix + message));
	}

	public static void messageSender(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				GymManagement.prefix + message));
	}

}

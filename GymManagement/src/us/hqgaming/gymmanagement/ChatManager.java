package us.hqgaming.gymmanagement;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatManager {

	public static void messagePlayer(Player p, String label) {

		String prefix = GymManagement.prefix;
		String message = label;

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix
				+ message));

		return;
	}

}

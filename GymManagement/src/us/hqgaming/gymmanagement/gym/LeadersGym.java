package us.hqgaming.gymmanagement.gym;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hqgaming.gymmanagement.ChatManager;
import us.hqgaming.gymmanagement.GymManagement;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.commands.PixelmonCommand;

public class LeadersGym extends PixelmonCommand {

	private final GymManagement plugin;

	public LeadersGym(GymManagement plugin) {
		super("leaders", CommandType.GYM);
		this.plugin = plugin;
	}

	public void runCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			System.out.println("Only players are able to run this command.");
			return;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("gym.leaders")) {
			ChatManager.messagePlayer(player,
					"&cYou do not have permission to execute this command.");
			return;
		}

		if (!plugin.hasOnlineLeaders()) {
			ChatManager.messagePlayer(player,
					"&cThere are no gym leaders online");
			return;
		}

		player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&a&lONLINE GYM LEADERS"));
		player.sendMessage(" ");
		for (Player p : Bukkit.getOnlinePlayers()) {

			if (plugin.isGymLeader(p)) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&c" + p.getName() + " &7- "
								+ plugin.getPlayerGym(p).getChatName()));
			}
		}
	}
}

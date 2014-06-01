package us.hqgaming.gymmanagement.gym;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hqgaming.gymmanagement.Chat;
import us.hqgaming.gymmanagement.GymManagement;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.commands.PixelmonCommand;

public class OpenGym extends PixelmonCommand {

	private GymManagement plugin;

	public OpenGym(GymManagement plugin) {
		super("open", CommandType.GYM);
		this.plugin = plugin;
	}

	public void runCommand(CommandSender sender, String[] args) {

		if (!(sender instanceof Player)) {
			System.out.println("Only players are able to run this command.");
			return;
		}

		Player player = (Player) sender;

		if (!plugin.isGymLeader(player)) {
			player.sendMessage(ChatColor.RED + "You are not a gym leader.");
			return;
		}

		Gym gym = plugin.getPlayerGym(player);

		gym.setStatus(true);
		plugin.updateGymMenu();
		for (Player p : Bukkit.getOnlinePlayers()) {
			Chat.messagePlayer(p, "&e" + player.getName()
					+ " &chas opened the " + gym.getGymName() + " &cgym!");
		}
	}
}

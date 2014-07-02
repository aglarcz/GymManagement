package us.hqgaming.gymmanagement;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatManager {

    /**
     * @param p Player to message
     * @param message String message (color coding)
     */
    public static void messagePlayer(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            GymManagement.prefix + message));
    }

        /**
     * @param sender Message command sender
     * @param message String message (color coding)
     */
    
    public static void messageSender(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            GymManagement.prefix + message));
    }

}

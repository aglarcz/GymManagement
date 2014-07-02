package us.hqgaming.gymmanagement;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import us.hqgaming.gymmanagement.gym.Gym;

public class GymMenu implements Listener {

    private GymManagement plugin;

    GymMenu(GymManagement plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        if (!e.getInventory().getTitle().equalsIgnoreCase("Gyms")) {
            return;
        }
        if (e.getCurrentItem() != null) {

            for (Gym gym : GymManagement.getGyms()) {

                if (e.getCurrentItem().getTypeId() == gym.getGymItemID()) {

                    if (!gym.isOpen()) {
                        if (!gym.isRunCommandIfClosed()) {
                            break;
                        }
                    }

                    player.performCommand(gym.getItemClickCommandName());
                    break;
                }
            }
        }

        e.setCancelled(true);
        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
    }

    public static void showMenu(final Player player) {
        if (!player.hasPermission("gym.menu")) {
            ChatManager.messagePlayer(player,
                                "&cYou do not have permission to execute this command.");
            return;
        }
        player.openInventory(GymManagement.gymMenu);
    }
}

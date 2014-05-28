package us.hqgaming.gymmanagement;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GymManager implements Listener {

	GymManagement plugin;

	GymManager(GymManagement plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMenuClick(InventoryClickEvent e) {

		Player player = (Player) e.getWhoClicked();

		if (e.getInventory().getTitle() == "Gyms") {
			if (e.getCurrentItem() != null) {

				for (String path : plugin.getConfig()
						.getConfigurationSection("Gyms").getKeys(false)) {

					if (path.equalsIgnoreCase("Server-"
							+ String.valueOf(GymManagement.SERVER_ID))) {

						for (String subPath : plugin.getConfig()
								.getConfigurationSection("Gyms." + path)
								.getKeys(false)) {
							for (Gym gym : Gym.values()) {

								if (gym.name().equalsIgnoreCase(subPath)) {

									int itemID = plugin.getConfig().getInt(
											"Gyms." + path + "." + subPath
													+ ".Item-ID");

									if (itemID == 0) {
										itemID += gym.getBadge().getID();
									}

									if (e.getCurrentItem().getTypeId() == itemID) {
										String command = plugin.getConfig()
												.getString(
														"Gyms." + path + "."
																+ subPath
																+ ".Command");

										player.performCommand(command);

									}

								}

							}
						}
					}
				}
			}

			e.setCancelled(true);
			player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
		}
	}

	public static void showMenu(final Player player) {

		player.openInventory(GymManagement.gymMenu);
	}
}

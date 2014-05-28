package us.hqgaming.gymmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import us.hqgaming.gymmanagement.badge.GiveBadge;
import us.hqgaming.gymmanagement.badge.RemoveBadge;
import us.hqgaming.gymmanagement.badge.SeeBadge;
import us.hqgaming.gymmanagement.commands.CommandManager;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.commands.PixelmonCommand;
import us.hqgaming.gymmanagement.gym.CloseGym;
import us.hqgaming.gymmanagement.gym.OpenGym;

/** --- Gym Management ---
 * 
 * @version 1.2
 * @author Xeno */

public class GymManagement extends JavaPlugin implements Listener {

	private static HashMap<String, List<Badge>> badges = new HashMap<String, List<Badge>>();
	static List<ItemStack> items = new ArrayList<ItemStack>();
	public List<PixelmonCommand> cmds = new ArrayList<PixelmonCommand>();

	public static int SERVER_ID;
	public static Inventory gymMenu;
	public static String prefix;
	private int gymSlots;

	private DataManager data;

	public enum GymPermission {

		ROCK(Gym.ROCK, "gym.rock"), FLYING(Gym.FLYING, "gym.flying"), WATER(
				Gym.WATER, "gym.water"), ELECTRIC(Gym.ELECTRIC, "gym.eletric"), GRASS(
				Gym.GRASS, "gym.grass"), POISON(Gym.POISON, "gym.poison"), PSYCHIC(
				Gym.PSYCHIC, "gym.psychic"), FIRE(Gym.FIRE, "gym.fire"), BUG(
				Gym.BUG, "gym.bug"), NORMAL(Gym.NORMAL, "gym.normal"), GHOST(
				Gym.GHOST, "gym.ghost"), FIGHTING(Gym.FIGHTING, "gym.fighting"), STEEL(
				Gym.STEEL, "gym.steel"), ICE(Gym.ICE, "gym.ice"), DRAGON(
				Gym.DRAGON, "gym.dragon");

		String permission;
		Gym gym;

		GymPermission(Gym gym, String permission) {

			this.permission = permission;
			this.gym = gym;

		}

		public String getPermission() {
			return permission;
		}

		public Gym getGym() {
			return gym;
		}

	}

	public void onEnable() {

		saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);
		load();

	}

	public void onDisable() {

		getDataManager().flushData(badges);
	}

	@SuppressWarnings("unchecked")
	private void load() {
		SERVER_ID = getConfig().getInt("Server-ID");
		gymSlots = getConfig().getInt("Gym-Slots");
		prefix = getConfig().getString("Chat-Prefix");
		data = DataManager.getInstance();

		new GymManager(this);
		getDataManager().setupData(this);
		gymMenu = Bukkit.createInventory(null, gymSlots, "Gyms");
		getCommand("badges").setExecutor(new CommandManager(this));
		getCommand("badge").setExecutor(new CommandManager(this));
		getCommand("gym").setExecutor(new CommandManager(this));
		getCommand("gyms").setExecutor(new CommandManager(this));
		badges = (HashMap<String, List<Badge>>) this.getDataManager().loadData(
				getDataManager().getData());

		if (badges == null) {
			badges = new HashMap<String, List<Badge>>();
		}

		cmds.add(new GiveBadge(this));
		cmds.add(new RemoveBadge(this));
		cmds.add(new SeeBadge(this));
		cmds.add(new OpenGym(this));
		cmds.add(new CloseGym(this));
		this.updateGymMenu();
	}

	public HashMap<String, List<Badge>> getBadgeAccounts() {

		return badges;
	}

	public DataManager getDataManager() {
		return data;
	}

	public int getGymSlots() {
		return gymSlots;
	}

	public String getPrefix() {
		return prefix;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		// Creates badge account if player does not have one
		if (!hasBadgeAccount(e.getPlayer().getName())) {
			createBadgeAccount(e.getPlayer().getName());
		}

		if (!isGymLeader(e.getPlayer())) {
			return;
		}

		Gym gym = getPlayerGym(e.getPlayer());

		for (Player p : Bukkit.getOnlinePlayers()) {
			Chat.messagePlayer(p,
					"&e" + e.getPlayer().getName() + " &a(" + gym.getGymName()
							+ " Gym Leader&a)&c has logged in!");
		}

	}

	@EventHandler
	public void onBadgesClick(InventoryClickEvent e) {

		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}

		Inventory inventory = e.getInventory();

		if (inventory.getTitle().contains("Badges")) {
			e.setCancelled(true);
			return;
		}
	}

	public boolean isGymLeader(Player player) {

		if (player.isOp()) {

			return false;
		}

		int counter = 0;
		for (GymPermission gp : GymPermission.values()) {

			if (player.hasPermission(gp.getPermission())) {

				counter++;
			}

		}

		if (counter > 1 || counter == 0) {
			return false;

		}

		return true;

	}

	public void helpMessage(CommandType commandType, Player player) {
		if (commandType == CommandType.BADGE) {
			player.sendMessage(ChatColor.RED
					+ "/badge give {player} - Gives players your gym badge.");
			player.sendMessage(ChatColor.RED
					+ "/badge remove {player} - Takes your gym badge away from players.");
			player.sendMessage(ChatColor.RED
					+ "/badge see {player} - Displays player's badges.");
		} else if (commandType == CommandType.GYM) {
			player.sendMessage(ChatColor.RED + "/gym open - Opens gym.");
			player.sendMessage(ChatColor.RED + "/gym close - Closes gym.");
		}

	}

	public void clearInventory(Inventory inventory) {

		for (ItemStack i : inventory.getContents()) {

			if (i != null) {
				inventory.remove(i);
			}

		}

	}

	@SuppressWarnings("deprecation")
	public void updateGymMenu() {
		clearInventory(gymMenu);
		items.clear();

		for (String path : getConfig().getConfigurationSection("Gyms").getKeys(
				false)) {

			if (path.equalsIgnoreCase("Server-" + String.valueOf(SERVER_ID))) {

				for (String subPath : getConfig().getConfigurationSection(
						"Gyms." + path).getKeys(false)) {
					for (Gym gym : Gym.values()) {

						if (gym.name().equalsIgnoreCase(subPath)) {

							String itemName = ChatColor
									.translateAlternateColorCodes(
											'&',
											getConfig()
													.getString(
															"Gyms." + path
																	+ "."
																	+ subPath
																	+ ".Name"));

							int itemID = getConfig()
									.getInt("Gyms." + path + "." + subPath
											+ ".Item-ID");

							if (itemID == 0) {
								itemID += gym.getBadge().getID();
							}

							List<String> oldLores = getConfig().getStringList(
									"Gyms." + path + "." + subPath + ".Lore");
							List<String> lores = new ArrayList<String>();
							List<String> tempLores = new ArrayList<String>();

							for (int i = 0; i < oldLores.size(); i++) {
								if (oldLores.get(i).contains("{BADGE}")) {

									tempLores.add(oldLores.get(i)
											.replace(
													"{BADGE}",
													gym.getBadge().name()
															.toUpperCase()));

								} else if (oldLores.get(i).contains("{STATUS}")) {
									tempLores.add(oldLores.get(i).replace(
											"{STATUS}",
											gym.isOpen() ? "&a&lOPEN"
													: "&4&lCLOSED"));
								} else {
									tempLores.add(oldLores.get(i));
								}
							}

							for (String s : tempLores) {
								lores.add(ChatColor
										.translateAlternateColorCodes('&', s));
							}

							ItemStack item = new ItemStack(itemID);
							ItemMeta itemMeta = item.getItemMeta();
							itemMeta.setDisplayName(itemName);
							itemMeta.setLore(lores);
							item.setItemMeta(itemMeta);
							items.add(item);
						}

					}
				}
			}
		}

		for (ItemStack i : items) {
			gymMenu.addItem(i);
		}
	}

	public Gym getPlayerGym(Player player) {

		if (!this.isGymLeader(player)) {
			return null;
		}

		for (GymPermission gp : GymPermission.values()) {

			if (player.hasPermission(gp.getPermission())) {

				return gp.getGym();
			}

		}

		return null;

	}

	@SuppressWarnings("deprecation")
	public void openBadgeInventory(Player player) {

		for (Entry<String, List<Badge>> e : this.getBadgeAccounts().entrySet()) {
			if (Bukkit.getPlayer(e.getKey()) == player) {
				List<Badge> badges = e.getValue();

				Inventory inventory = Bukkit.createInventory(player, 9,
						player.getName() + "'s Badges");

				for (Badge badge : badges) {
					inventory.addItem(new ItemStack(badge.getID()));
				}

				player.openInventory(inventory);

			}
		}

	}

	@SuppressWarnings("deprecation")
	public void openBadgeInventory(Player player, Player other) {

		for (Entry<String, List<Badge>> e : this.getBadgeAccounts().entrySet()) {
			if (Bukkit.getPlayer(e.getKey()) == other) {
				List<Badge> badges = e.getValue();

				Inventory inventory = Bukkit.createInventory(other, 9,
						other.getName() + "'s Badges");

				for (Badge badge : badges) {
					inventory.addItem(new ItemStack(badge.getID()));
				}

				player.openInventory(inventory);

			}
		}

	}

	public List<Badge> getBadges(Player player) {
		for (Entry<String, List<Badge>> e : this.getBadgeAccounts().entrySet()) {
			if (Bukkit.getPlayer(e.getKey()) == player) {
				List<Badge> badges = e.getValue();

				return badges;

			}
		}
		return null;
	}

	public boolean hasBadgeAccount(String name) {

		if (this.getBadgeAccounts().containsKey(name)) {
			return true;
		}

		return false;
	}

	public void createBadgeAccount(String name) {

		List<Badge> newBadges = new ArrayList<Badge>();

		this.getBadgeAccounts().put(name, newBadges);

		getDataManager().flushData(badges);

	}
}

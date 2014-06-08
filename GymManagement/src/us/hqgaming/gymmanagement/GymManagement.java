package us.hqgaming.gymmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import us.hqgaming.gymmanagement.badge.Badge;
import us.hqgaming.gymmanagement.badge.GiveBadge;
import us.hqgaming.gymmanagement.badge.RemoveBadge;
import us.hqgaming.gymmanagement.badge.SeeBadge;
import us.hqgaming.gymmanagement.commands.CommandManager;
import us.hqgaming.gymmanagement.commands.CommandType;
import us.hqgaming.gymmanagement.commands.PixelmonCommand;
import us.hqgaming.gymmanagement.gym.CloseGym;
import us.hqgaming.gymmanagement.gym.Gym;
import us.hqgaming.gymmanagement.gym.LeadersGym;
import us.hqgaming.gymmanagement.gym.OpenGym;

/** --- Gym Management ---
 * 
 * @version 1.4
 * @author Xeno */

@SuppressWarnings("deprecation")
public class GymManagement extends JavaPlugin implements Listener {

	private static HashMap<String, List<Badge>> badges = new HashMap<String, List<Badge>>();
	private static List<Gym> gyms = new ArrayList<Gym>();
	public static List<ItemStack> items = new ArrayList<ItemStack>();
	public List<PixelmonCommand> cmds = new ArrayList<PixelmonCommand>();

	public static String prefix;
	public static Inventory gymMenu;
	public static boolean newFile = false;
	private CommandManager cmdmanager;

	private int gymSlots;
	private int badgeSlots;

	public void onEnable() {

		saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);

		loadData();
		loadConfiguration();
		registerGyms();
		registerPermissions();
		registerCommands();
		updateGymMenu();

	}

	public void onDisable() {

		getDataManager().saveData(badges);
	}

	public void loadConfiguration() {

		gymSlots = getConfig().getInt("Gym-Slots");
		badgeSlots = getConfig().getInt("Badge-Slots");
		prefix = getConfig().getString("Chat-Prefix");

	}

	public void registerCommands() {
		cmdmanager = new CommandManager(this);

		// Load Commands
		for (Entry<String, Map<String, Object>> set : this.getDescription()
				.getCommands().entrySet()) {

			getCommand(set.getKey()).setExecutor(this.getCommandManager());

		}

		cmds.add(new GiveBadge(this));
		cmds.add(new RemoveBadge(this));
		cmds.add(new SeeBadge(this));
		cmds.add(new OpenGym(this));
		cmds.add(new CloseGym(this));
		cmds.add(new LeadersGym(this));
	}

	public void registerPermissions() {

		for (Gym gym : gyms) {

			Permission perm = new Permission("gym."
					+ gym.getGymName().toLowerCase());
			perm.setDefault(PermissionDefault.OP);
			Bukkit.getServer().getPluginManager().addPermission(perm);
			Bukkit.getLogger().info(
					"Gym Permission: " + perm.getName()
							+ " has been registered!");

		}

	}

	public void registerGyms() {

		new GymMenu(this);
		gymMenu = Bukkit.createInventory(null, gymSlots, "Gyms");

		for (String gymName : getConfig().getConfigurationSection("Gyms")
				.getKeys(false)) {

			Gym gym = new Gym(gymName);
			gym.setBadge(
					getConfig().getString("Gyms." + gymName + ".Badge-Name"),
					getConfig().getInt("Gyms." + gymName + ".Badge-Item"));
			gym.setChatName(getConfig().getString(
					"Gyms." + gymName + ".Gym-Name"));
			gym.setRunCommandIfClosed(getConfig().getBoolean(
					"Gyms." + gymName + ".Run-On-Click-Command-If-Gym-Closed"));
			gym.setItemClickCommandName(getConfig().getString(
					"Gyms." + gymName + ".On-Item-Click-Command-Name"));
			gym.setGymItemID(getConfig()
					.getInt("Gyms." + gymName + ".Gym-Item"));
			GymManagement.gyms.add(gym);

		}

	}

	@SuppressWarnings("unchecked")
	public void loadData() {
		getDataManager().setupData(this);
		if (!newFile) {
			badges = (HashMap<String, List<Badge>>) this.getDataManager()
					.loadData(getDataManager().getData());
		} else {
			badges = new HashMap<String, List<Badge>>();
		}
	}

	public HashMap<String, List<Badge>> getBadgeAccounts() {

		return badges;
	}

	public DataManager getDataManager() {
		return DataManager.getInstance();
	}

	public int getGymSlots() {
		return gymSlots;
	}

	public int getBadgeSlots() {
		return badgeSlots;
	}

	public String getPrefix() {
		return prefix;
	}

	public List<Gym> getGyms() {
		return gyms;
	}

	public CommandManager getCommandManager() {
		return cmdmanager;
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
			ChatManager.messagePlayer(p, "&e" + e.getPlayer().getName()
					+ " &a(" + gym.getChatName()
					+ " Leader&a)&c has logged in!");
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

		int counter = 0;
		for (Gym gym : gyms) {

			if (player.hasPermission("gym." + gym.getGymName())) {

				counter++;
			}

		}

		if (counter > 1 || counter == 0) {
			return false;

		}

		return true;

	}

	public boolean isGym(String gymname) {
		for (Gym gym : gyms) {
			if (gym.getGymName().equalsIgnoreCase(gymname)) {
				return true;
			}
		}
		return false;
	}

	public void helpMessage(CommandType commandType, Player player) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&cGym Management by &b&nXenoJava.com&r"));
		if (commandType == CommandType.BADGE) {
			player.sendMessage(ChatColor.RED
					+ "/badge give {player} <Optional: (gym name)> - Gives players your gym badge.");
			player.sendMessage(ChatColor.RED
					+ "/badge remove {player} <Optional: (gym name)> - Takes your gym badge away from players.");
			player.sendMessage(ChatColor.RED
					+ "/badge see {player} - Displays player's badges.");
		} else if (commandType == CommandType.GYM) {
			player.sendMessage(ChatColor.RED
					+ "/gym open <Optional: (gym name)> - Opens gym.");
			player.sendMessage(ChatColor.RED
					+ "/gym close <Optional: (gym name)> - Closes gym.");
			player.sendMessage(ChatColor.RED
					+ "/gym leaders - Lists online gym leaders.");
		}

	}

	public void clearInventory(Inventory inventory) {

		for (ItemStack i : inventory.getContents()) {

			if (i != null) {
				inventory.remove(i);
			}

		}

	}

	public void updateGymMenu() {
		clearInventory(gymMenu);
		items.clear();

		for (Gym gym : gyms) {

			String itemName = ChatColor.translateAlternateColorCodes('&',
					gym.getChatName());

			int itemID = getConfig().getInt(
					"Gyms." + gym.getGymName() + ".Gym-Item");

			if (itemID == 0) {
				itemID += gym.getBadge().getID();
			}

			List<String> oldLores = getConfig().getStringList(
					"Gyms." + gym.getGymName() + ".Item-Lore");
			List<String> lores = new ArrayList<String>();
			List<String> tempLores = new ArrayList<String>();

			for (int i = 0; i < oldLores.size(); i++) {
				if (oldLores.get(i).contains("{BADGE}")) {

					tempLores.add(oldLores.get(i).replace("{BADGE}",
							gym.getBadge().getBadgeName().toUpperCase()));

				} else if (oldLores.get(i).contains("{STATUS}")) {
					tempLores.add(oldLores.get(i).replace("{STATUS}",
							gym.isOpen() ? "&a&lOPEN" : "&4&lCLOSED"));
				} else {
					tempLores.add(oldLores.get(i));
				}
			}

			for (String s : tempLores) {
				lores.add(ChatColor.translateAlternateColorCodes('&', s));
			}

			ItemStack item = new ItemStack(itemID);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(itemName);
			itemMeta.setLore(lores);
			item.setItemMeta(itemMeta);
			items.add(item);
		}

		for (ItemStack i : items) {
			gymMenu.addItem(i);
		}
	}

	public Gym getPlayerGym(Player player) {

		if (!this.isGymLeader(player)) {
			return null;
		}

		for (Gym gym : gyms) {

			if (player.hasPermission("gym." + gym.getGymName())) {

				return gym;
			}

		}

		return null;

	}

	public Gym getGym(String gymname) {

		for (Gym gym : gyms) {
			if (gym.getGymName().equalsIgnoreCase(gymname)) {
				return gym;
			}
		}
		return null;
	}

	public boolean hasOnlineLeaders() {
		for (Player p : Bukkit.getOnlinePlayers()) {

			if (isGymLeader(p)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasBadge(Player player, String name) {
		for (Entry<String, List<Badge>> e : this.getBadgeAccounts().entrySet()) {
			if (Bukkit.getPlayer(e.getKey()) == player) {
				List<Badge> badges = e.getValue();
				for (Badge badge : badges) {
					if (badge.getBadgeName().equalsIgnoreCase(name)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void removeBadge(Player player, String name) {
		for (Entry<String, List<Badge>> e : this.getBadgeAccounts().entrySet()) {
			if (Bukkit.getPlayer(e.getKey()) == player) {
				List<Badge> badges = e.getValue();
				Badge badge = null;
				for (Badge b : badges) {
					if (b.getBadgeName().equalsIgnoreCase(name)) {
						badge = b;
					}
				}

				e.getValue().remove(badge);
			}
		}
	}

	public void openBadgeInventory(Player player) {

		for (Entry<String, List<Badge>> e : this.getBadgeAccounts().entrySet()) {
			if (Bukkit.getPlayer(e.getKey()) == player) {
				List<Badge> badges = e.getValue();

				Inventory inventory = Bukkit.createInventory(player,
						this.getBadgeSlots(), player.getName() + "'s Badges");

				for (Badge badge : badges) {
					inventory.addItem(new ItemStack(badge.getID()));
				}

				player.openInventory(inventory);

			}
		}

	}

	public void openBadgeInventory(Player player, Player other) {

		for (Entry<String, List<Badge>> e : this.getBadgeAccounts().entrySet()) {
			if (Bukkit.getPlayer(e.getKey()) == other) {
				List<Badge> badges = e.getValue();

				Inventory inventory = Bukkit.createInventory(other,
						this.getBadgeSlots(), other.getName() + "'s Badges");

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

		getDataManager().saveData(badges);

	}
}

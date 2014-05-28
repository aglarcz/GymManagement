package us.hqgaming.gymmanagement;

import org.bukkit.ChatColor;

public enum Gym {

	FLYING(Badge.ZEPHYR, ChatColor
			.translateAlternateColorCodes('&', "&fFlying"), false), ROCK(
			Badge.BOULDER, ChatColor
					.translateAlternateColorCodes('&', "&7Rock"), false), WATER(
			Badge.CASCADE, ChatColor.translateAlternateColorCodes('&',
					"&9Water"), false), ELECTRIC(Badge.THUNDER, ChatColor
			.translateAlternateColorCodes('&', "&6Electric"), false), GRASS(
			Badge.RAINBOW, ChatColor.translateAlternateColorCodes('&',
					"&aGrass"), false), POISON(Badge.SOUL, ChatColor
			.translateAlternateColorCodes('&', "&5Poison"), false), PSYCHIC(
			Badge.THUNDER, ChatColor.translateAlternateColorCodes('&',
					"&ePsychic"), false), FIRE(Badge.VOLCANO, ChatColor
			.translateAlternateColorCodes('&', "&cFire"), false), BUG(
			Badge.HIVE, ChatColor.translateAlternateColorCodes('&', "&2Bug"),
			false), NORMAL(Badge.PLAIN, ChatColor.translateAlternateColorCodes(
			'&', "&eNORMAL"), false), GHOST(Badge.FOG, ChatColor
			.translateAlternateColorCodes('&', "&9Ghost"), false), FIGHTING(
			Badge.STORM, ChatColor.translateAlternateColorCodes('&',
					"&c&lFighting"), false), STEEL(Badge.MINERAL, ChatColor
			.translateAlternateColorCodes('&', "&7Steel"), false), ICE(
			Badge.GLACIER,
			ChatColor.translateAlternateColorCodes('&', "&bIce"), false), DRAGON(
			Badge.RISING, ChatColor.translateAlternateColorCodes('&',
					"&4Dragon"), false);

	private String name;
	private Badge badge;
	private boolean isOpen;

	Gym(Badge badge, String name, boolean isOpen) {
		this.name = name;
		this.badge = badge;
		this.isOpen = isOpen;
	}

	public String getGymName() {
		return name;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setStatus(boolean status) {
		isOpen = status;
	}

	public Badge getBadge() {
		return badge;
	}
}

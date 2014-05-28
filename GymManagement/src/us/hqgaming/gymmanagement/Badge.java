package us.hqgaming.gymmanagement;

public enum Badge {

	BOULDER(10458), ZEPHYR(10487), HIVE(10470), PLAIN(10477), FOG(10466), STORM(
			10484), MINERAL(10476), GLACIER(10468), RISING(10481), CASCADE(
			10459), THUNDER(10485), RAINBOW(10478), SOUL(10482), MARSH(10473), VOLCANO(
			10486);
	private int itemID;

	Badge(int itemID) {
		this.itemID = itemID;
	}

	public int getID() {
		return itemID;
	}
}

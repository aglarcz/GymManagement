package us.hqgaming.gymmanagement.gym;

import us.hqgaming.gymmanagement.badge.Badge;

public class Gym {

	private String chatName;
	private String itemClickCommandName;
	private Badge badge;
	private boolean status = false;
	private boolean isRunCommandIfClosed;
	private int gymItemID;
	private final String name;

	public Gym(String name) {
		this.name = name;
	}

	public String getGymName() {
		return name;
	}

	public boolean isOpen() {
		return status;
	}

	public void setRunCommandIfClosed(boolean isRunCommandIfClosed) {
		this.isRunCommandIfClosed = isRunCommandIfClosed;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setChatName(String chatName) {
		this.chatName = chatName;
	}

	public boolean isRunCommandIfClosed() {
		return this.isRunCommandIfClosed;
	}

	public void setGymItemID(int gymItemID) {
		this.gymItemID = gymItemID;
	}

	public void setItemClickCommandName(String itemClickCommandName) {
		this.itemClickCommandName = itemClickCommandName;
	}

	public void setBadge(String badgeName, int itemID) {
		this.badge = new Badge(badgeName);
		this.badge.setID(itemID);
	}

	public Badge getBadge() {
		return badge;
	}

	public String getChatName() {
		return chatName;
	}

	public int getGymItemID() {
		return gymItemID;
	}

	public String getItemClickCommandName() {
		return itemClickCommandName;
	}
}

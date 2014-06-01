package us.hqgaming.gymmanagement.badge;

import java.io.Serializable;

public class Badge implements Serializable {

	private static final long serialVersionUID = -6781313679137167727L;

	private int itemID;
	private final String name;

	public Badge(String name) {
		this.name = name;
	}

	public void setID(int itemID) {
		this.itemID = itemID;
	}

	public int getID() {
		return itemID;
	}

	public String getBadgeName() {
		return name;
	}
}

package us.hqgaming.gymmanagement.badge;

public class Badge {

    private String gymName;
    private int itemID;
    private final String name;

    public Badge(String name) {
        this.name = name;
    }

    public void setID(int itemID) {
        this.itemID = itemID;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public int getID() {
        return itemID;
    }

    public String getGymName() {
        return gymName;
    }

    public String getBadgeName() {
        return name;
    }
}

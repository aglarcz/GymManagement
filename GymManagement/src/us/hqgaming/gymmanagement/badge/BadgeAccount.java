package us.hqgaming.gymmanagement.badge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import us.hqgaming.gymmanagement.DataManager;
import us.hqgaming.gymmanagement.GymManagement;

public class BadgeAccount {

    private final String holder_name;
    private List<String> badges;

    public BadgeAccount(String holder_name) {
        this.holder_name = holder_name;
        GymManagement.getBadgeAccounts().add(this);
    }

    public String getHolderName() {
        return holder_name;
    }

    public void addBadge(Badge badge) {
        String badge_name = badge.getBadgeName();
        if (!badges.contains(badge_name)) {
            badges.add(badge_name);
        }
    }

    public void removeBadge(Badge badge) {
        String badge_name = badge.getBadgeName();
        if (badges.contains(badge_name)) {
            badges.remove(badge_name);
        }
    }

    public boolean hasBadge(Badge badge) {
        return badges.contains(badge.getBadgeName());
    }

    public List<String> getBadges() {
        return badges;
    }

    public void save(DataManager data) throws IOException {
        data.getData().set(holder_name, badges);
        data.saveData();
    }

    public boolean load(DataManager data) {
        if (!data.getData().contains(holder_name)) {
            badges = new ArrayList<String>();
            return false;
        }

        badges = data.getData().getStringList(holder_name);
        return true;
    }

}

package us.hqgaming.gymmanagement.gym;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.hqgaming.gymmanagement.GymManagement;
import us.hqgaming.gymmanagement.utils.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static us.hqgaming.gymmanagement.gym.GymStatus.*;

public class Gym {

    private String name;
    private String chatName;
    private String command;
    private List<String> leaders;
    private Location home;

    private Badge badge;

    private boolean run_command_closed;
    private GymStatus status = CLOSED;
    private int itemid;

    private static ArrayList<Gym> gyms = new ArrayList<Gym>();
    private Yaml yaml;

    private String[] rules;
    private LeaderDisplay leaderDisplay;

    public Gym(String name) {
        this.name = name;
        this.leaderDisplay = new LeaderDisplay(this);
        this.yaml = (Yaml) GymManagement.getDataManager().get(2, this);
        this.load();
        getGyms().add(this);
    }

    public static Gym getGym(String gymname) {
        for (Gym gym : gyms)
            if (gym.getName().equalsIgnoreCase(gymname)) return gym;
        return null;
    }

    public boolean isLeader(GymAccount account) {
        return leaders.contains(account.getHolderName());
    }

    public void addLeader(GymAccount account) {
        this.getLeaders().add(account.getHolderName());
        GymManagement.getMenu().update();
        this.save();
    }

    public void removeLeader(GymAccount account) {
        this.getLeaders().remove(account.getHolderName());
        GymManagement.getMenu().update();
        this.save();
    }

    public static ArrayList<Gym> getGyms() {
        return gyms;
    }

    public List<String> getLeaders() {
        return leaders;
    }

    public Location getHome() {
        return this.home;
    }

    public void setHome(Location home) {
        this.home = home;
        this.save();
    }

    public boolean hasHome() {
        return home != null;
    }

    public void deleteHome() {
        this.getYaml().set("Home", null);
        this.home = null;
        this.save();
    }

    public String getName() {
        return name;
    }

    public GymStatus getStatus() {
        return status;
    }

    public boolean hasRules() {
        return rules != null;
    }

    public void setRunCommandClosed(boolean isRunCommandIfClosed) {
        this.run_command_closed = isRunCommandIfClosed;
    }

    public void setStatus(GymStatus status) {
        this.status = status;
        GymManagement.getMenu().update();
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public boolean runCommandClosed() {
        return this.run_command_closed;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setBadge(String name, int itemID) {
        this.badge = new Badge(name);
        this.badge.setGymName(this.getName());
        this.badge.setID(itemID);
    }

    public void decipher(Object o) throws Exception {
        if (o instanceof File) {
            File file = (File) o;
            ArrayList<String> rules = new ArrayList<String>();
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                rules.add(str);
            }
            in.close();
            this.rules = rules.toArray(new String[rules.size()]);
        }
    }

    public void sendRules(CommandSender sender){
        for(String s : rules) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
    }

    public Badge getBadge() {
        return badge;
    }

    public String getChatName() {
        return chatName;
    }

    public int getItemid() {
        return itemid;
    }

    public String[] getRules() {
        return this.rules;
    }

    public String getCommand() {
        return command;
    }

    public Yaml getYaml() {
        return yaml;
    }

    public LeaderDisplay getLeaderDisplay() {
        return leaderDisplay;
    }

    public void save() {
        if (home != null) {
            this.getYaml().set("Home.x", home.getX());
            this.getYaml().set("Home.y", home.getY());
            this.getYaml().set("Home.z", home.getZ());

            this.getYaml().set("Home.yaw", home.getYaw());
            this.getYaml().set("Home.pitch", home.getPitch());

            this.getYaml().set("Home.world", home.getWorld().getName());
        }

        this.getYaml().set("Leaders", leaders);
        this.getYaml().save();
    }

    public boolean load() {
        if (!this.getYaml().contains("Leaders")) {
            leaders = new ArrayList<String>();
        } else {
            leaders = this.getYaml().getStringList("Leaders");
        }

        if (this.getYaml().contains("Home")) {
            int x = this.getYaml().getInteger("Home.x");
            int y = this.getYaml().getInteger("Home.y");
            int z = this.getYaml().getInteger("Home.z");

            float yaw = this.getYaml().getInteger("Home.yaw");
            float pitch = this.getYaml().getInteger("Home.pitch");

            World world = Bukkit.getWorld(this.getYaml().getString("Home.world"));

            this.home = new Location(world, x, y, z, yaw, pitch);
        }


        return true;
    }

    public class LeaderDisplay {

        final Gym gym;

        public LeaderDisplay(Gym gym) {
            this.gym = gym;
        }

        public String showOffline(boolean offline) {
            StringBuilder leaders = new StringBuilder();
            for (int i = 0; i < gym.getLeaders().size(); i++) {
                if (Bukkit.getPlayer(gym.getLeaders().get(i)) != null) {
                    if (offline) {
                        leaders.append("&f[&a*&f]&a").append(gym.getLeaders().get(i));
                    } else {
                        leaders.append("&a").append(gym.getLeaders().get(i));
                    }
                    continue;
                } else if (!offline) continue;
                if (i == 0) {
                    leaders.append(gym.getLeaders().get(i));
                    continue;
                }
                leaders.append(", &c").append(gym.getLeaders().get(i));
            }

            if(leaders.toString().equals("")){
                return "None";
            }

            return ChatColor.translateAlternateColorCodes('&', leaders.toString());
        }

    }
}

/*     */ package us.hqgaming.gymmanagement.managers;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import us.hqgaming.gymmanagement.GymManagement;
/*     */ import us.hqgaming.gymmanagement.gym.Badge;
/*     */ import us.hqgaming.gymmanagement.gym.Gym;
/*     */ import us.hqgaming.gymmanagement.gym.Gym.LeaderDisplay;
/*     */ import us.hqgaming.gymmanagement.gym.GymStatus;
/*     */ 
/*     */ public class MenuManager implements org.bukkit.event.Listener
/*     */ {
/*     */   public static Inventory menu;
/*  25 */   public static ArrayList<ItemStack> items = new ArrayList();
/*     */   
/*     */   public static Inventory getMenu()
/*     */   {
/*  29 */     return menu;
/*     */   }
/*     */   
/*     */   public MenuManager() {
/*  33 */     if (!GymManagement.GUI()) {
/*  34 */       return;
/*     */     }
/*  36 */     menu = Bukkit.createInventory(null, GymManagement.getInstance().getGymSize(), "Gyms");
/*  37 */     Bukkit.getServer().getPluginManager().registerEvents(this, GymManagement.getInstance());
/*  38 */     update();
/*     */   }
/*     */   
/*     */   public void update() {
/*  42 */     clearInventory(menu);
/*  43 */     items.clear();
/*  44 */     for (Gym gym : Gym.getGyms()) {
/*  45 */       String itemName = ChatColor.translateAlternateColorCodes('&', gym.getChatName());
/*     */       
/*  47 */       int itemID = GymManagement.getInstance().getConfig().getInt("Gyms." + gym.getName() + ".Gym-Item");
/*     */       
/*  49 */       if (itemID == 0) itemID += gym.getBadge().getID();
/*  50 */       List<String> oldLores = GymManagement.getInstance().getConfig().getStringList("Gyms." + gym.getName() + ".Item-Lore");
/*     */       
/*  52 */       List<String> lores = new ArrayList();
/*  53 */       List<String> tempLores = new ArrayList();
/*     */       
/*  55 */       String badge_variable = "{BADGE}";
/*  56 */       String status_variable = "{STATUS}";
/*  57 */       String online_variable = "{ONLINE}";
/*  58 */       String leaders_variable = "{LEADERS}";
/*     */       
/*  60 */       for (String oldLore : oldLores) {
/*  61 */         if (oldLore.contains("{BADGE}")) {
/*  62 */           tempLores.add(oldLore.replace("{BADGE}", gym.getBadge().getName().toUpperCase()));
/*     */         }
/*  64 */         else if (oldLore.contains("{STATUS}")) {
/*  65 */           tempLores.add(oldLore.replace("{STATUS}", gym.getStatus().getDisplay()));
/*     */         }
/*  67 */         else if (oldLore.contains("{LEADERS}")) {
/*  68 */           tempLores.add(oldLore.replace("{LEADERS}", gym.getLeaderDisplay().showOffline(true)));
/*     */         }
/*  70 */         else if (oldLore.contains("{ONLINE}")) {
/*  71 */           tempLores.add(oldLore.replace("{ONLINE}", gym.getLeaderDisplay().showOffline(false)));
/*     */         }
/*     */         else {
/*  74 */           tempLores.add(oldLore);
/*     */         }
/*     */       }
/*     */       String s;
/*  78 */       for (Iterator i$ = tempLores.iterator(); i$.hasNext(); lores.add(ChatColor.translateAlternateColorCodes('&', s))) { s = (String)i$.next();
/*     */       }
/*  80 */       ItemStack item = new ItemStack(itemID);
/*  81 */       if (item == null) {
/*  82 */         item.setType(Material.AIR);
/*     */       }
/*  84 */       ItemMeta itemMeta = item.getItemMeta();
/*  85 */       itemMeta.setDisplayName(itemName);
/*  86 */       itemMeta.setLore(lores);
/*  87 */       item.setItemMeta(itemMeta);
/*  88 */       items.add(item);
/*     */     }
/*     */     ItemStack i;
/*  91 */     for (Iterator i$ = items.iterator(); i$.hasNext(); menu.addItem(new ItemStack[] { i })) i = (ItemStack)i$.next();
/*     */   }
/*     */   
/*     */   public void clearInventory(Inventory inventory) {
/*  95 */     for (ItemStack i : inventory.getContents()) {
/*  96 */       if (i != null) inventory.remove(i);
/*     */     }
/*     */   }
/*     */   
/*     */   @org.bukkit.event.EventHandler
/*     */   public void onClick(InventoryClickEvent e) {
/* 102 */     Player player = (Player)e.getWhoClicked();
/*     */     
/* 104 */     if (!e.getInventory().getTitle().equalsIgnoreCase("Gyms")) {
/* 105 */       return;
/*     */     }
/* 107 */     if (e.getCurrentItem() != null)
/*     */     {
/* 109 */       for (Gym gym : Gym.getGyms())
/*     */       {
/* 111 */         if (e.getCurrentItem().getTypeId() == gym.getItemid())
/*     */         {
/* 113 */           if ((gym.getStatus() == GymStatus.CLOSED) && (
/* 114 */             (!gym.runCommandClosed()) || (gym.getCommand().equalsIgnoreCase("none")) || (gym.getCommand().equalsIgnoreCase("")))) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 120 */           player.performCommand(gym.getCommand());
/* 121 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 126 */     e.setCancelled(true);
/* 127 */     player.playSound(player.getLocation(), org.bukkit.Sound.CLICK, 1.0F, 1.0F);
/*     */   }
/*     */   
/*     */   public static void showMenu(Player player) {
/* 131 */     if (!player.hasPermission("gym.access.menu")) {
/* 132 */       us.hqgaming.gymmanagement.Chat.message(player, "&cYou do not have permission to execute this command.");
/*     */       
/* 134 */       return;
/*     */     }
/*     */     
/* 137 */     if (GymManagement.GUI()) {
/* 138 */       player.openInventory(getMenu());
/* 139 */       return;
/*     */     }
/*     */     
/* 142 */     player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a== &cGYMS &a=="));
/* 143 */     for (Gym gym : Gym.getGyms()) {
/* 144 */       player.sendMessage(ChatColor.GREEN + "/gym stats " + gym.getName().toLowerCase());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Lukas\Desktop\GymManagement.jar!\us\hqgaming\gymmanagement\managers\MenuManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
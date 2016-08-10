/*    */ package us.hqgaming.gymmanagement.managers;
/*    */ 
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.inventory.InventoryClickEvent;
/*    */ import org.bukkit.event.player.PlayerJoinEvent;
/*    */ import org.bukkit.inventory.Inventory;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ import us.hqgaming.gymmanagement.GymManagement;
/*    */ import us.hqgaming.gymmanagement.gym.Gym;
/*    */ import us.hqgaming.gymmanagement.gym.GymAccount;
/*    */ 
/*    */ public class EventManager implements Listener
/*    */ {
/* 19 */   private UpdateManager updater = GymManagement.getUpdater();
/*    */   
/*    */   @EventHandler(priority=EventPriority.HIGHEST)
/*    */   public void onJoin(PlayerJoinEvent e) {
/* 23 */     String name = e.getPlayer().getName();
/*    */     
/* 25 */     GymAccount account = GymAccount.getAccount(name);
/* 26 */     String gym_name = null;
/*    */     
/* 28 */     if (account.getApplicableGyms().length == 0)
/* 29 */       return;
/* 30 */     if (account.getApplicableGyms().length > 1) {
/* 31 */       gym_name = "&e&lMulti Gym";
/* 32 */     } else if (account.getApplicableGyms().length == 1) {
/* 33 */       gym_name = account.getApplicableGyms()[0].getChatName();
/*    */     }
/*    */     
/* 36 */     for (Player p : Bukkit.getOnlinePlayers()) {
/* 37 */       p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&ePixelmon&f] &b&l" + e.getPlayer().getDisplayName() + " &a&l(" + gym_name + " Leader&a&l)&b&l has joined the server!"));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   @EventHandler(priority=EventPriority.MONITOR)
/*    */   public void handleUpdate(PlayerJoinEvent event)
/*    */   {
/* 45 */     final Player p = event.getPlayer();
/* 46 */     if ((this.updater.update()) && (!this.updater.getLatestUpdate().equals("null")) && (p.hasPermission("gymmanagement.update")))
/*    */     {
/* 48 */       Bukkit.getScheduler().runTaskLater(GymManagement.getInstance(), new org.bukkit.scheduler.BukkitRunnable()
/*    */       {
/*    */         public void run() {
/* 51 */           p.sendMessage("§f[§6GymManagement§f] §7" + EventManager.this.updater.getLatestUpdate() + " §eis now available!");
/*    */           
/* 53 */           p.sendMessage("§f[§6GymManagement§f] §ehttp://dev.bukkit.org/bukkit-plugins/gymmanagement/");
/* 54 */           p.sendMessage("§f[§6GymManagement§f] §eRun §7/gymmanagement update §eto update now."); } }, 110L);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   @EventHandler
/*    */   public void onBadgesClick(InventoryClickEvent e)
/*    */   {
/* 62 */     if (!(e.getWhoClicked() instanceof Player)) return;
/* 63 */     Inventory inventory = e.getInventory();
/* 64 */     if (inventory.getTitle().contains("Badges")) e.setCancelled(true);
/*    */   }
/*    */ }


/* Location:              C:\Users\Lukas\Desktop\GymManagement.jar!\us\hqgaming\gymmanagement\managers\EventManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
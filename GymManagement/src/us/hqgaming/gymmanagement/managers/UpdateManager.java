/*     */ package us.hqgaming.gymmanagement.managers;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import us.hqgaming.gymmanagement.Chat;
/*     */ import us.hqgaming.gymmanagement.GymManagement;
/*     */ import us.hqgaming.gymmanagement.gym.GymAccount;
/*     */ import us.hqgaming.gymmanagement.utils.Log;
/*     */ import us.hqgaming.gymmanagement.utils.Updater;
/*     */ import us.hqgaming.gymmanagement.utils.Updater.UpdateType;
/*     */ 
/*     */ public class UpdateManager
/*     */ {
/*     */   private int config_version;
/*     */   private boolean update;
/*  24 */   private String latestUpdate = "null";
/*     */   
/*     */   private Plugin plugin;
/*  27 */   private final int VERSION = 6;
/*     */   
/*     */   public UpdateManager(Plugin plugin) {
/*  30 */     this.plugin = plugin;
/*  31 */     init();
/*     */   }
/*     */   
/*     */   public boolean update() {
/*  35 */     return this.update;
/*     */   }
/*     */   
/*     */   public String getLatestUpdate() {
/*  39 */     return this.latestUpdate;
/*     */   }
/*     */   
/*     */   public int getConfigVersion() {
/*  43 */     return this.config_version;
/*     */   }
/*     */   
/*     */   public int getVersion() {
/*  47 */     return 6;
/*     */   }
/*     */   
/*     */   private void init()
/*     */   {
/*  52 */     this.config_version = this.plugin.getConfig().getInt("Configuration");
/*  53 */     this.update = this.plugin.getConfig().getBoolean("Automatic-Updates");
/*     */     
/*  55 */     if (this.config_version != 6) {
/*  56 */       Log.severe("Updating your configuration ...");
/*  57 */       updateConfiguration();
/*     */     }
/*     */     
/*     */ 
/*  61 */     File badges_old = new File(this.plugin.getDataFolder(), "badges.yml");
/*     */     
/*  63 */     if (badges_old.exists()) {
/*  64 */       FileConfiguration badges = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(badges_old);
/*  65 */       int size = badges.getKeys(false).size();
/*     */       
/*  67 */       Log.severe("Updating your badges backends file, Opening accounts for " + size + " users ...");
/*  68 */       for (String s : badges.getKeys(false)) {
/*  69 */         GymAccount account = GymAccount.getAccount(s);
/*  70 */         account.getBadges().addAll(badges.getStringList(s));
/*  71 */         account.save();
/*     */       }
/*     */       
/*  74 */       Log.severe("Updated your badges backends file !");
/*  75 */       badges_old.delete();
/*     */     }
/*     */     
/*     */ 
/*  79 */     if (this.update) {
/*  80 */       this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new org.bukkit.scheduler.BukkitRunnable()
/*     */       {
/*     */ 
/*     */         public void run() {
/*  84 */           UpdateManager.this.updateCheck(); } }, 0L, 216000L);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void update(final CommandSender sender)
/*     */   {
/*  91 */     if (update()) {
/*  92 */       new Thread(new Runnable()
/*     */       {
/*     */         public void run() {
/*  95 */           Chat.message(sender, "&eRunning updater ..");
/*     */           
/*  97 */           Updater updater = new Updater(UpdateManager.this.plugin, 79053, GymManagement.getInstance().getDataFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
/*     */           
/*     */ 
/* 100 */           switch (UpdateManager.3.$SwitchMap$us$hqgaming$gymmanagement$utils$Updater$UpdateResult[updater.getResult().ordinal()]) {
/*     */           case 1: 
/* 102 */             Chat.message(sender, "&cUpdater failed! (Could not contact dev.bukkit.org)");
/* 103 */             break;
/*     */           case 2: 
/* 105 */             Chat.message(sender, "&cUpdater failed! (Failed to download file)");
/* 106 */             break;
/*     */           case 3: 
/* 108 */             Chat.message(sender, "&eDownload complete! (&7" + updater.getLatestName() + "&e)");
/*     */             
/* 110 */             Chat.message(sender, "&eRestart the server to apply the update.");
/*     */             
/* 112 */             break;
/*     */           
/*     */           }
/*     */           
/*     */         }
/*     */       }).start();
/*     */     } else {
/* 119 */       Chat.message(sender, "&cThe updater has not been enabled in the config!");
/*     */     }
/*     */   }
/*     */   
/*     */   private void updateCheck()
/*     */   {
/* 125 */     Log.info("Checking for new updates...");
/*     */     
/* 127 */     Updater updater = new Updater(this.plugin, 79053, GymManagement.getInstance().getDataFile(), Updater.UpdateType.NO_DOWNLOAD, false);
/*     */     
/*     */ 
/* 130 */     us.hqgaming.gymmanagement.utils.Updater.UpdateResult result = updater.getResult();
/* 131 */     switch (result) {
/*     */     case NO_UPDATE: 
/* 133 */       Log.info("No update was found.");
/* 134 */       break;
/*     */     case UPDATE_AVAILABLE: 
/* 136 */       this.latestUpdate = updater.getLatestName();
/* 137 */       Log.info("------------------------------------");
/* 138 */       Log.info(this.latestUpdate + " is now available!");
/* 139 */       Log.info("Run '/gymmanagement update' to update now.");
/* 140 */       Log.info("------------------------------------");
/* 141 */       break;
/*     */     case DISABLED: 
/* 143 */       Log.info("Update checking has been disabled in the updater config.");
/* 144 */       break;
/*     */     case FAIL_APIKEY: 
/* 146 */       Log.warning("The API key you have provided is incorrect!");
/* 147 */       break;
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */   public void updateConfiguration()
/*     */   {
/* 154 */     InputStream inputStream = null;
/* 155 */     OutputStream outputStream = null;
/*     */     try {
/* 157 */       inputStream = this.plugin.getResource("config.yml");
/* 158 */       File configFile = new File(this.plugin.getDataFolder() + "/config.yml");
/* 159 */       com.google.common.io.Files.copy(configFile, new File(this.plugin.getDataFolder(), "config." + getTime() + ".yml"));
/*     */       
/* 161 */       outputStream = new FileOutputStream(configFile);
/*     */       
/* 163 */       int read = 0;
/* 164 */       byte[] bytes = new byte['Ѐ'];
/*     */       
/* 166 */       while ((read = inputStream.read(bytes)) != -1) { outputStream.write(bytes, 0, read);
/*     */       }
/* 168 */       Log.severe("Your configuration file has been updated !"); return;
/*     */     }
/*     */     catch (IOException e) {
/* 171 */       e.printStackTrace();
/* 172 */       Log.severe("Could not update config.yml!");
/*     */     }
/*     */     finally
/*     */     {
/* 176 */       if (inputStream != null)
/* 177 */         try { inputStream.close();
/*     */         } catch (IOException e) {
/* 179 */           e.printStackTrace();
/*     */         }
/* 181 */       if (outputStream != null) {
/*     */         try {
/* 183 */           outputStream.close();
/*     */         } catch (IOException e) {
/* 185 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private String getTime() {
/* 192 */     Calendar cal = Calendar.getInstance();
/* 193 */     SimpleDateFormat sdf = new SimpleDateFormat("M.d.yyyy", java.util.Locale.ENGLISH);
/*     */     
/* 195 */     String time = sdf.format(cal.getTime());
/* 196 */     return time;
/*     */   }
/*     */ }


/* Location:              C:\Users\Lukas\Desktop\GymManagement.jar!\us\hqgaming\gymmanagement\managers\UpdateManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
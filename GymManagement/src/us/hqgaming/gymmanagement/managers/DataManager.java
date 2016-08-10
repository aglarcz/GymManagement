/*     */ package us.hqgaming.gymmanagement.managers;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import us.hqgaming.gymmanagement.gym.Gym;
/*     */ import us.hqgaming.gymmanagement.gym.GymAccount;
/*     */ import us.hqgaming.gymmanagement.utils.Log;
/*     */ import us.hqgaming.gymmanagement.utils.Yaml;
/*     */ 
/*     */ 
/*     */ public class DataManager
/*     */ {
/*  16 */   private static DataManager instance = new DataManager();
/*     */   private Plugin plugin;
/*     */   
/*  19 */   public static DataManager getInstance() { return instance; }
/*     */   
/*     */ 
/*     */ 
/*  23 */   private File[] dirs = new File[3];
/*     */   
/*  25 */   private ArrayList<Yaml> gyms = new ArrayList();
/*  26 */   private ArrayList<Yaml> accounts = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getDirectory(int index)
/*     */   {
/*  36 */     return this.dirs[(index - 1)];
/*     */   }
/*     */   
/*     */   public void setupData(Plugin plugin) {
/*  40 */     this.plugin = plugin;
/*  41 */     this.dirs[0] = new File(plugin.getDataFolder(), "accounts");
/*  42 */     this.dirs[1] = new File(plugin.getDataFolder(), "gyms");
/*  43 */     this.dirs[2] = new File(plugin.getDataFolder(), "rules");
/*     */     
/*  45 */     for (File dir : this.dirs) {
/*  46 */       if ((!dir.exists()) && (!dir.mkdir()))
/*  47 */         Log.severe(ChatColor.RED + "Could not create " + dir.getName() + " directory!");
/*     */     }
/*     */   }
/*     */   
/*     */   public Object get(int index, Object o) {
/*  52 */     switch (index) {
/*     */     case 1: 
/*  54 */       return getAccount((GymAccount)o);
/*     */     case 2: 
/*  56 */       return getGym((Gym)o);
/*     */     case 3: 
/*  58 */       return getRules((Gym)o);
/*     */     }
/*  60 */     return null;
/*     */   }
/*     */   
/*     */   private Yaml getAccount(GymAccount account) {
/*  64 */     for (Yaml yaml : this.accounts) {
/*  65 */       if (yaml.getFile().getName().equalsIgnoreCase(account.getHolderName() + ".yml")) {
/*  66 */         return yaml;
/*     */       }
/*     */     }
/*     */     
/*  70 */     Yaml yaml = new Yaml(new File(getDirectory(1), account.getHolderName() + ".yml"));
/*  71 */     this.accounts.add(yaml);
/*  72 */     return yaml;
/*     */   }
/*     */   
/*     */   private Yaml getGym(Gym gym) {
/*  76 */     for (Yaml yaml : this.gyms) {
/*  77 */       if (yaml.getFile().getName().equalsIgnoreCase(gym.getName() + ".yml")) {
/*  78 */         return yaml;
/*     */       }
/*     */     }
/*     */     
/*  82 */     Yaml yaml = new Yaml(new File(getDirectory(2), gym.getName() + ".yml"));
/*  83 */     this.gyms.add(yaml);
/*  84 */     return yaml;
/*     */   }
/*     */   
/*     */   private File getRules(Gym gym)
/*     */   {
/*  89 */     File rules_folder = new File(this.plugin.getDataFolder(), "rules");
/*  90 */     File gym_rules = new File(rules_folder, gym.getName() + ".txt");
/*  91 */     if (!gym_rules.exists())
/*     */       try {
/*  93 */         if (!gym_rules.createNewFile()) {
/*  94 */           Log.severe(ChatColor.RED + "Could not create rules file for gym: " + gym.getName());
/*     */         } else {
/*  96 */           Log.severe(ChatColor.RED + "Gym rule: " + gym.getName() + " has been created!");
/*     */         }
/*     */       } catch (IOException e) {
/*  99 */         e.printStackTrace();
/*     */       }
/* 101 */     return gym_rules;
/*     */   }
/*     */ }


/* Location:              C:\Users\Lukas\Desktop\GymManagement.jar!\us\hqgaming\gymmanagement\managers\DataManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
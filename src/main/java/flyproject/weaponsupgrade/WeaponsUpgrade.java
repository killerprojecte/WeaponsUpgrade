package flyproject.weaponsupgrade;

import flyproject.blockcatcher.BlockCatcherAPI;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class WeaponsUpgrade extends JavaPlugin implements Listener {

    public static boolean debug = false;
    private static FileConfiguration item;

    @Override
    public void onEnable() {
        saveResource("items.yml",false);
        descriptionFile();
        if (getDescription().getVersion().contains("Beta")) getLogger().warning("这是一个测试版本 如有BUG请及时反馈");
        System.out.println("注册监听器...");
        Bukkit.getPluginManager().registerEvents(this,this);
        saveDefaultConfig();
        item = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/items.yml"));
        // Plugin startup logic

    }
    @EventHandler
    public void onPlayerAttackEntity(EntityDeathEvent event){
        if (!(event.getEntity().getKiller() instanceof Player))return;
        if (getConfig().getConfigurationSection("weapons").getKeys(false) == null){
            getLogger().warning("配置为空无法注册 请添加配置");
        }
        debug(event.getEntityType().name());
        debug("设置Player");
        Player p = (Player) event.getEntity().getKiller();
        debug("遍历开始");
        Bukkit.getScheduler().runTaskAsynchronously(this,() -> {
            for (String key : getConfig().getConfigurationSection("weapons").getKeys(false)){
                debug(key);
                if (getConfig().getString("weapons." + key + ".entity").equals(event.getEntityType().name())){
                    debug("满足实体");
                    if (!p.getItemInHand().getType().equals(Material.AIR) && p.getItemInHand().getItemMeta().getDisplayName().equals(cl(getConfig().getString("weapons." + key + ".displayname")))){
                        debug("满足名称");
                        ItemStack is = p.getItemInHand();
                        ItemMeta im = is.getItemMeta();
                        debug("Type: " + is.getType());
                        if (is.getType().toString().equals(getConfig().getString("weapons." + key + ".type"))){
                            debug("满足Type");
                            if (!im.hasLore()) continue;
                            List<String> lorei = Objects.requireNonNull(im.getLore());
                            List<String> newlore = new ArrayList();
                            String suffix = cl(getConfig().getString("weapons." + key + ".lore.suffix"));
                            String symbol = cl(getConfig().getString("weapons." + key + ".lore.symbol"));
                            int maxpoint = getConfig().getInt("weapons." + key + ".lore.max");
                            for (String txt : lorei){
                                if (!txt.startsWith(suffix))continue;
                                if (!txt.endsWith(i2s(maxpoint)))continue;
                                int point = getNowPoint(txt,suffix,symbol,maxpoint);
                                if (point<100 && point>=0){
                                    int np = point + 1;
                                    if (np>=100){
                                        short dura = is.getDurability();
                                        ItemStack old = is.clone();
                                        Map<Enchantment,Integer> encmap = old.getEnchantments();
                                        is = item.getItemStack(getConfig().getString("weapons." + key + ".upgrade.itemstack"));
                                        ItemMeta meta = is.getItemMeta();
                                        for (Enchantment enc : encmap.keySet()){
                                            int level = encmap.get(enc);
                                            if (is.getEnchantments().containsKey(enc)){
                                                int nlevel = is.getEnchantments().get(enc);
                                                if (level<=nlevel) continue;
                                                meta.addEnchant(enc,level,true);
                                            } else {
                                                meta.addEnchant(enc,level,true);
                                            }
                                        }
                                        is.setItemMeta(meta);
                                        is.setDurability(dura);
                                        p.setItemInHand(is);
                                        if (debug) getLogger().info(":>=100 Reward");
                                        return;
                                    } else {
                                        im.setLore(getNewList(lorei,suffix+point+symbol+maxpoint,suffix+np+symbol+maxpoint));
                                        is.setItemMeta(im);
                                        p.setItemInHand(is);
                                        if (debug) getLogger().info(":<100 Reward");
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event){
        if (getConfig().getConfigurationSection("mine").getKeys(false) == null){
            getLogger().warning("配置为空无法注册 请添加配置");
        }
        debug("设置Player");
        Player p = event.getPlayer();
        debug("遍历开始");
            for (String key : getConfig().getConfigurationSection("mine").getKeys(false)){
                debug(key);
                if (getConfig().getString("mine." + key + ".block").equals(event.getBlock().getType().name())){
                    debug("满足方块");
                    debug("Display: " + p.getItemInHand().getItemMeta().getDisplayName().replace("§","$"));
                    if (!p.getItemInHand().getType().equals(Material.AIR) && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName() && p.getItemInHand().getItemMeta().getDisplayName().equals(cl(getConfig().getString("mine." + key + ".displayname")))){
                        debug("满足名称");
                        ItemStack is = p.getItemInHand();
                        ItemMeta im = is.getItemMeta();
                        debug("Type: " + is.getType());
                        if (is.getType().toString().equals(getConfig().getString("mine." + key + ".type"))){
                            debug("满足Type");
                            if (!im.hasLore()) continue;
                            List<String> lorei = im.getLore();
                            List<String> newlore = new ArrayList();
                            String suffix = cl(getConfig().getString("mine." + key + ".lore.suffix"));
                            String symbol = cl(getConfig().getString("mine." + key + ".lore.symbol"));
                            int maxpoint = getConfig().getInt("mine." + key + ".lore.max");
                            for (String txt : lorei){
                                if (!txt.startsWith(suffix))continue;
                                if (!txt.endsWith(i2s(maxpoint)))continue;
                                if (!getConfig().getBoolean("mine." + key + ".replace",false)){
                                    if (BlockCatcherAPI.isReplace(event.getBlock())) return;
                                }
                                int point = getNowPoint(txt,suffix,symbol,maxpoint);
                                if (point<100 && point>=0){
                                    int np = point + 1;
                                    if (np>=100){
                                        short dura = is.getDurability();
                                        ItemStack old = is.clone();
                                        Map<Enchantment,Integer> encmap = old.getEnchantments();
                                        is = item.getItemStack(getConfig().getString("mine." + key + ".upgrade.itemstack"));
                                        ItemMeta meta = is.getItemMeta();
                                        for (Enchantment enc : encmap.keySet()){
                                            int level = encmap.get(enc);
                                            if (is.getEnchantments().containsKey(enc)){
                                                int nlevel = is.getEnchantments().get(enc);
                                                if (level<=nlevel) continue;
                                                meta.addEnchant(enc,level,true);
                                            } else {
                                                meta.addEnchant(enc,level,true);
                                            }
                                        }
                                        is.setItemMeta(meta);
                                        is.setDurability(dura);
                                        p.setItemInHand(is);
                                        if (debug) getLogger().info(":>=100 Reward");
                                        return;
                                    } else {
                                        im.setLore(getNewList(lorei,suffix+point+symbol+maxpoint,suffix+np+symbol+maxpoint));
                                        is.setItemMeta(im);
                                        p.setItemInHand(is);
                                        if (debug) getLogger().info(":<100 Reward");
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if (event.getInventory().getType().equals(InventoryType.SMITHING)){
            ItemStack is = event.getCurrentItem();
            ItemMeta im = is.getItemMeta();
            for (String key : getConfig().getConfigurationSection("weapons").getKeys(false)){
                if (im.getDisplayName().equals(cl(getConfig().getString("weapons." + key + ".displayname")))){
                    event.setCancelled(true);
                    return;
                }
            }
            for (String key : getConfig().getConfigurationSection("mine").getKeys(false)){
                if (im.getDisplayName().equals(cl(getConfig().getString("mine." + key + ".displayname")))){
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length==0){
            item = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/items.yml"));
            reloadConfig();
            sender.sendMessage(cl("&a/weu save <id> ———— 设置一个物品ID"));
            sender.sendMessage(cl("&a/weu get <id> ———— 获取一个已设定的物品"));
            sender.sendMessage(cl("&aWeaponsUpgrade 已重载"));
            return true;
        } else if (args.length==2){
            if (!(sender instanceof Player))return false;
            Player p = (Player) sender;
            reloadConfig();
            if (args[0].equalsIgnoreCase("save")){
                if (p.getItemInHand().getType().equals(Material.AIR)) {p.sendMessage(cl("&c请在手上持有物品"));
                    return false;}
                ItemStack is = p .getItemInHand();
                item.set(args[1],is);
                try {
                    item.save(new File(getDataFolder() + "/items.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                item = YamlConfiguration.loadConfiguration(new File(getDataFolder() + "/items.yml"));
                sender.sendMessage(cl("&a物品ID已被设置为: " + args[1]));
            } else {
                HashMap<Integer, ItemStack> undrop = p.getInventory().addItem(item.getItemStack(args[1]));
                if (undrop.size()==0){
                    sender.sendMessage(cl("&a物品已放置到您的背包!"));
                } else {
                    sender.sendMessage(cl("&a背包已满无法放置!"));
                }
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TextComponent getClickHoverText(String text, String hovertext, ClickEvent.Action action, String vaule){
        TextComponent mainComponent = new TextComponent(text);
        mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hovertext).create()));
        mainComponent.setClickEvent(new ClickEvent(action,vaule));
        return mainComponent;
    }

    public static String cl(String s){
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    public static int s2i(String s){
        return Integer.parseInt(s);
    }

    public static String i2s(int i){
        return String.valueOf(i);
    }
    public static int getNowPoint(String eqlore, String suffix, String symbol, int maxpoint){
        String ret = eqlore;
        ret = ret.replace(suffix,"");
        ret = ret.replace(symbol,"");
        ret = ret.replace(i2s(maxpoint),"");
        return s2i(ret);
    }
    public static List<String> getNewList(List<String> lores,String cs,String rs){
        List<String> newlore = new ArrayList();
        if (lores!=null){
            for (String txt : lores){
                if (txt.equals(cs)){
                    newlore.add(rs);
                } else {
                    newlore.add(txt);
                }
            }
            return newlore;
        } else {
            return null;
        }
    }
    public static PluginDescriptionFile des;

    private void descriptionFile(){
        des = getDescription();
    }
    public static void debug(String s){
        if (debug){
            System.out.println("[WEU][DEBUG] " + s);
        }
    }
}

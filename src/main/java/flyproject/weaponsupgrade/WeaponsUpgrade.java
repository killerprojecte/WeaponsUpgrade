package flyproject.weaponsupgrade;

import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class WeaponsUpgrade extends JavaPlugin {

    public static boolean debug = false;

    @Override
    public void onEnable() {
        getLogger().warning("这是一个测试版本 如有BUG请及时反馈");
        System.out.println("注册监听器...");
        saveDefaultConfig();
        // Plugin startup logic

    }
    @EventHandler
    public void onPlayerAttackEntity(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player))return;
        if (getConfig().getConfigurationSection("weapons").getKeys(false) == null){
            getLogger().warning("配置为空无法注册 请添加配置");
        }
        Player p = (Player) event.getDamager();
        for (String key : getConfig().getConfigurationSection("weapons").getKeys(false)){
            if (getConfig().getString("weapons." + key + ".entity").equals(event.getEntityType().getName())){
                if (p.getItemInHand().getItemMeta().getDisplayName().equals(cl(getConfig().getString("weapons." + key + ".displayname")))){
                    ItemStack is = p.getItemInHand();
                    ItemMeta im = is.getItemMeta();
                    if (is.getType().name().equals(getConfig().getString("weapons." + key + ".type"))){
                        List<String> lorei = Objects.requireNonNull(im.getLore());
                        List<String> newlore = new ArrayList();
                        String suffix = cl(getConfig().getString("weapons." + key + ".lore.suffix"));
                        String symbol = cl(getConfig().getString("weapons." + key + ".lore.symbol"));
                        int maxpoint = getConfig().getInt("weapons." + key + ".lore.max");
                        for (String txt : lorei){
                            if (!txt.startsWith(suffix))return;
                            if (!txt.endsWith(i2s(maxpoint)))return;
                            int point = getNowPoint(txt,suffix,symbol,maxpoint);
                            if (point<100 && point>=0){
                                int np = point + 1;
                                if (np>=100){
                                    short dura = is.getDurability();
                                    is = (ItemStack) getConfig().get("weapons." + key + ".upgrade.itemstack");
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))return false;
        Player p = (Player) sender;
        if (p.getItemInHand().getType().equals(Material.AIR)) {p.sendMessage(cl("&c请在手上持有物品"));
            return false;}
        ItemStack is = p .getItemInHand();
        p.spigot().sendMessage(getClickHoverText(cl("&a点击复制ItemStack"),cl("&8点击复制"), ClickEvent.Action.COPY_TO_CLIPBOARD,is.toString()));
        return super.onCommand(sender, command, label, args);
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
}

package flyproject.weaponsupgrade;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
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

    @Override
    public void onEnable() {
        getLogger().info("This is a Beta Tag Version! If find any bug pls tell me.");

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
                                String ugdisplay = cl(getConfig().getString("weapons." + key + ".upgrade.displayname"));
                                String uglore = cl(getConfig().getString("weapons." + key + ".upgrade.lore"));
                                if (np>=100){
                                    im.setDisplayName(ugdisplay);
                                    im.setLore(getNewList(lorei,suffix+maxpoint+symbol+maxpoint,uglore));
                                    is.setItemMeta(im);
                                    p.setItemInHand(is);
                                } else {

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

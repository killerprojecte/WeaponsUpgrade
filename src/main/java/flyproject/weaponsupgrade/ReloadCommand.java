package flyproject.weaponsupgrade;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(WeaponsUpgrade.cl("&aéè½½æå"));
        WeaponsUpgrade plugin = WeaponsUpgrade.getPlugin(WeaponsUpgrade.class);
        plugin.reloadConfig();
        return false;
    }
}

package fr.wivern.gambling.commands;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.kits.Kits;
import fr.wivern.gambling.util.command.ACommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GKitCommand extends ACommand {
    private Gambling gambling;

    public GKitCommand(Gambling gambling) {
        super(gambling, "gkit", "command.gkit", false);
        this.gambling = gambling;
    }

    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player)sender;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)null, this.gambling.getKitConfig().getInt("KIT-MENU.INVENTORY-SIZE"), ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME")));
        if (args.length != 0 && args.length != 1) {
            if (args.length == 2) {
                if (player.hasPermission(this.gambling.getKitConfig().getString("ADMIN-PERM"))) {
                    String kitName;
                    if (args[0].equalsIgnoreCase("create")) {
                        kitName = args[1];
                        this.gambling.getKitManager().createKit(kitName, player);
                    }

                    if (args[0].equalsIgnoreCase("remove")) {
                        kitName = args[1];
                        Kits kits = this.gambling.getKitManager().getKitByName(kitName);
                        if (kits == null) {
                            player.sendMessage(this.gambling.getConfigManager().getString("KIT-NOT-EXIST"));
                            return true;
                        }

                        this.gambling.getKitManager().deleteKit(player, kits);
                    }

                    if (args[0].equalsIgnoreCase("icon")) {
                        kitName = args[1];
                        if (this.gambling.getKitManager().getKitByName(kitName) != null) {
                            this.gambling.getKitManager().editIcon(kitName, player);
                        }
                    }
                } else {
                    this.gambling.getKitManager().openKitsMenu(inventory, player);
                }
            }

            return true;
        } else {
            this.gambling.getKitManager().openKitsMenu(inventory, player);
            return true;
        }
    }
}

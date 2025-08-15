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

        try {
            // Vérification de la configuration
            String kitMenuNameConfig = this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME");
            if (kitMenuNameConfig == null) {
                player.sendMessage("§cErreur: Configuration KIT-MENU.INVENTORY-NAME manquante dans kits.yml");
                return true;
            }

            String kitMenuName = ChatColor.translateAlternateColorCodes('&', kitMenuNameConfig);
            Inventory inventory = Bukkit.createInventory((InventoryHolder)null,
                    this.gambling.getKitConfig().getInt("KIT-MENU.INVENTORY-SIZE"),
                    kitMenuName);

            if (args.length == 0) {
                // Ouvrir le menu des kits
                this.gambling.getKitManager().openKitsMenu(inventory, player);
                return true;
            }

            if (args.length == 1) {
                // Ouvrir le menu des kits aussi
                this.gambling.getKitManager().openKitsMenu(inventory, player);
                return true;
            }

            if (args.length == 2) {
                // Vérifier les permissions admin
                String adminPermConfig = this.gambling.getKitConfig().getString("ADMIN-PERM");
                String adminPerm = adminPermConfig != null ? adminPermConfig : "command.gkit.admin";

                if (!player.hasPermission(adminPerm)) {
                    this.gambling.getKitManager().openKitsMenu(inventory, player);
                    return true;
                }

                String action = args[0];
                String kitName = args[1];

                if (action.equalsIgnoreCase("create")) {
                    if (kitName == null || kitName.trim().isEmpty()) {
                        player.sendMessage("§cVeuillez spécifier un nom pour le kit.");
                        return true;
                    }
                    this.gambling.getKitManager().createKit(kitName, player);
                    return true;
                }

                if (action.equalsIgnoreCase("remove")) {
                    Kits kits = this.gambling.getKitManager().getKitByName(kitName);
                    if (kits == null) {
                        String kitNotExistMsg = this.gambling.getConfigManager().getString("KIT-NOT-EXIST");
                        player.sendMessage(kitNotExistMsg != null ? kitNotExistMsg : "§cCe kit n'existe pas.");
                        return true;
                    }
                    this.gambling.getKitManager().deleteKit(player, kits);
                    return true;
                }

                if (action.equalsIgnoreCase("icon")) {
                    if (this.gambling.getKitManager().getKitByName(kitName) != null) {
                        this.gambling.getKitManager().editIcon(kitName, player);
                    } else {
                        String kitNotExistMsg = this.gambling.getConfigManager().getString("KIT-NOT-EXIST");
                        player.sendMessage(kitNotExistMsg != null ? kitNotExistMsg : "§cCe kit n'existe pas.");
                    }
                    return true;
                }

                // Action inconnue
                player.sendMessage("§cActions disponibles: create, remove, icon");
                return true;
            }

            // Trop d'arguments
            player.sendMessage("§cUtilisation: /gkit [create|remove|icon] [nom]");
            return true;

        } catch (Exception e) {
            player.sendMessage("§cErreur lors de l'exécution de la commande: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
}
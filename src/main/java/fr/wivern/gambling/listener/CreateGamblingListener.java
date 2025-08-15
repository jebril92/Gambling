package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CreateGamblingListener implements Listener {
    private final Gambling gambling;

    public CreateGamblingListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void processCommand(PlayerCommandPreprocessEvent event) {
        if (this.gambling.getGamblingManager().getPlayerMoneyWait().contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(this.gambling.getConfigManager().getString("CANNOT-USE-COMMAND"));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel") && this.gambling.getGamblingManager().getPlayerMoneyWait().contains(event.getPlayer())) {
            event.setCancelled(true);
            this.gambling.getGamblingManager().removePlayerMoney(player);
            player.sendMessage(this.gambling.getConfigManager().getString("CANCEL"));
            return;
        }

        if (this.gambling.getGamblingManager().getPlayerMoneyWait().contains(event.getPlayer())) {
            event.setCancelled(true);

            try {
                int money = Integer.parseInt(message);
                int moneyLimit = this.gambling.getConfigManager().getInt("LIMIT-MONEY");
                if (moneyLimit > money) {
                    player.sendMessage(this.gambling.getConfigManager().getString("SMALL-MONEY").replace("<money>", String.valueOf(moneyLimit)));
                    return;
                }

                if (this.gambling.getEconomy().getBalance(player) < (double)money) {
                    player.sendMessage(this.gambling.getConfigManager().getString("NOT-MONEY").replace("<money>", String.valueOf(money)));
                    return;
                }

                this.gambling.getKitManager().openKitSelector(player);
                this.gambling.getGamblingManager().removePlayerMoney(player);
                this.gambling.getGamblingManager().getPlayerMoney().put(player, money);
            } catch (NumberFormatException var6) {
                player.sendMessage(this.gambling.getConfigManager().getString("NOT-AVAILABLE-MONEY"));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Vérifications null de sécurité
        if (event.getInventory() == null || event.getInventory().getName() == null) {
            return;
        }

        if (!event.getInventory().getName().equalsIgnoreCase(this.gambling.getConfigManager().getString("KIT-SELECTOR.INVENTORY-NAME"))) {
            return;
        }

        event.setCancelled(true);

        // Vérifications null pour l'item
        if (event.getCurrentItem() == null ||
                event.getCurrentItem().getType() == null ||
                !event.getCurrentItem().hasItemMeta() ||
                event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE ||
                event.getCurrentItem().getItemMeta() == null ||
                event.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }

        String kitDisplayName = event.getCurrentItem().getItemMeta().getDisplayName();
        String kitPrefix = this.gambling.getConfigManager().getString("KIT-SELECTOR.KIT.NAME");

        if (kitPrefix == null) {
            ((Player)event.getWhoClicked()).sendMessage("§cErreur: Configuration KIT-SELECTOR.KIT.NAME manquante");
            return;
        }

        // Nettoyer le nom du kit en supprimant les codes de couleur ET le préfixe
        String translatedPrefix = ChatColor.translateAlternateColorCodes('&', kitPrefix);
        String kitName = ChatColor.stripColor(kitDisplayName.replace(translatedPrefix, ""));

        Player player = (Player)event.getWhoClicked();

        // Debug pour voir le nom du kit
        player.sendMessage("§eDEBUG - Kit sélectionné: '" + kitName + "'");

        if (this.gambling.getGamblingManager().getPlayerMoney().containsKey(player)) {
            int money = this.gambling.getGamblingManager().getPlayerMoney().get(player);
            player.performCommand("gambling create " + kitName + " " + money);
            player.closeInventory();
            this.gambling.getGamblingManager().getPlayerMoney().remove(player);
        } else {
            player.sendMessage("§cErreur: Aucun montant trouvé pour vous.");
            player.closeInventory();
        }
    }
}
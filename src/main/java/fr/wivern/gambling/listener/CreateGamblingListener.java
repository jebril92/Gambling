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
import org.bukkit.inventory.ItemStack;

public class CreateGamblingListener implements Listener {
    private final Gambling gambling;

    public CreateGamblingListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void processCommand(PlayerCommandPreprocessEvent event) {
        if (this.gambling.getGamblingManager().getPlayerMoneyWait().contains(event.getPlayer()) || this.gambling.getGamblingManager().getPlayerItemWait().contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(this.gambling.getConfigManager().getString("CANNOT-USE-COMMAND"));
        }

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (message.equalsIgnoreCase("item") && this.gambling.getGamblingManager().getPlayerItemWait().contains(event.getPlayer())) {
            event.setCancelled(true);
            if (this.gambling.getDisabledMaterialManager().getMaterialList().contains(player.getItemInHand().getType())) {
                player.sendMessage(this.gambling.getConfigManager().getString("ITEM-DISABLED"));
                return;
            }

            this.gambling.getKitManager().openKitSelector(player);
            this.gambling.getGamblingManager().removePlayerItem(player);
            this.gambling.getGamblingManager().getPlayerItem().put(player, player.getItemInHand());
        }

        if (message.equalsIgnoreCase("cancel") && (this.gambling.getGamblingManager().getPlayerItemWait().contains(event.getPlayer()) || this.gambling.getGamblingManager().getPlayerMoneyWait().contains(event.getPlayer()))) {
            event.setCancelled(true);
            this.gambling.getGamblingManager().removePlayerItem(player);
            this.gambling.getGamblingManager().removePlayerMoney(player);
            player.sendMessage(this.gambling.getConfigManager().getString("CANCEL"));
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
        if (event.getInventory().getName().equalsIgnoreCase(this.gambling.getConfigManager().getString("KIT-SELECTOR.INVENTORY-NAME"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta() || event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                return;
            }

            String kitName = event.getCurrentItem().getItemMeta().getDisplayName();
            String newName = kitName.replace(ChatColor.translateAlternateColorCodes('&', this.gambling.getConfigManager().getString("KIT-SELECTOR.KIT.NAME")), "");
            Player player = (Player)event.getWhoClicked();
            if (this.gambling.getGamblingManager().getPlayerMoney().containsKey(player)) {
                player.performCommand("gambling create " + newName + " " + this.gambling.getGamblingManager().getPlayerMoney().get(player));
                player.closeInventory();
                this.gambling.getGamblingManager().getPlayerMoney().remove(player);
            }

            if (this.gambling.getGamblingManager().getPlayerItem().containsKey(player)) {
                player.performCommand("gambling create " + newName + " item");
                player.getInventory().removeItem(new ItemStack[]{(ItemStack)this.gambling.getGamblingManager().getPlayerItem().get(player)});
                player.closeInventory();
                this.gambling.getGamblingManager().getPlayerItem().remove(player);
            }
        }

    }
}

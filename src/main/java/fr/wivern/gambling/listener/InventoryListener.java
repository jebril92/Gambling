package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {
    private Gambling gambling;

    public InventoryListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();

        // Vérifications null de sécurité
        if (event.getCurrentItem() == null ||
                event.getCurrentItem().getType() == null ||
                !event.getCurrentItem().hasItemMeta() ||
                event.getCurrentItem().getItemMeta() == null ||
                event.getInventory() == null ||
                event.getInventory().getName() == null) {
            return;
        }

        String gamblingMenuName = this.gambling.getInventoryManager().inventoryName(this.gambling.getConfigManager().getString("GAMBLING-MENU.INVENTORY-NAME"));

        if (!event.getInventory().getName().equalsIgnoreCase(gamblingMenuName)) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem().getType() == Material.SKULL_ITEM) {
            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            String nameColor = this.gambling.getConfigManager().getString("GAMBLING-MENU.SKULL.NAME-COLOR");

            if (displayName == null || nameColor == null) {
                return;
            }

            String targetName = displayName.replace(nameColor, "");
            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                player.sendMessage("§cCe joueur n'est plus en ligne.");
                player.closeInventory();
                return;
            }

            if (player == target) {
                player.sendMessage(this.gambling.getConfigManager().getString("CANNOT-DUEL-YOURSELF"));
                player.closeInventory();
                return;
            }

            if (this.gambling.getMatchManager().getPlayerWaitMap().containsKey(player)) {
                player.sendMessage(this.gambling.getConfigManager().getString("ALREADY-GAMBLING"));
                player.closeInventory();
                return;
            }

            this.gambling.getInventoryManager().openViewInventory(player, target);
        }

        String kitMaterialName = this.gambling.getConfigManager().getString("GAMBLING-MENU.KIT.MATERIAL");
        Material kitMaterial = kitMaterialName != null ? Material.getMaterial(kitMaterialName) : null;

        if (kitMaterial != null && event.getCurrentItem().getType() == kitMaterial) {
            String kitMenuName = this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME");
            if (kitMenuName != null) {
                Inventory inventory = Bukkit.createInventory((InventoryHolder)null,
                        this.gambling.getKitConfig().getInt("KIT-MENU.INVENTORY-SIZE"),
                        ChatColor.translateAlternateColorCodes('&', kitMenuName));
                this.gambling.getKitManager().openKitsMenu(inventory, player);
            }
        }
    }

    @EventHandler
    public void onStartDuel(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();

        // Vérifications null de sécurité
        if (event.getCurrentItem() == null ||
                event.getCurrentItem().getType() == null ||
                event.getInventory() == null ||
                event.getInventory().getName() == null) {
            return;
        }

        String viewMenuName = this.gambling.getInventoryManager().inventoryName(this.gambling.getConfigManager().getString("GAMBLING-VIEW.INVENTORY-NAME"));

        if (!event.getInventory().getName().equalsIgnoreCase(viewMenuName)) {
            return;
        }

        event.setCancelled(true);

        String startMaterialName = this.gambling.getConfigManager().getString("GAMBLING-VIEW.START.MATERIAL");
        Material startMaterial = startMaterialName != null ? Material.getMaterial(startMaterialName) : null;

        if (startMaterial == null || event.getCurrentItem().getType() != startMaterial) {
            return;
        }

        if (!event.getCurrentItem().hasItemMeta() ||
                event.getCurrentItem().getItemMeta() == null ||
                event.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }

        String playerNameColor = this.gambling.getConfigManager().getString("GAMBLING-VIEW.START.PLAYER-NAME-COLOR");
        if (playerNameColor == null) {
            return;
        }

        String targetName = event.getCurrentItem().getItemMeta().getDisplayName().replace(playerNameColor, "");
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            player.sendMessage("§cCe joueur n'est plus en ligne.");
            player.closeInventory();
            return;
        }

        if (!this.gambling.getMatchManager().getPlayerWaitMap().containsKey(target)) {
            player.sendMessage("§cCe joueur n'est plus en attente.");
            player.closeInventory();
            return;
        }

        PlayerData targetData = this.gambling.getMatchManager().getPlayerWaitMap().get(target);
        if (targetData == null) {
            player.sendMessage("§cErreur: Données du joueur introuvables.");
            player.closeInventory();
            return;
        }

        String kitName = targetData.getKitName();

        // Seulement l'argent maintenant
        if (!targetData.isUseMoney()) {
            player.sendMessage("§cErreur: Ce joueur utilise un mode non supporté.");
            player.closeInventory();
            return;
        }

        double money = targetData.getMoney();
        if (this.gambling.getEconomy().getBalance(player) < money) {
            player.sendMessage(this.gambling.getConfigManager().getString("DONT-HAVE-ENOUGHT-MONEY"));
            player.closeInventory();
            return;
        }

        if (this.gambling.getKitManager().getKitByName(kitName) == null) {
            player.sendMessage(this.gambling.getConfigManager().getString("NOT-AVAIBLE-KIT"));
            player.closeInventory();
            return;
        }

        this.startWithMoney(player, target, money, kitName);
    }

    private void startWithMoney(Player player, Player target, double money, String kitName) {
        this.gambling.getEconomy().withdrawPlayer(player, money);
        this.gambling.getMatchManager().startMatch(player, target);
        this.gambling.getGamblingManager().putDataWithMoney(target, kitName, money);
        this.gambling.getGamblingManager().putDataWithMoney(player, kitName, money);
        this.gambling.getMatchManager().getPlayerWaitMap().remove(target);
        this.gambling.getServer().getScheduler().runTaskLater(this.gambling, () -> {
            this.gambling.getKitManager().giveKit(kitName, player);
            this.gambling.getKitManager().giveKit(kitName, target);
        }, 20L);
    }
}
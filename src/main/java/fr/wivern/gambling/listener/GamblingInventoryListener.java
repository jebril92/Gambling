package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.inventory.GamblingInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GamblingInventoryListener implements Listener {
    private final Gambling gambling;

    public GamblingInventoryListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.INVENTORY-NAME"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta()) {
                return;
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.LIST.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.LIST.NAME"))) {
                this.gambling.getInventoryManager().openInventory((Player)event.getWhoClicked());
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.FIGHT.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.FIGHT.NAME"))) {
                (new GamblingInventory()).openFightMenu((Player)event.getWhoClicked(), this.gambling);
            }
        }

    }

    @EventHandler
    public void onClickStart(InventoryClickEvent event) {
        if (event.getInventory().getName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-CREATE.INVENTORY-NAME"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta()) {
                return;
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-CREATE.MONEY.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-CREATE.MONEY.NAME"))) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage(this.gambling.getConfigManager().getString("MESSAGE-CREATE-MONEY"));
                this.gambling.getGamblingManager().addPlayerMoney((Player)event.getWhoClicked());
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-CREATE.ITEM.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-CREATE.ITEM.NAME"))) {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage(this.gambling.getConfigManager().getString("MESSAGE-CREATE-ITEM"));
                this.gambling.getGamblingManager().addPlayerItem((Player)event.getWhoClicked());
            }
        }

    }
}

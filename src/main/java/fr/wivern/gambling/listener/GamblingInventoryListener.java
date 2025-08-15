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
        // Vérifications null de sécurité
        if (event.getInventory() == null || event.getInventory().getName() == null) {
            return;
        }

        if (!event.getInventory().getName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.INVENTORY-NAME"))) {
            return;
        }

        event.setCancelled(true);

        // Vérifications null pour l'item
        if (event.getCurrentItem() == null ||
                event.getCurrentItem().getType() == null ||
                !event.getCurrentItem().hasItemMeta() ||
                event.getCurrentItem().getItemMeta() == null ||
                event.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }

        Material listMaterial = Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.LIST.MATERIAL"));
        String listName = this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.LIST.NAME");
        Material fightMaterial = Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.FIGHT.MATERIAL"));
        String fightName = this.gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.FIGHT.NAME");

        if (listMaterial != null && listName != null &&
                event.getCurrentItem().getType() == listMaterial &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(listName)) {
            this.gambling.getInventoryManager().openInventory((Player)event.getWhoClicked());
        }

        if (fightMaterial != null && fightName != null &&
                event.getCurrentItem().getType() == fightMaterial &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(fightName)) {
            (new GamblingInventory()).openFightMenu((Player)event.getWhoClicked(), this.gambling);
        }
    }
}
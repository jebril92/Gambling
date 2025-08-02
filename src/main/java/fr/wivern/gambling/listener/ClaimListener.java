package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClaimListener implements Listener {
    private final Gambling gambling;

    public ClaimListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.INVENTORY-NAME"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || !event.getCurrentItem().hasItemMeta()) {
                return;
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.WIN.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.WIN.NAME"))) {
                this.gambling.getClaimManager().claimWinItems((Player)event.getWhoClicked());
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.LEAVE.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.LEAVE.NAME"))) {
                this.gambling.getClaimManager().claimLeaveItems((Player)event.getWhoClicked());
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.CRASH.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.CRASH.NAME"))) {
                this.gambling.getClaimManager().claimCrashItems((Player)event.getWhoClicked());
            }

            if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.BET.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(this.gambling.getConfigManager().getString("GAMBLING-CLAIM.BET.NAME"))) {
                this.gambling.getClaimManager().claimBetItems((Player)event.getWhoClicked());
            }

            event.getWhoClicked().closeInventory();
        }

    }
}

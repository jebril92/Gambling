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
        // Vérifications null de sécurité
        if (event.getInventory() == null ||
                event.getInventory().getName() == null ||
                event.getCurrentItem() == null ||
                event.getCurrentItem().getType() == null ||
                !event.getCurrentItem().hasItemMeta() ||
                event.getCurrentItem().getItemMeta() == null ||
                event.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }

        String claimInventoryName = this.gambling.getConfigManager().getString("GAMBLING-CLAIM.INVENTORY-NAME");
        if (claimInventoryName == null || !event.getInventory().getName().equalsIgnoreCase(claimInventoryName)) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player)event.getWhoClicked();

        // Vérification WIN
        String winMaterialName = this.gambling.getConfigManager().getString("GAMBLING-CLAIM.WIN.MATERIAL");
        String winName = this.gambling.getConfigManager().getString("GAMBLING-CLAIM.WIN.NAME");
        Material winMaterial = winMaterialName != null ? Material.getMaterial(winMaterialName) : null;

        if (winMaterial != null && winName != null &&
                event.getCurrentItem().getType() == winMaterial &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(winName)) {
            this.gambling.getClaimManager().claimWinItems(player);
            event.getWhoClicked().closeInventory();
            return;
        }

        // Vérification LEAVE
        String leaveMaterialName = this.gambling.getConfigManager().getString("GAMBLING-CLAIM.LEAVE.MATERIAL");
        String leaveName = this.gambling.getConfigManager().getString("GAMBLING-CLAIM.LEAVE.NAME");
        Material leaveMaterial = leaveMaterialName != null ? Material.getMaterial(leaveMaterialName) : null;

        if (leaveMaterial != null && leaveName != null &&
                event.getCurrentItem().getType() == leaveMaterial &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(leaveName)) {
            this.gambling.getClaimManager().claimLeaveItems(player);
            event.getWhoClicked().closeInventory();
            return;
        }

        // Vérification CRASH
        String crashMaterialName = this.gambling.getConfigManager().getString("GAMBLING-CLAIM.CRASH.MATERIAL");
        String crashName = this.gambling.getConfigManager().getString("GAMBLING-CLAIM.CRASH.NAME");
        Material crashMaterial = crashMaterialName != null ? Material.getMaterial(crashMaterialName) : null;

        if (crashMaterial != null && crashName != null &&
                event.getCurrentItem().getType() == crashMaterial &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(crashName)) {
            this.gambling.getClaimManager().claimCrashItems(player);
            event.getWhoClicked().closeInventory();
            return;
        }

        // Vérification BET
        String betMaterialName = this.gambling.getConfigManager().getString("GAMBLING-CLAIM.BET.MATERIAL");
        String betName = this.gambling.getConfigManager().getString("GAMBLING-CLAIM.BET.NAME");
        Material betMaterial = betMaterialName != null ? Material.getMaterial(betMaterialName) : null;

        if (betMaterial != null && betName != null &&
                event.getCurrentItem().getType() == betMaterial &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(betName)) {
            this.gambling.getClaimManager().claimBetItems(player);
            event.getWhoClicked().closeInventory();
            return;
        }

        // Si aucune correspondance, fermer l'inventaire
        event.getWhoClicked().closeInventory();
    }
}
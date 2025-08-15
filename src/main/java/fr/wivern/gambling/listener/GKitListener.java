package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.util.item.ItemBuilder;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GKitListener implements Listener {
    private Gambling gambling;

    public GKitListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onKitClic(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();

        // Vérifications null de sécurité
        if (event.getCurrentItem() == null ||
                event.getCurrentItem().getType() == null ||
                event.getInventory() == null ||
                event.getInventory().getName() == null) {
            return;
        }

        // Protection contre les valeurs null de configuration
        String kitMenuNameConfig = this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME");
        if (kitMenuNameConfig == null) {
            player.sendMessage("§cErreur: Configuration KIT-MENU.INVENTORY-NAME manquante");
            return;
        }

        String kitMenuName = ChatColor.translateAlternateColorCodes('&', kitMenuNameConfig);

        if (!event.getInventory().getName().equalsIgnoreCase(kitMenuName)) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
            return;
        }

        String backMaterialName = this.gambling.getKitConfig().getString("KIT-MENU.BACK.MATERIAL");
        Material backMaterial = backMaterialName != null ? Material.getMaterial(backMaterialName) : null;
        String backNameConfig = this.gambling.getKitConfig().getString("KIT-MENU.BACK.NAME");

        if (backMaterial != null && backNameConfig != null &&
                event.getCurrentItem().getType() == backMaterial &&
                event.getCurrentItem().hasItemMeta() &&
                event.getCurrentItem().getItemMeta() != null &&
                event.getCurrentItem().getItemMeta().getDisplayName() != null &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', backNameConfig))) {
            this.gambling.getInventoryManager().openInventory(player);
            return;
        }

        if (event.getCurrentItem().hasItemMeta() &&
                event.getCurrentItem().getItemMeta() != null &&
                event.getCurrentItem().getItemMeta().getDisplayName() != null &&
                backNameConfig != null &&
                !event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', backNameConfig))) {

            String kitDisplayName = event.getCurrentItem().getItemMeta().getDisplayName();
            String kitPrefixConfig = this.gambling.getKitConfig().getString("KIT-MENU.KIT.NAME");

            if (kitPrefixConfig != null) {
                String translatedPrefix = ChatColor.translateAlternateColorCodes('&', kitPrefixConfig);
                String kitName = ChatColor.stripColor(kitDisplayName.replace(translatedPrefix, ""));

                try {
                    ItemStack[] armor = this.gambling.getKitManager().getKitArmor(kitName);
                    ItemStack[] stuff = this.gambling.getKitManager().getKitStuff(kitName);

                    if (armor != null && stuff != null) {
                        this.openInventoryKit(kitName, player, armor, stuff);
                    }
                } catch (Exception e) {
                    player.sendMessage("§cErreur lors de l'ouverture du kit: " + e.getMessage());
                }
            }
        }
    }

    @EventHandler
    public void onPreviewClic(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();

        // Vérifications null de sécurité
        if (event.getCurrentItem() == null ||
                event.getCurrentItem().getType() == null ||
                event.getInventory() == null ||
                event.getInventory().getName() == null) {
            return;
        }

        String inventoryName = event.getInventory().getName();
        String kitViewPrefixConfig = this.gambling.getKitConfig().getString("KIT-VIEW.INVENTORY-NAME");

        if (kitViewPrefixConfig == null) {
            return;
        }

        String kitViewPrefix = ChatColor.translateAlternateColorCodes('&', kitViewPrefixConfig);

        if (!inventoryName.startsWith(kitViewPrefix)) {
            return;
        }

        String kitName = inventoryName.replace(kitViewPrefix, "");

        if (this.gambling.getKitManager().getKitByName(kitName) == null) {
            return;
        }

        event.setCancelled(true);

        if (!event.getCurrentItem().hasItemMeta() ||
                event.getCurrentItem().getItemMeta() == null) {
            return;
        }

        String backMaterialName = this.gambling.getKitConfig().getString("KIT-VIEW.BACK.MATERIAL");
        Material backMaterial = backMaterialName != null ? Material.getMaterial(backMaterialName) : null;
        String backNameConfig = this.gambling.getKitConfig().getString("KIT-VIEW.BACK.NAME");

        if (backMaterial != null && backNameConfig != null &&
                event.getCurrentItem().getType() == backMaterial &&
                event.getCurrentItem().getItemMeta().getDisplayName() != null &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', backNameConfig))) {

            String kitMenuNameConfig = this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME");
            if (kitMenuNameConfig != null) {
                Inventory inventory = Bukkit.createInventory((InventoryHolder)null,
                        this.gambling.getKitConfig().getInt("KIT-MENU.INVENTORY-SIZE"),
                        ChatColor.translateAlternateColorCodes('&', kitMenuNameConfig));
                this.gambling.getKitManager().openKitsMenu(inventory, player);
            }
        }
    }

    public void openInventoryKit(String kitName, Player player, ItemStack[] armorContents, ItemStack[] contents) {
        if (kitName == null || armorContents == null || contents == null) {
            return;
        }

        String kitViewNameConfig = this.gambling.getKitConfig().getString("KIT-VIEW.INVENTORY-NAME");
        if (kitViewNameConfig == null) {
            player.sendMessage("§cErreur: Configuration KIT-VIEW.INVENTORY-NAME manquante");
            return;
        }

        String kitViewName = ChatColor.translateAlternateColorCodes('&', kitViewNameConfig);

        Inventory inventory = Bukkit.createInventory((InventoryHolder)null,
                this.gambling.getKitConfig().getInt("KIT-VIEW.INVENTORY-SIZE"),
                kitViewName + kitName);

        // Ajouter les armures
        for (ItemStack armor : armorContents) {
            if (armor != null) {
                inventory.addItem(armor);
            }
        }

        // Ajouter les items à partir du slot 18
        int startPlace = 17;
        for (ItemStack item : contents) {
            startPlace++;
            if (startPlace < inventory.getSize()) {
                inventory.setItem(startPlace, item);
            }
        }

        // Ajouter les items de verre
        try {
            int glassData = this.gambling.getKitConfig().getInt("KIT-VIEW.GLASS-DATA");
            String glassNameConfig = this.gambling.getKitConfig().getString("KIT-MENU.GLASS-NAME");

            if (glassNameConfig != null) {
                String translatedGlassName = ChatColor.translateAlternateColorCodes('&', glassNameConfig);
                Iterator<String> glassSlots = this.gambling.getKitConfig().getStringList("KIT-VIEW.GLASS").iterator();

                while (glassSlots.hasNext()) {
                    String slotStr = glassSlots.next();
                    try {
                        int slot = Integer.parseInt(slotStr);
                        if (slot >= 0 && slot < inventory.getSize()) {
                            inventory.setItem(slot, (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)glassData)).setName(translatedGlassName).toItemStack());
                        }
                    } catch (NumberFormatException e) {
                        // Ignorer les slots invalides
                    }
                }
            }
        } catch (Exception e) {
            // Ignorer les erreurs de configuration des verres
        }

        // Ajouter le bouton retour
        try {
            String backMaterialName = this.gambling.getKitConfig().getString("KIT-VIEW.BACK.MATERIAL");
            Material backMaterial = backMaterialName != null ? Material.getMaterial(backMaterialName) : null;
            String backNameConfig = this.gambling.getKitConfig().getString("KIT-VIEW.BACK.NAME");

            if (backMaterial != null && backNameConfig != null) {
                ItemStack backButton = (new ItemBuilder(backMaterial))
                        .setName(ChatColor.translateAlternateColorCodes('&', backNameConfig))
                        .toItemStack();
                inventory.setItem(17, backButton);
            }
        } catch (Exception e) {
            // Ignorer les erreurs de configuration du bouton retour
        }

        player.openInventory(inventory);
    }
}
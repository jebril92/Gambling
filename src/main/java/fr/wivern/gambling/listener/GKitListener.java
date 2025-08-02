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
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != null) {
            if (event.getInventory().getName().equalsIgnoreCase(this.gambling.getInventoryManager().inventoryName(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME"))))) {
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getKitConfig().getString("KIT-MENU.BACK.MATERIAL")) && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.BACK.NAME")))) {
                    this.gambling.getInventoryManager().openInventory(player);
                } else if (event.getCurrentItem().hasItemMeta() && !event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.BACK.NAME")))) {
                    String kitName = event.getCurrentItem().getItemMeta().getDisplayName();
                    String newName = kitName.replace(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.KIT.NAME")), "");
                    this.openInventoryKit(newName, player, this.gambling.getKitManager().getKitArmor(newName), this.gambling.getKitManager().getKitStuff(newName));
                } else {
                    event.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onPreviewClic(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != null) {
            String inventoryName = event.getInventory().getName();
            String newName = inventoryName.replace(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-VIEW.INVENTORY-NAME")), "");
            if (this.gambling.getKitManager().getKitByName(newName) != null) {
                if (!event.getCurrentItem().hasItemMeta()) {
                    event.setCancelled(true);
                }

                if (event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getKitConfig().getString("KIT-VIEW.BACK.MATERIAL")) && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-VIEW.BACK.NAME")))) {
                    Inventory inventory = Bukkit.createInventory((InventoryHolder)null, this.gambling.getKitConfig().getInt("KIT-MENU.INVENTORY-SIZE"), ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.INVENTORY-NAME")));
                    this.gambling.getKitManager().openKitsMenu(inventory, player);
                }

                event.setCancelled(true);
            }

        }
    }

    public void openInventoryKit(String kitName, Player player, ItemStack[] armorContents, ItemStack[] contents) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)null, this.gambling.getKitConfig().getInt("KIT-VIEW.INVENTORY-SIZE"), ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-VIEW.INVENTORY-NAME") + kitName));
        ItemStack[] var6 = armorContents;
        int data = armorContents.length;

        int var8;
        for(var8 = 0; var8 < data; ++var8) {
            ItemStack items = var6[var8];
            inventory.addItem(new ItemStack[]{items});
        }

        int startPlace = 17;
        ItemStack[] var12 = contents;
        var8 = contents.length;

        for(int var14 = 0; var14 < var8; ++var14) {
            ItemStack items = var12[var14];
            ++startPlace;
            inventory.setItem(startPlace, items);
        }

        data = this.gambling.getKitConfig().getInt("KIT-VIEW.GLASS-DATA");
        String glassName = ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.GLASS-NAME"));
        Iterator var15 = this.gambling.getKitConfig().getStringList("KIT-VIEW.GLASS").iterator();

        while(var15.hasNext()) {
            String values = (String)var15.next();
            inventory.setItem(Integer.parseInt(values), (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)data)).setName(glassName).toItemStack());
        }

        inventory.setItem(17, (new ItemBuilder(Material.getMaterial(this.gambling.getKitConfig().getString("KIT-VIEW.BACK.MATERIAL")))).setName(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-VIEW.BACK.NAME"))).toItemStack());
        player.openInventory(inventory);
    }
}

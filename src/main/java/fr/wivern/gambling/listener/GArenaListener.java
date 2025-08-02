package fr.wivern.gambling.listener;

import fr.wivern.gambling.Gambling;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GArenaListener implements Listener {
    private Gambling gambling;

    public GArenaListener(Gambling gambling) {
        this.gambling = gambling;
    }

    @EventHandler
    public void onClicArena(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != null) {
            Player player = (Player)event.getWhoClicked();
            if (event.getInventory().getName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', this.gambling.getInventoryManager().inventoryName(this.gambling.getArenaConfig().getString("ARENA-MENU.INVENTORY-NAME"))))) {
                event.setCancelled(true);
                String location;
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getArenaConfig().getString("ARENA-MENU.POS.1.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.POS.1.NAME")))) {
                    location = round(player.getLocation().getX(), 2) + "," + round(player.getLocation().getY(), 2) + "," + round(player.getLocation().getZ(), 2) + "," + round((double)player.getLocation().getYaw(), 2) + "," + round((double)player.getLocation().getPitch(), 2);
                    this.gambling.getArenaConfig().set("ARENA.POS1", location);
                    this.gambling.getArenaConfig().set("ARENA.WORLD", player.getWorld().getName());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-SET-POS").replace("<pos>", "1")));
                    this.gambling.getArenaConfig().save("");
                    player.closeInventory();
                }

                if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.getMaterial(this.gambling.getArenaConfig().getString("ARENA-MENU.POS.2.MATERIAL")) && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.POS.2.NAME")))) {
                    location = round(player.getLocation().getX(), 2) + "," + round(player.getLocation().getY(), 2) + "," + round(player.getLocation().getZ(), 2) + "," + round((double)player.getLocation().getYaw(), 2) + "," + round((double)player.getLocation().getPitch(), 2);
                    this.gambling.getArenaConfig().set("ARENA.POS2", location);
                    this.gambling.getArenaConfig().set("ARENA.WORLD", player.getWorld().getName());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-SET-POS").replace("<pos>", "2")));
                    this.gambling.getArenaConfig().save("");
                    player.closeInventory();
                }
            }

        }
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        } else {
            BigDecimal bd = BigDecimal.valueOf(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }
}

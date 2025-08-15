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
        // Vérifications null de sécurité
        if (event.getCurrentItem() == null ||
                event.getCurrentItem().getType() == null ||
                event.getInventory() == null ||
                event.getInventory().getName() == null) {
            return;
        }

        Player player = (Player)event.getWhoClicked();
        String arenaMenuName = ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.INVENTORY-NAME"));

        if (!event.getInventory().getName().equalsIgnoreCase(arenaMenuName)) {
            return;
        }

        event.setCancelled(true);

        // Vérifications null pour les métadonnées
        if (!event.getCurrentItem().hasItemMeta() ||
                event.getCurrentItem().getItemMeta() == null ||
                event.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }

        Material pos1Material = Material.getMaterial(this.gambling.getArenaConfig().getString("ARENA-MENU.POS.1.MATERIAL"));
        String pos1Name = ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.POS.1.NAME"));
        Material pos2Material = Material.getMaterial(this.gambling.getArenaConfig().getString("ARENA-MENU.POS.2.MATERIAL"));
        String pos2Name = ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.POS.2.NAME"));

        String location;
        if (pos1Material != null && pos1Name != null &&
                event.getCurrentItem().getType() == pos1Material &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(pos1Name)) {

            location = round(player.getLocation().getX(), 2) + "," +
                    round(player.getLocation().getY(), 2) + "," +
                    round(player.getLocation().getZ(), 2) + "," +
                    round((double)player.getLocation().getYaw(), 2) + "," +
                    round((double)player.getLocation().getPitch(), 2);
            this.gambling.getArenaConfig().set("ARENA.POS1", location);
            this.gambling.getArenaConfig().set("ARENA.WORLD", player.getWorld().getName());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-SET-POS").replace("<pos>", "1")));
            this.gambling.getArenaConfig().save("");
            player.closeInventory();
        }

        if (pos2Material != null && pos2Name != null &&
                event.getCurrentItem().getType() == pos2Material &&
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(pos2Name)) {

            location = round(player.getLocation().getX(), 2) + "," +
                    round(player.getLocation().getY(), 2) + "," +
                    round(player.getLocation().getZ(), 2) + "," +
                    round((double)player.getLocation().getYaw(), 2) + "," +
                    round((double)player.getLocation().getPitch(), 2);
            this.gambling.getArenaConfig().set("ARENA.POS2", location);
            this.gambling.getArenaConfig().set("ARENA.WORLD", player.getWorld().getName());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-SET-POS").replace("<pos>", "2")));
            this.gambling.getArenaConfig().save("");
            player.closeInventory();
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
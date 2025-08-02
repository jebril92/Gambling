package fr.wivern.gambling.commands;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.util.command.ACommand;
import fr.wivern.gambling.util.item.ItemBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GArenaCommand extends ACommand {
    private Gambling gambling;

    public GArenaCommand(Gambling gambling) {
        super(gambling, "garena", "command.garena", false);
        this.gambling = gambling;
    }

    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player)sender;
        int size = this.gambling.getArenaConfig().getInt("ARENA-MENU.INVENTORY-SIZE");
        String name = ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.INVENTORY-NAME"));
        Inventory inventory = Bukkit.createInventory((InventoryHolder)null, size, name);
        int data = this.gambling.getArenaConfig().getInt("ARENA-MENU.GLASS-DATA");
        String glassName = ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.GLASS-NAME"));
        Iterator var9 = this.gambling.getArenaConfig().getStringList("ARENA-MENU.GLASS").iterator();

        while(var9.hasNext()) {
            String values = (String)var9.next();
            inventory.setItem(Integer.parseInt(values), (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)data)).setName(glassName).toItemStack());
        }

        List<String> pos1List = new ArrayList();
        List<String> pos2List = new ArrayList();
        String firstPos = this.gambling.getArenaConfig().getString("ARENA.POS1");
        String secondPos = this.gambling.getArenaConfig().getString("ARENA.POS2");
        this.gambling.getArenaConfig().getStringList("ARENA-MENU.POS.1.LORE").forEach((line) -> {
            pos1List.add(ChatColor.translateAlternateColorCodes('&', line.replace("<position>", firstPos)));
        });
        this.gambling.getArenaConfig().getStringList("ARENA-MENU.POS.2.LORE").forEach((line) -> {
            pos2List.add(ChatColor.translateAlternateColorCodes('&', line.replace("<position>", secondPos)));
        });
        ItemStack pos1 = (new ItemBuilder(Material.getMaterial(this.gambling.getArenaConfig().getString("ARENA-MENU.POS.1.MATERIAL")))).setName(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.POS.1.NAME"))).setLore((List)pos1List).toItemStack();
        ItemStack pos2 = (new ItemBuilder(Material.getMaterial(this.gambling.getArenaConfig().getString("ARENA-MENU.POS.2.MATERIAL")))).setName(ChatColor.translateAlternateColorCodes('&', this.gambling.getArenaConfig().getString("ARENA-MENU.POS.2.NAME"))).setLore((List)pos2List).toItemStack();
        inventory.setItem(this.gambling.getArenaConfig().getInt("ARENA-MENU.POS.1.SLOT"), pos1);
        inventory.setItem(this.gambling.getArenaConfig().getInt("ARENA-MENU.POS.2.SLOT"), pos2);
        player.openInventory(inventory);
        return true;
    }
}

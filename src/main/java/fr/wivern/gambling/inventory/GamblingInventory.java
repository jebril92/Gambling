package fr.wivern.gambling.inventory;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.util.item.ItemBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GamblingInventory {
    public void openMainInventory(Player player, Gambling gambling) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)null, gambling.getConfigManager().getInt("GAMBLING-MENU-MAIN.INVENTORY-SIZE"), gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.INVENTORY-NAME"));
        List<String> listGambling = new ArrayList(gambling.getConfigManager().getStringList("GAMBLING-MENU-MAIN.LIST.LORE"));
        List<String> listFight = new ArrayList(gambling.getConfigManager().getStringList("GAMBLING-MENU-MAIN.FIGHT.LORE"));
        int data = gambling.getConfigManager().getInt("GAMBLING-MENU-MAIN.GLASS-DATA");
        String glassName = gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.GLASS-NAME");
        Iterator var8 = gambling.getConfigManager().getStringList("GAMBLING-MENU-MAIN.GLASS").iterator();

        while(var8.hasNext()) {
            String values = (String)var8.next();
            inventory.setItem(Integer.parseInt(values), (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)data)).setName(glassName).toItemStack());
        }

        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-MENU-MAIN.LIST.SLOT"), (new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.LIST.MATERIAL")))).setName(gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.LIST.NAME")).setLore((List)listGambling).toItemStack());
        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-MENU-MAIN.FIGHT.SLOT"), (new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.FIGHT.MATERIAL")))).setName(gambling.getConfigManager().getString("GAMBLING-MENU-MAIN.FIGHT.NAME")).setLore((List)listFight).toItemStack());
        player.openInventory(inventory);
    }

    public void openFightMenu(Player player, Gambling gambling) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)null, gambling.getConfigManager().getInt("GAMBLING-CREATE.INVENTORY-SIZE"), gambling.getConfigManager().getString("GAMBLING-CREATE.INVENTORY-NAME"));
        int data = gambling.getConfigManager().getInt("GAMBLING-CREATE.GLASS-DATA");
        String glassName = gambling.getConfigManager().getString("GAMBLING-CREATE.GLASS-NAME");
        Iterator var6 = gambling.getConfigManager().getStringList("GAMBLING-CREATE.GLASS").iterator();

        while(var6.hasNext()) {
            String values = (String)var6.next();
            inventory.setItem(Integer.parseInt(values), (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)data)).setName(glassName).toItemStack());
        }

        List<String> loreMoney = new ArrayList(gambling.getConfigManager().getStringList("GAMBLING-CREATE.MONEY.LORE"));
        List<String> loreItem = new ArrayList(gambling.getConfigManager().getStringList("GAMBLING-CREATE.ITEM.LORE"));
        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-CREATE.MONEY.SLOT"), (new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-CREATE.MONEY.MATERIAL")))).setName(gambling.getConfigManager().getString("GAMBLING-CREATE.MONEY.NAME")).setLore((List)loreMoney).toItemStack());
        inventory.setItem(gambling.getConfigManager().getInt("GAMBLING-CREATE.ITEM.SLOT"), (new ItemBuilder(Material.getMaterial(gambling.getConfigManager().getString("GAMBLING-CREATE.ITEM.MATERIAL")))).setName(gambling.getConfigManager().getString("GAMBLING-CREATE.ITEM.NAME")).setLore((List)loreItem).toItemStack());
        player.openInventory(inventory);
    }
}

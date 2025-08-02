package fr.wivern.gambling.inventory;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.data.PlayerData;
import fr.wivern.gambling.util.item.ItemBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryManager {
    private Gambling gambling;

    public InventoryManager(Gambling gambling) {
        this.gambling = gambling;
    }

    private int inventorySize(int direction) {
        return direction;
    }

    public String inventoryName(String direction) {
        return direction;
    }

    private ItemStack itemConfig(List<String> lore, Material material, String name) {
        return (new ItemBuilder(material)).setName(name).setLore(lore).toItemStack();
    }

    private ItemStack glassItem(int direction, String name) {
        return (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)direction)).setName(name).toItemStack();
    }

    private int informationSlot(int slot) {
        return slot;
    }

    public void openInventory(Player player) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, this.inventorySize(this.gambling.getConfig().getInt("GAMBLING-MENU.INVENTORY-SIZE")), this.inventoryName(this.gambling.getConfigManager().getString("GAMBLING-MENU.INVENTORY-NAME")));
        (new BukkitRunnable() {
            public void run() {
                inventory.clear();
                Iterator var1 = InventoryManager.this.gambling.getConfigManager().getStringList("GAMBLING-MENU.GLASS").iterator();

                while(var1.hasNext()) {
                    String values = (String)var1.next();
                    inventory.setItem(Integer.parseInt(values), InventoryManager.this.glassItem(InventoryManager.this.gambling.getConfig().getInt("GAMBLING-MENU.GLASS-DATA"), InventoryManager.this.gambling.getConfigManager().getString("GAMBLING-MENU.GLASS-NAME")));
                }

                int slot = InventoryManager.this.gambling.getConfigManager().getInt("GAMBLING-MENU.KIT.SLOT");
                Material material = Material.getMaterial(InventoryManager.this.gambling.getConfigManager().getString("GAMBLING-MENU.KIT.MATERIAL"));
                String name = InventoryManager.this.gambling.getConfigManager().getString("GAMBLING-MENU.KIT.NAME");
                List<String> kitLore = new ArrayList(InventoryManager.this.gambling.getConfigManager().getStringList("GAMBLING-MENU.KIT.LORE"));
                inventory.setItem(slot, (new ItemBuilder(material)).setName(name).setLore((List)kitLore).toItemStack());
                InventoryManager.this.gambling.getMatchManager().getPlayerWaitMap().forEach((players, playerData) -> {
                    ItemStack itemStackSkull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
                    SkullMeta skullMeta = (SkullMeta)itemStackSkull.getItemMeta();
                    skullMeta.setDisplayName(InventoryManager.this.gambling.getConfigManager().getString("GAMBLING-MENU.SKULL.NAME-COLOR") + players.getName());
                    List<String> lore = new ArrayList();
                    InventoryManager.this.gambling.getConfigManager().getStringList("GAMBLING-MENU.SKULL.LORE").forEach((line) -> {
                        lore.add(line.replace("<kit>", playerData.getKitName()));
                    });
                    skullMeta.setLore(lore);
                    skullMeta.setOwner(players.getName());
                    itemStackSkull.setItemMeta(skullMeta);
                    inventory.addItem(new ItemStack[]{itemStackSkull});
                });
            }
        }).runTaskTimerAsynchronously(this.gambling, 1L, 5L);
        player.openInventory(inventory);
    }

    public void openViewInventory(final Player player, final Player target) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, this.inventorySize(this.gambling.getConfig().getInt("GAMBLING-VIEW.INVENTORY-SIZE")), this.inventoryName(this.gambling.getConfigManager().getString("GAMBLING-VIEW.INVENTORY-NAME")));
        (new BukkitRunnable() {
            public void run() {
                inventory.clear();
                if (!InventoryManager.this.gambling.getMatchManager().getPlayerWaitMap().containsKey(target)) {
                    player.closeInventory();
                    this.cancel();
                } else {
                    Iterator var1 = InventoryManager.this.gambling.getConfigManager().getStringList("GAMBLING-VIEW.GLASS").iterator();

                    while(var1.hasNext()) {
                        String values = (String)var1.next();
                        inventory.setItem(Integer.parseInt(values), InventoryManager.this.glassItem(InventoryManager.this.gambling.getConfig().getInt("GAMBLING-VIEW.GLASS-DATA"), InventoryManager.this.gambling.getConfigManager().getString("GAMBLING-VIEW.GLASS-NAME")));
                    }

                    List<String> l = InventoryManager.this.gambling.getConfigManager().getStringList("GAMBLING-VIEW.START.LORE");
                    List<String> lore = InventoryManager.this.gambling.getConfigManager().getStringList("GAMBLING-VIEW.PREVIEW.MONEY.LORE");
                    inventory.setItem(InventoryManager.this.informationSlot(InventoryManager.this.gambling.getConfig().getInt("GAMBLING-VIEW.START.SLOT")), InventoryManager.this.itemConfig(l, Material.getMaterial(InventoryManager.this.gambling.getConfig().getString("GAMBLING-VIEW.START.MATERIAL")), InventoryManager.this.gambling.getConfigManager().getString("GAMBLING-VIEW.START.PLAYER-NAME-COLOR") + target.getName()));
                    if (((PlayerData)InventoryManager.this.gambling.getMatchManager().getPlayerWaitMap().get(target)).isUseMoney()) {
                        inventory.setItem(InventoryManager.this.informationSlot(InventoryManager.this.gambling.getConfig().getInt("GAMBLING-VIEW.PREVIEW.MONEY.SLOT")), InventoryManager.this.itemConfig(lore, Material.getMaterial(InventoryManager.this.gambling.getConfig().getString("GAMBLING-VIEW.PREVIEW.MONEY.MATERIAL")), InventoryManager.this.gambling.getConfigManager().getString("GAMBLING-VIEW.PREVIEW.MONEY.NAME-COLOR") + ((PlayerData)InventoryManager.this.gambling.getMatchManager().getPlayerWaitMap().get(target)).getMoney()));
                    } else {
                        ItemStack itemStack = ((PlayerData)InventoryManager.this.gambling.getMatchManager().getPlayerWaitMap().get(target)).getItemStack();
                        inventory.setItem(InventoryManager.this.informationSlot(InventoryManager.this.gambling.getConfig().getInt("GAMBLING-VIEW.PREVIEW.MONEY.SLOT")), itemStack);
                    }

                }
            }
        }).runTaskTimerAsynchronously(this.gambling, 1L, 5L);
        player.openInventory(inventory);
    }

    public void openMainMenu(Player player) {
    }
}

package fr.wivern.gambling.kits;

import fr.wivern.gambling.Gambling;
import fr.wivern.gambling.util.config.Base64Save;
import fr.wivern.gambling.util.config.Config;
import fr.wivern.gambling.util.config.ItemSave;
import fr.wivern.gambling.util.item.ItemBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class KitManager {
    private Gambling gambling;
    private List<Kits> kits;

    public KitManager(Gambling gambling) {
        this.gambling = gambling;
        this.kits = new ArrayList();
        this.setupKits();
    }

    public void setupKits() {
        if (this.gambling.getKitStorage().listFiles() != null) {
            File[] files = this.gambling.getKitStorage().listFiles((cursor) -> {
                return cursor.getName().endsWith(".yml");
            });
            if (files != null) {
                this.kits = (List)Arrays.stream(files).map((file) -> {
                    Config config = new Config(this.gambling, file.getName(), "kits");

                    try {
                        ItemStack[] stuff = Base64Save.itemStackArrayFromBase64(config.getString("stuff"));
                        ItemStack[] armor = Base64Save.itemStackArrayFromBase64(config.getString("armor"));
                        ItemStack icon = config.getString("icon") == null ? new ItemStack(Material.REDSTONE_BLOCK) : ItemSave.deserializeItemStack(config.getString("icon"));
                        return new Kits(config.getFileName().split("\\.")[0], armor, stuff, icon, config);
                    } catch (IOException var6) {
                        var6.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList());
            }
        }
    }

    public Kits getKitByName(String kitName) {
        return this.kits.stream()
                .filter(s -> s.getKitName().equalsIgnoreCase(kitName))
                .findFirst()
                .orElse(null);
    }

    public void createKit(String kitName, Player player) {
        if (this.getKitByName(kitName) == null) {
            this.kits.add(new Kits(kitName, player.getInventory().getArmorContents(), player.getInventory().getContents(), new ItemStack(Material.REDSTONE_BLOCK), new Config(this.gambling, kitName, this.gambling.getKitStorage().getName())));
            player.sendMessage(this.gambling.getConfigManager().getString("KIT-CREATE"));
        } else {
            player.sendMessage(this.gambling.getConfigManager().getString("KIT-EXIST"));
        }

    }

    public void deleteKit(Player player, Kits kits) {
        kits.getConfig().deleteFile(kits.getKitName(), this.gambling.getKitStorage().getName());
        this.kits.remove(kits);
        player.sendMessage(this.gambling.getConfigManager().getString("KIT-DELETE"));
    }

    public void giveKit(String kitName, Player player) {
        Kits kits = this.getKitByName(kitName);
        player.getInventory().setArmorContents(kits.getArmor());
        player.getInventory().setContents(kits.getStuff());
        player.sendMessage(this.gambling.getConfigManager().getString("KIT-RECEIVE").replace("<kit>", kits.getKitName()));
    }

    public ItemStack[] getKitArmor(String kitName) {
        return this.getKitByName(kitName).getArmor();
    }

    public ItemStack[] getKitStuff(String kitName) {
        return this.getKitByName(kitName).getStuff();
    }

    public void editIcon(String kitName, Player player) {
        Kits kits = this.getKitByName(kitName);
        ItemStack itemStack = player.getItemInHand();
        if (itemStack != null && itemStack.getType() != null && itemStack.getType() != Material.AIR) {
            kits.setIcon(player.getItemInHand());
            player.sendMessage(this.gambling.getConfigManager().getString("KIT-ICON").replace("<kit>", kitName));
        } else {
            player.sendMessage(this.gambling.getConfigManager().getString("ITEM-HAND"));
        }
    }

    public ItemStack getIcon(String kitName) {
        return this.getKitByName(kitName).getIcon();
    }

    public void openKitsMenu(Inventory inventory, Player player) {
        int data = this.gambling.getKitConfig().getInt("KIT-MENU.GLASS-DATA");
        String name = this.gambling.getKitConfig().getString("KIT-MENU.GLASS-NAME");
        String glassName = ChatColor.translateAlternateColorCodes('&', name);
        int slot = this.gambling.getKitConfig().getInt("KIT-MENU.BACK.SLOT");
        Material material = Material.getMaterial(this.gambling.getKitConfig().getString("KIT-MENU.BACK.MATERIAL"));
        String nameBack = ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.BACK.NAME"));
        Iterator var9 = this.gambling.getKitConfig().getStringList("KIT-MENU.GLASS").iterator();

        while(var9.hasNext()) {
            String values = (String)var9.next();
            inventory.setItem(Integer.parseInt(values), (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)data)).setName(glassName).toItemStack());
        }

        inventory.setItem(slot, (new ItemBuilder(material)).setName(nameBack).toItemStack());
        this.kits.forEach((kits) -> {
            List<String> lore = new ArrayList();
            this.gambling.getKitConfig().getStringList("KIT-MENU.KIT.LORE").forEach((line) -> {
                lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("<kit>", kits.getKitName())));
            });
            inventory.addItem(new ItemStack[]{(new ItemBuilder(kits.getIcon())).setName(ChatColor.translateAlternateColorCodes('&', this.gambling.getKitConfig().getString("KIT-MENU.KIT.NAME") + kits.getKitName())).setLore((List)lore).toItemStack()});
        });
        player.openInventory(inventory);
    }

    public void openKitSelector(Player player) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)null, this.gambling.getConfigManager().getInt("KIT-SELECTOR.INVENTORY-SIZE"), this.gambling.getConfigManager().getString("KIT-SELECTOR.INVENTORY-NAME"));
        int data = this.gambling.getConfigManager().getInt("KIT-SELECTOR.GLASS-DATA");
        String name = this.gambling.getConfigManager().getString("KIT-SELECTOR.GLASS-NAME");
        String glassName = ChatColor.translateAlternateColorCodes('&', name);
        Iterator var6 = this.gambling.getConfigManager().getStringList("KIT-SELECTOR.GLASS").iterator();

        while(var6.hasNext()) {
            String values = (String)var6.next();
            inventory.setItem(Integer.parseInt(values), (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)data)).setName(glassName).toItemStack());
        }

        this.kits.forEach((kits) -> {
            List<String> lore = new ArrayList();
            this.gambling.getConfigManager().getStringList("KIT-SELECTOR.KIT.LORE").forEach((line) -> {
                lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("<kit>", kits.getKitName())));
            });
            inventory.addItem(new ItemStack[]{(new ItemBuilder(kits.getIcon())).setName(ChatColor.translateAlternateColorCodes('&', this.gambling.getConfigManager().getString("KIT-SELECTOR.KIT.NAME") + kits.getKitName())).setLore((List)lore).toItemStack()});
        });
        player.openInventory(inventory);
    }

    public void saveKits() {
        this.kits.forEach((kits) -> {
            Config config = kits.getConfig();
            config.set("armor", Base64Save.itemStackArrayToBase64(kits.getArmor()));
            config.set("stuff", Base64Save.itemStackArrayToBase64(kits.getStuff()));
            config.set("icon", ItemSave.serializeItemStack(kits.getIcon()));
            config.save(this.gambling.getKitStorage().getName());
        });
    }
}

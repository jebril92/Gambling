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
                    try {
                        Config config = new Config(this.gambling, file.getName(), "kits");
                        ItemStack[] stuff = Base64Save.itemStackArrayFromBase64(config.getString("stuff"));
                        ItemStack[] armor = Base64Save.itemStackArrayFromBase64(config.getString("armor"));
                        ItemStack icon = config.getString("icon") == null ? new ItemStack(Material.REDSTONE_BLOCK) : ItemSave.deserializeItemStack(config.getString("icon"));
                        return new Kits(config.getFileName().split("\\.")[0], armor, stuff, icon, config);
                    } catch (IOException var6) {
                        var6.printStackTrace();
                        return null;
                    }
                }).filter(kit -> kit != null).collect(Collectors.toList());
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
        try {
            if (this.getKitByName(kitName) == null) {
                // Créer la configuration du kit
                Config config = new Config(this.gambling, kitName, "kits");

                // Créer le kit
                Kits newKit = new Kits(kitName,
                        player.getInventory().getArmorContents(),
                        player.getInventory().getContents(),
                        new ItemStack(Material.REDSTONE_BLOCK),
                        config);

                // Ajouter à la liste
                this.kits.add(newKit);

                // SAUVEGARDER IMMÉDIATEMENT
                this.saveKit(newKit);

                String createMsg = this.gambling.getConfigManager().getString("KIT-CREATE");
                player.sendMessage(createMsg != null ? createMsg : "§aKit créé avec succès !");
                player.sendMessage("§eDEBUG - Kit '" + kitName + "' créé et sauvegardé");

            } else {
                String existMsg = this.gambling.getConfigManager().getString("KIT-EXIST");
                player.sendMessage(existMsg != null ? existMsg : "§cCe kit existe déjà !");
            }
        } catch (Exception e) {
            player.sendMessage("§cErreur lors de la création du kit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveKit(Kits kit) {
        try {
            Config config = kit.getConfig();
            config.set("armor", Base64Save.itemStackArrayToBase64(kit.getArmor()));
            config.set("stuff", Base64Save.itemStackArrayToBase64(kit.getStuff()));
            config.set("icon", ItemSave.serializeItemStack(kit.getIcon()));
            config.save("kits");

            System.out.println("DEBUG - Kit sauvegardé: " + kit.getKitName());
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du kit " + kit.getKitName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteKit(Player player, Kits kits) {
        try {
            kits.getConfig().deleteFile(kits.getKitName(), "kits");
            this.kits.remove(kits);
            String deleteMsg = this.gambling.getConfigManager().getString("KIT-DELETE");
            player.sendMessage(deleteMsg != null ? deleteMsg : "§cKit supprimé !");
        } catch (Exception e) {
            player.sendMessage("§cErreur lors de la suppression: " + e.getMessage());
        }
    }

    public void giveKit(String kitName, Player player) {
        try {
            Kits kits = this.getKitByName(kitName);
            if (kits != null) {
                player.getInventory().setArmorContents(kits.getArmor());
                player.getInventory().setContents(kits.getStuff());
                String receiveMsg = this.gambling.getConfigManager().getString("KIT-RECEIVE");
                if (receiveMsg != null) {
                    player.sendMessage(receiveMsg.replace("<kit>", kits.getKitName()));
                } else {
                    player.sendMessage("§eVous avez reçu le kit " + kits.getKitName());
                }
            }
        } catch (Exception e) {
            player.sendMessage("§cErreur lors de l'attribution du kit: " + e.getMessage());
        }
    }

    public ItemStack[] getKitArmor(String kitName) {
        Kits kit = this.getKitByName(kitName);
        return kit != null ? kit.getArmor() : null;
    }

    public ItemStack[] getKitStuff(String kitName) {
        Kits kit = this.getKitByName(kitName);
        return kit != null ? kit.getStuff() : null;
    }

    public void editIcon(String kitName, Player player) {
        try {
            Kits kits = this.getKitByName(kitName);
            if (kits != null) {
                ItemStack itemStack = player.getItemInHand();
                if (itemStack != null && itemStack.getType() != null && itemStack.getType() != Material.AIR) {
                    kits.setIcon(player.getItemInHand());
                    this.saveKit(kits); // Sauvegarder après modification
                    String iconMsg = this.gambling.getConfigManager().getString("KIT-ICON");
                    if (iconMsg != null) {
                        player.sendMessage(iconMsg.replace("<kit>", kitName));
                    } else {
                        player.sendMessage("§aIcône du kit " + kitName + " mise à jour !");
                    }
                } else {
                    String handMsg = this.gambling.getConfigManager().getString("ITEM-HAND");
                    player.sendMessage(handMsg != null ? handMsg : "§cVous devez tenir un item en main !");
                }
            }
        } catch (Exception e) {
            player.sendMessage("§cErreur lors de la modification de l'icône: " + e.getMessage());
        }
    }

    public ItemStack getIcon(String kitName) {
        Kits kit = this.getKitByName(kitName);
        return kit != null ? kit.getIcon() : new ItemStack(Material.REDSTONE_BLOCK);
    }

    public void openKitsMenu(Inventory inventory, Player player) {
        try {
            int data = this.gambling.getKitConfig().getInt("KIT-MENU.GLASS-DATA");
            String nameConfig = this.gambling.getKitConfig().getString("KIT-MENU.GLASS-NAME");
            String glassName = nameConfig != null ? ChatColor.translateAlternateColorCodes('&', nameConfig) : " ";
            int slot = this.gambling.getKitConfig().getInt("KIT-MENU.BACK.SLOT");
            String materialConfig = this.gambling.getKitConfig().getString("KIT-MENU.BACK.MATERIAL");
            Material material = materialConfig != null ? Material.getMaterial(materialConfig) : Material.BARRIER;
            String nameBackConfig = this.gambling.getKitConfig().getString("KIT-MENU.BACK.NAME");
            String nameBack = nameBackConfig != null ? ChatColor.translateAlternateColorCodes('&', nameBackConfig) : "§cRetour";

            Iterator var9 = this.gambling.getKitConfig().getStringList("KIT-MENU.GLASS").iterator();

            while(var9.hasNext()) {
                String values = (String)var9.next();
                try {
                    inventory.setItem(Integer.parseInt(values), (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)data)).setName(glassName).toItemStack());
                } catch (NumberFormatException e) {
                    // Ignorer les slots invalides
                }
            }

            if (material != null) {
                inventory.setItem(slot, (new ItemBuilder(material)).setName(nameBack).toItemStack());
            }

            this.kits.forEach((kits) -> {
                try {
                    List<String> lore = new ArrayList();
                    String kitNamePrefix = this.gambling.getKitConfig().getString("KIT-MENU.KIT.NAME");
                    kitNamePrefix = kitNamePrefix != null ? kitNamePrefix : "&f» &6";

                    this.gambling.getKitConfig().getStringList("KIT-MENU.KIT.LORE").forEach((line) -> {
                        lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("<kit>", kits.getKitName())));
                    });

                    ItemStack kitItem = (new ItemBuilder(kits.getIcon()))
                            .setName(ChatColor.translateAlternateColorCodes('&', kitNamePrefix + kits.getKitName()))
                            .setLore((List)lore)
                            .toItemStack();
                    inventory.addItem(new ItemStack[]{kitItem});
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'ajout du kit " + kits.getKitName() + " au menu: " + e.getMessage());
                }
            });

            player.openInventory(inventory);
        } catch (Exception e) {
            player.sendMessage("§cErreur lors de l'ouverture du menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openKitSelector(Player player) {
        try {
            Inventory inventory = Bukkit.createInventory((InventoryHolder)null,
                    this.gambling.getConfigManager().getInt("KIT-SELECTOR.INVENTORY-SIZE"),
                    this.gambling.getConfigManager().getString("KIT-SELECTOR.INVENTORY-NAME"));

            int data = this.gambling.getConfigManager().getInt("KIT-SELECTOR.GLASS-DATA");
            String nameConfig = this.gambling.getConfigManager().getString("KIT-SELECTOR.GLASS-NAME");
            String glassName = nameConfig != null ? ChatColor.translateAlternateColorCodes('&', nameConfig) : " ";

            Iterator var6 = this.gambling.getConfigManager().getStringList("KIT-SELECTOR.GLASS").iterator();

            while(var6.hasNext()) {
                String values = (String)var6.next();
                try {
                    inventory.setItem(Integer.parseInt(values), (new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte)data)).setName(glassName).toItemStack());
                } catch (NumberFormatException e) {
                    // Ignorer les slots invalides
                }
            }

            this.kits.forEach((kits) -> {
                try {
                    List<String> lore = new ArrayList();
                    String kitNamePrefix = this.gambling.getConfigManager().getString("KIT-SELECTOR.KIT.NAME");
                    kitNamePrefix = kitNamePrefix != null ? kitNamePrefix : "&f» &6";

                    this.gambling.getConfigManager().getStringList("KIT-SELECTOR.KIT.LORE").forEach((line) -> {
                        lore.add(ChatColor.translateAlternateColorCodes('&', line.replace("<kit>", kits.getKitName())));
                    });

                    ItemStack kitItem = (new ItemBuilder(kits.getIcon()))
                            .setName(ChatColor.translateAlternateColorCodes('&', kitNamePrefix + kits.getKitName()))
                            .setLore((List)lore)
                            .toItemStack();
                    inventory.addItem(new ItemStack[]{kitItem});
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'ajout du kit " + kits.getKitName() + " au sélecteur: " + e.getMessage());
                }
            });

            player.openInventory(inventory);
        } catch (Exception e) {
            player.sendMessage("§cErreur lors de l'ouverture du sélecteur: " + e.getMessage());
        }
    }

    public void saveKits() {
        this.kits.forEach((kits) -> {
            this.saveKit(kits);
        });
    }

    // Méthode ajoutée pour le debug
    public List<Kits> getKits() {
        return this.kits;
    }
}
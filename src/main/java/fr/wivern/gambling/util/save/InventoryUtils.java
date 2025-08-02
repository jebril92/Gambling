package fr.wivern.gambling.util.save;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryUtils {
    public static PlayerInv playerInventoryFromPlayer(Player player) {
        return new PlayerInv(player.getInventory().getContents(), player.getInventory().getArmorContents());
    }

    public static PlayerInv playerInventoryFromPlayer2(Player player) {
        PlayerInv inv = new PlayerInv();
        inv.setContents(player.getInventory().getContents());
        inv.setArmorContents(player.getInventory().getArmorContents());
        return inv;
    }

    public static String playerInvToString(PlayerInv inv) {
        if (inv == null) {
            return "null";
        } else {
            StringBuilder builder = new StringBuilder();
            ItemStack[] armor = inv.getArmorContents();

            int i;
            for(i = 0; i < armor.length; ++i) {
                if (i == 3) {
                    if (armor[i] == null) {
                        builder.append(itemStackToString(new ItemStack(Material.AIR)));
                    } else {
                        builder.append(itemStackToString(armor[3]));
                    }
                } else if (armor[i] == null) {
                    builder.append(itemStackToString(new ItemStack(Material.AIR))).append(";");
                } else {
                    builder.append(itemStackToString(armor[i])).append(";");
                }
            }

            builder.append("|");

            for(i = 0; i < inv.getContents().length; ++i) {
                builder.append(i).append("#").append(itemStackToString(inv.getContents()[i])).append(i == inv.getContents().length - 1 ? "" : ";");
            }

            return builder.toString();
        }
    }

    public static String playerInventoryToString(PlayerInventory inv) {
        StringBuilder builder = new StringBuilder();
        ItemStack[] armor = inv.getArmorContents();

        int i;
        for(i = 0; i < armor.length; ++i) {
            if (i == 3) {
                if (armor[i] == null) {
                    builder.append(itemStackToString(new ItemStack(Material.AIR)));
                } else {
                    builder.append(itemStackToString(armor[3]));
                }
            } else if (armor[i] == null) {
                builder.append(itemStackToString(new ItemStack(Material.AIR))).append(";");
            } else {
                builder.append(itemStackToString(armor[i])).append(";");
            }
        }

        builder.append("|");

        for(i = 0; i < inv.getContents().length; ++i) {
            builder.append(i).append("#").append(itemStackToString(inv.getContents()[i])).append(i == inv.getContents().length - 1 ? "" : ";");
        }

        return builder.toString();
    }

    public static PlayerInv playerInventoryFromString(String in) {
        if (in != null && !in.equals("unset") && !in.equals("null") && !in.equals("'null'")) {
            PlayerInv inv = new PlayerInv();
            String[] data = in.split("\\|");
            ItemStack[] armor = new ItemStack[data[0].split(";").length];

            for(int i = 0; i < data[0].split(";").length; ++i) {
                armor[i] = itemStackFromString(data[0].split(";")[i]);
            }

            inv.setArmorContents(armor);
            ItemStack[] contents = new ItemStack[data[1].split(";").length];
            String[] var5 = data[1].split(";");
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String s = var5[var7];
                int slot = Integer.parseInt(s.split("#")[0]);
                if (s.split("#").length == 1) {
                    contents[slot] = null;
                } else {
                    contents[slot] = itemStackFromString(s.split("#")[1]);
                }
            }

            inv.setContents(contents);
            return inv;
        } else {
            return null;
        }
    }

    public static String itemStackToString(ItemStack item) {
        StringBuilder builder = new StringBuilder();
        if (item != null) {
            String isType = String.valueOf(item.getType().getId());
            builder.append("t@").append(isType);
            String isAmount;
            if (item.getDurability() != 0) {
                isAmount = String.valueOf(item.getDurability());
                builder.append(":d@").append(isAmount);
            }

            if (item.getAmount() != 1) {
                isAmount = String.valueOf(item.getAmount());
                builder.append(":a@").append(isAmount);
            }

            Map<Enchantment, Integer> isEnch = item.getEnchantments();
            if (isEnch.size() > 0) {
                Iterator var4 = isEnch.entrySet().iterator();

                while(var4.hasNext()) {
                    Entry<Enchantment, Integer> ench = (Entry)var4.next();
                    builder.append(":e@").append(((Enchantment)ench.getKey()).getId()).append("@").append(ench.getValue());
                }
            }

            if (item.hasItemMeta()) {
                ItemMeta imeta = item.getItemMeta();
                if (imeta.hasDisplayName()) {
                    builder.append(":dn@").append(imeta.getDisplayName());
                }

                if (imeta.hasLore()) {
                    builder.append(":l@").append(imeta.getLore());
                }
            }
        }

        return builder.toString();
    }

    public static String inventoryToString(Inventory inv) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < inv.getContents().length; ++i) {
            builder.append(i).append("#").append(itemStackToString(inv.getContents()[i]));
            if (i != inv.getContents().length - 1) {
                builder.append(";");
            }
        }

        return builder.toString();
    }

    public static Inventory inventoryFromString(String in) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54);
        String[] split = in.split(";");
        String[] var4 = split;
        int var5 = split.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String s = var4[var6];
            String[] info = s.split("#");
            inv.setItem(Integer.parseInt(info[0]), info.length > 1 ? itemStackFromString(info[1]) : null);
        }

        return inv;
    }

    public static ItemStack itemStackFromString(String in) {
        ItemStack item = null;
        ItemMeta meta = null;
        if (in == "null") {
            return new ItemStack(Material.AIR);
        } else {
            String[] split = in.split(":");
            String[] var5 = split;
            int var6 = split.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String itemInfo = var5[var7];
                String[] itemAttribute = itemInfo.split("@");
                String s2 = itemAttribute[0];
                byte var12 = -1;
                switch(s2.hashCode()) {
                    case 97:
                        if (s2.equals("a")) {
                            var12 = 2;
                        }
                        break;
                    case 100:
                        if (s2.equals("d")) {
                            var12 = 1;
                        }
                        break;
                    case 101:
                        if (s2.equals("e")) {
                            var12 = 3;
                        }
                        break;
                    case 108:
                        if (s2.equals("l")) {
                            var12 = 5;
                        }
                        break;
                    case 116:
                        if (s2.equals("t")) {
                            var12 = 0;
                        }
                        break;
                    case 3210:
                        if (s2.equals("dn")) {
                            var12 = 4;
                        }
                }

                switch(var12) {
                    case 0:
                        item = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                        meta = item.getItemMeta();
                        break;
                    case 1:
                        if (item != null) {
                            item.setDurability(Short.valueOf(itemAttribute[1]));
                        }
                        break;
                    case 2:
                        if (item != null) {
                            item.setAmount(Integer.valueOf(itemAttribute[1]));
                        }
                        break;
                    case 3:
                        if (item != null) {
                            item.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                        }
                        break;
                    case 4:
                        if (meta != null) {
                            meta.setDisplayName(itemAttribute[1]);
                        }
                        break;
                    case 5:
                        itemAttribute[1] = itemAttribute[1].replace("[", "");
                        itemAttribute[1] = itemAttribute[1].replace("]", "");
                        List<String> lore = Arrays.asList(itemAttribute[1].split(","));

                        for(int x = 0; x < lore.size(); ++x) {
                            String s = (String)lore.get(x);
                            if (s != null && s.toCharArray().length != 0) {
                                if (s.charAt(0) == ' ') {
                                    s = s.replaceFirst(" ", "");
                                }

                                lore.set(x, s);
                            }
                        }

                        if (meta != null) {
                            meta.setLore(lore);
                        }
                }
            }

            if (meta != null && (meta.hasDisplayName() || meta.hasLore())) {
                item.setItemMeta(meta);
            }

            return item;
        }
    }

    public static String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        if (minutes == 0) {
            return seconds + " secondes";
        } else {
            seconds %= 60;
            return minutes + " minutes and " + seconds + " seconds";
        }
    }

    public static void listFields() {
        try {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            Class craftItemStack = Class.forName(name + ".inventory.CraftItemStack");
            Method[] var2 = craftItemStack.getDeclaredMethods();
            int var3 = var2.length;

            int var4;
            for(var4 = 0; var4 < var3; ++var4) {
                Method m = var2[var4];
                Bukkit.getLogger().severe(m.getName());
                Parameter[] var6 = m.getParameters();
                int var7 = var6.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    Parameter p = var6[var8];
                    Bukkit.getLogger().severe(m.getName() + " " + p.getName() + " " + p.getType().getName());
                }
            }

            Field[] var11 = craftItemStack.getDeclaredFields();
            var3 = var11.length;

            for(var4 = 0; var4 < var3; ++var4) {
                Field f = var11[var4];
                Bukkit.getLogger().severe(f.getType().getCanonicalName() + " " + f.getName());
            }
        } catch (ClassNotFoundException var10) {
            var10.printStackTrace();
        }

    }

    public static String getFriendlyItemName(ItemStack is) {
        String pckg = Bukkit.getServer().getClass().getPackage().getName();

        try {
            Class craftItemStack = Class.forName(pckg + ".inventory.CraftItemStack");
            Method m = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
            Object o = m.invoke(craftItemStack, is);
            Method nm = o.getClass().getDeclaredMethod("getName");
            Object om = nm.invoke(o, (Object[])null);
            return (String)om;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException var7) {
            var7.printStackTrace();
            return null;
        }
    }
}

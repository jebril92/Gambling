package fr.wivern.gambling.kits;

import fr.wivern.gambling.util.config.Config;
import java.util.Arrays;
import org.bukkit.inventory.ItemStack;

public class Kits {
    private String kitName;
    private ItemStack[] armor;
    private ItemStack[] stuff;
    private ItemStack icon;
    private final transient Config config;

    public String toString() {
        return "Kits{kitName='" + this.kitName + '\'' + ", armor=" + Arrays.toString(this.armor) + ", stuff=" + Arrays.toString(this.stuff) + ", icon=" + this.icon + ", config=" + this.config + '}';
    }

    public Kits(String kitName, ItemStack[] armor, ItemStack[] stuff, ItemStack icon, Config file) {
        this.kitName = kitName;
        this.armor = armor;
        this.stuff = stuff;
        this.icon = icon;
        this.config = file;
    }

    public String getKitName() {
        return this.kitName;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack[] getStuff() {
        return this.stuff;
    }

    public void setStuff(ItemStack[] stuff) {
        this.stuff = stuff;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public Config getConfig() {
        return this.config;
    }
}

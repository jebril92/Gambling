package fr.wivern.gambling.util.save;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInv {
    private ItemStack[] contents;
    private ItemStack[] armorContents;

    public PlayerInv() {
    }

    public PlayerInv(ItemStack[] contents, ItemStack[] armorContents) {
        this.contents = contents;
        this.armorContents = armorContents;
    }

    public static PlayerInv fromPlayerInventory(PlayerInventory inv) {
        return new PlayerInv(inv.getContents(), inv.getArmorContents());
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public ItemStack[] getArmorContents() {
        return this.armorContents;
    }

    public void setArmorContents(ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }

    public ItemStack getHelmet() {
        return this.armorContents[0];
    }

    public ItemStack getChestPiece() {
        return this.armorContents[1];
    }

    public ItemStack getLeggings() {
        return this.armorContents[2];
    }

    public ItemStack getBoots() {
        return this.armorContents[3];
    }

    public ItemStack getSword() {
        return this.contents[0];
    }
}
